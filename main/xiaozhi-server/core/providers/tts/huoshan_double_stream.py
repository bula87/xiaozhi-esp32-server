import os
import uuid
import json
import queue
import asyncio
import traceback
import websockets

from typing import Callable, Any
from config.logger import setup_logging
from core.utils.util import check_model_key
from core.providers.tts.base import TTSProviderBase
from core.utils.tts import MarkdownCleaner, convert_percentage_to_range
from core.providers.tts.dto.dto import SentenceType, ContentType, InterfaceType


TAG = __name__
logger = setup_logging()

PROTOCOL_VERSION = 0b0001
DEFAULT_HEADER_SIZE = 0b0001

# Message Type:
FULL_CLIENT_REQUEST = 0b0001
AUDIO_ONLY_RESPONSE = 0b1011
FULL_SERVER_RESPONSE = 0b1001
ERROR_INFORMATION = 0b1111

# Message Type Specific Flags
MsgTypeFlagNoSeq = 0b0000  # Non-terminal packet with no sequence
MsgTypeFlagPositiveSeq = 0b1  # Non-terminal packet with sequence > 0
MsgTypeFlagLastNoSeq = 0b10  # last packet with no sequence
MsgTypeFlagNegativeSeq = 0b11  # Payload contains event number (int32)
MsgTypeFlagWithEvent = 0b100
# Message Serialization
NO_SERIALIZATION = 0b0000
JSON = 0b0001
# Message Compression
COMPRESSION_NO = 0b0000
COMPRESSION_GZIP = 0b0001

EVENT_NONE = 0
EVENT_Start_Connection = 1

EVENT_FinishConnection = 2

EVENT_ConnectionStarted = 50  # Successful connection establishment

EVENT_ConnectionFailed = 51  # Connection establishment failed (possibly unable to authenticate via permissions)

EVENT_ConnectionFinished = 52  # Connection finished

# Upstream Session events
EVENT_StartSession = 100
EVENT_CancelSession = 101
EVENT_FinishSession = 102
# Downstream Session events
EVENT_SessionStarted = 150
EVENT_SessionCanceled = 151
EVENT_SessionFinished = 152

EVENT_SessionFailed = 153

# Uplink General Events
EVENT_TaskRequest = 200

# Downlink TTS Events
EVENT_TTSSentenceStart = 350

EVENT_TTSSentenceEnd = 351

EVENT_TTSResponse = 352

class Header:
    def __init__(
        self,
        protocol_version=PROTOCOL_VERSION,
        header_size=DEFAULT_HEADER_SIZE,
        message_type: int = 0,
        message_type_specific_flags: int = 0,
        serial_method: int = NO_SERIALIZATION,
        compression_type: int = COMPRESSION_NO,
        reserved_data=0,
    ):
        self.header_size = header_size
        self.protocol_version = protocol_version
        self.message_type = message_type
        self.message_type_specific_flags = message_type_specific_flags
        self.serial_method = serial_method
        self.compression_type = compression_type
        self.reserved_data = reserved_data

    def as_bytes(self) -> bytes:
        return bytes(
            [
                (self.protocol_version << 4) | self.header_size,
                (self.message_type << 4) | self.message_type_specific_flags,
                (self.serial_method << 4) | self.compression_type,
                self.reserved_data,
            ]
        )


class Optional:
    def __init__(
        self, event: int = EVENT_NONE, sessionId: str = None, sequence: int = None
    ):
        self.event = event
        self.sessionId = sessionId
        self.errorCode: int = 0
        self.connectionId: str | None = None
        self.response_meta_json: str | None = None
        self.sequence = sequence

    # Convert to byte sequence
    def as_bytes(self) -> bytes:
        option_bytes = bytearray()
        if self.event != EVENT_NONE:
            option_bytes.extend(self.event.to_bytes(4, "big", signed=True))
        if self.sessionId is not None:
            session_id_bytes = str.encode(self.sessionId)
            size = len(session_id_bytes).to_bytes(4, "big", signed=True)
            option_bytes.extend(size)
            option_bytes.extend(session_id_bytes)
        if self.sequence is not None:
             option_bytes.extend(self.sequence.to_bytes(4, "big", signed=True))
        return option_bytes


class Response:
    def __init__(self, header: Header, optional: Optional):
        self.optional = optional
        self.header = header
        self.payload: bytes | None = None

    def __str__(self):
        return super().__str__()


class TTSProvider(TTSProviderBase):
    def __init__(self, config, delete_audio_file):
        super().__init__(config, delete_audio_file)
        self.ws = None
        self.interface_type = InterfaceType.DUAL_STREAM
        self._monitor_task = None  # monitoring task reference
        self.appId = config.get("appid")
        self.access_token = config.get("access_token")
        self.cluster = config.get("cluster")
        self.resource_id = config.get("resource_id")
        self.resource_type = True if self.resource_id == "seed-tts-2.0" else False
        self.report_on_last = self.resource_type
        self.activate_session = False
        if config.get("private_voice"):
            self.voice = config.get("private_voice")
        else:
             self.voice = config.get("speaker")

        # Default audio_params configuration
        default_audio_params = {
            "speech_rate": 0,
            "loudness_rate": 0
        }

        # Default additions configuration
        default_additions = {
            "aigc_metadata": {},
            "cache_config": {},
            "post_process": {
                 "pitch": 0
            }
        }

        # Default mix_speaker configuration
        default_mix_speaker = {}

        # Merge user configuration
        self.audio_params = {**default_audio_params, **config.get("audio_params", {})}
        self.additions = {**default_additions, **config.get("additions", {})}
        self.mix_speaker = {**default_mix_speaker, **config.get("mix_speaker", {})}

        # Apply percentage adjustment (if exists), otherwise use public configuration
        if "ttsVolume" in config:
            self.audio_params["loudness_rate"] = int(convert_percentage_to_range(
                 config["ttsVolume"], min_val=-50, max_val=100, base_val=0
            ))

        if "ttsRate" in config:
            self.audio_params["speech_rate"] = int(convert_percentage_to_range(
                config["ttsRate"], min_val=-50, max_val=100, base_val=0
            ))

        if "ttsPitch" in config:
            self.additions["post_process"]["pitch"] = int(convert_percentage_to_range(
                config["ttsPitch"], min_val=-12, max_val=12, base_val=0
            ))

        self.ws_url = config.get("ws_url")
        self.authorization = config.get("authorization")
        self.header = {"Authorization": f"{self.authorization}{self.access_token}"}
        enable_ws_reuse_value = config.get("enable_ws_reuse", True)
        self.enable_ws_reuse = False if str(enable_ws_reuse_value).lower() == 'false' else True
        self.tts_text = ""

        model_key_msg = check_model_key("TTS", self.access_token)
        if model_key_msg:
            logger.bind(tag=TAG).error(model_key_msg)

    async def open_audio_channels(self, conn):
        try:
            await super().open_audio_channels(conn)
            # Update the sample rate in audio_params to the actual conn.sample_rate
            self.audio_params["sample_rate"] = conn.sample_rate
        except Exception as e:
            logger.bind(tag=TAG).error(f"Failed to open audio channels: {str(e)}")
            self.ws = None
            raise

    async def _ensure_connection(self):
        """Establish a new WebSocket connection and start the listening task (only the first time)"""
        try:
            if self.ws:
                if self.enable_ws_reuse:
                    logger.bind(tag=TAG).debug(f"Using existing link...")
                    return self.ws
                else:
                    try:
                        await self.finish_connection()
                    except Exception:
                        pass
            logger.bind(tag=TAG).debug("Start establishing new connection...")
            ws_header = {
                "X-Api-App-Key": self.appId,
                "X-Api-Access-Key": self.access_token,
                "X-Api-Resource-Id": self.resource_id,
                "X-Api-Connect-Id": uuid.uuid4(),
            }
            self.ws = await websockets.connect(
                self.ws_url, additional_headers=ws_header, max_size=1000000000
            )
            logger.bind(tag=TAG).debug("WebSocket connection established successfully")
            
            # After connection is established successfully, start the monitoring task
            if self._monitor_task is None or self._monitor_task.done():
                logger.bind(tag=TAG).debug("Start monitoring task...")
                self._monitor_task = asyncio.create_task(self._start_monitor_tts_response())
            
            return self.ws
        except Exception as e:
            logger.bind(tag=TAG).error(f"Connection establishment failed: {str(e)}")
            self.ws = None
            raise
    
    async def finish_connection(self):
        """Send FinishConnection event, wait for server to return EVENT_ConnectionFinished"""
        try:
            if self.ws:
                logger.bind(tag=TAG).debug("Start closing connection...")
                header = Header(
                    message_type=FULL_CLIENT_REQUEST,
                    message_type_specific_flags=MsgTypeFlagWithEvent,
                    serial_method=JSON,
                ).as_bytes()
                optional = Optional(
                    event=EVENT_FinishConnection, sessionId=self.conn.sentence_id
                ).as_bytes()
                payload = str.encode("{}")
                await self.send_event(self.ws, header, optional, payload)
                logger.bind(tag=TAG).debug("Session end request has been sent")
        except Exception as e:
            logger.bind(tag=TAG).error(f"Failed to close session: {str(e)}")
            # Ensure resources are cleaned up
            await self.close()
            raise

    def tts_text_priority_thread(self):
        """Volcano Engine dual-stream TTS text processing thread"""
        while not self.conn.stop_event.is_set():
            try:
                message = self.tts_text_queue.get(timeout=1)

                if self.conn.client_abort:
                    try:
                        logger.bind(tag=TAG).info("Received interruption information, terminate TTS text processing thread")
                        if self.enable_ws_reuse:
                            asyncio.run_coroutine_threadsafe(
                               self.cancel_session(self.conn.sentence_id),
                               loop=self.conn.loop,
                            )
                        else:
                            asyncio.run_coroutine_threadsafe(
                               self.finish_connection(),
                               loop=self.conn.loop,
                            )
                        continue
                    except Exception as e:
                        logger.bind(tag=TAG).error(f"Failed to cancel TTS session: {str(e)}")
                        continue

                # Filter old messages: check if sentence_id matches
                if message.sentence_id != self.conn.sentence_id:
                    continue

                logger.bind(tag=TAG).debug(
                    f"Received TTS task | {message.sentence_type.name} | {message.content_type.name} | Session ID: {message.sentence_id}"
                )
                
                if message.sentence_type == SentenceType.FIRST:
                    # Initialize parameters
                    try:
                        if not getattr(self.conn, "sentence_id", None): 
                            self.conn.sentence_id = uuid.uuid4().hex
                        logger.bind(tag=TAG).debug(f"Automatically generate new session ID: {self.conn.sentence_id}")

                        logger.bind(tag=TAG).debug("Start the TTS session...")
                        future = asyncio.run_coroutine_threadsafe(
                            self.start_session(self.conn.sentence_id),
                            loop=self.conn.loop,
                        )
                        future.result(timeout=self.tts_timeout)
                        self.before_stop_play_files.clear()
                        logger.bind(tag=TAG).debug("TTS session started successfully")
                    except Exception as e:
                        logger.bind(tag=TAG).error(f"Failed to start TTS session: {str(e)}")
                        continue

                elif ContentType.TEXT == message.content_type:
                    if message.content_detail:
                        try:
                            logger.bind(tag=TAG).debug(
                               f"Starting to send TTS text: {message.content_detail}"
                            )
                            future = asyncio.run_coroutine_threadsafe(
                                self.text_to_speak(message.content_detail, None),
                                loop=self.conn.loop,
                            )
                            future.result(timeout=self.tts_timeout)
                        except Exception as e:
                            logger.bind(tag=TAG).error(f"Failed to send TTS text: {str(e)}")
                            if self.ws:
                                try:
                                    await self.ws.close()
                                except:
                                    pass
                               self.ws = None
                            continue
                elif ContentType.FILE == message.content_type:
                    logger.bind(tag=TAG).info(
                        f"Adding audio file to playlist: {message.content_file}"
                    )
                    if message.content_file and os.path.exists(message.content_file):
                        # Process file audio data first
                        self._process_audio_file_stream(message.content_file, callback=lambda audio_data: self.handle_audio_file(audio_data, message.content_detail))
                
                if message.sentence_type == SentenceType.LAST:
                    try:
                        logger.bind(tag=TAG).debug("Start and end TTS session...")
                        future = asyncio.run_coroutine_threadsafe(
                            self.finish_session(self.conn.sentence_id),
                            loop=self.conn.loop,
                        )
                        future.result(timeout=self.tts_timeout)
                    except Exception as e:
                        logger.bind(tag=TAG).error(f"Ending TTS session failed: {str(e)}")
                        continue
            except queue.Empty:
                continue
            except Exception as e:
                logger.bind(tag=TAG).error(
                    f"Failed to process TTS message: {str(e)}",
                    exc_info=True
                )
                continue

    async def text_to_speak(self, text: str, _):
        """Send text to TTS service"""
        try:
            # Establish new connection
            if self.ws is None:
                logger.bind(tag=TAG).warning(f"WebSocket connection does not exist, terminate sending text")
                return
 
            #  Filter Markdown
            filtered_text = MarkdownCleaner.clean_markdown(text)
            if self._correct_words_pattern:
                filtered_text = self._correct_words_pattern.sub(lambda m: self.correct_words[m.group(0)], filtered_text)

            if filtered_text:
                # Send text
                await self.send_text(self.voice, filtered_text, self.conn.sentence_id)
            return
        except Exception as e:
            logger.bind(tag=TAG).error(f"Failed to send TTS text: {str(e)}")
            if self.ws:
                try:
                    await self.ws.close()
                except:
                    pass
                self.ws = None

    async def start_session(self, session_id):
        logger.bind(tag=TAG).debug(f"Start session~~{session_id}")
        try:       
            # Close the previous connection and create a new link when the previous session is active
            if self.activate_session:
                await self.close()
            
            # Set session activation flag
            self.activate_session = True
            
            # Ensure connection is established
            await self._ensure_connection()
 
            header = Header(
                message_type=FULL_CLIENT_REQUEST,
                message_type_specific_flags=MsgTypeFlagWithEvent,
                serial_method=JSON,
            ).as_bytes()
            optional = Optional(
                event=EVENT_StartSession, sessionId=session_id
            ).as_bytes()
            payload = self.get_payload_bytes(
                event=EVENT_StartSession, speaker=self.voice
            )
            await self.send_event(self.ws, header, optional, payload)
            logger.bind(tag=TAG).debug("Session start request has been sent")
        except Exception as e:
            logger.bind(tag=TAG).error(f"Session start failed: {str(e)}")
            # Ensure resource cleanup
            await self.close()
            raise

    async def finish_session(self, session_id):
        logger.bind(tag=TAG).debug(f"Closing session~~{session_id}")
        try:
            if self.ws:
                header = Header(
                    message_type=FULL_CLIENT_REQUEST,
                    message_type_specific_flags=MsgTypeFlagWithEvent,
                    serial_method=JSON,
                ).as_bytes()
                optional = Optional(
                    event=EVENT_FinishSession, sessionId=session_id
                ).as_bytes()
                payload = str.encode("{}")
                await self.send_event(self.ws, header, optional, payload)
                logger.bind(tag=TAG).debug("Session end request has been sent")

        except Exception as e:
            logger.bind(tag=TAG).error(f"Failed to close session: {str(e)}")
            # Ensure resources are cleaned up
            await self.close()
            raise

    async def cancel_session(self,session_id):
        logger.bind(tag=TAG).debug(f"Cancel session, release server resources~~{session_id}")
        try:
             if self.ws:
                header = Header(
                    message_type=FULL_CLIENT_REQUEST,
                    message_type_specific_flags=MsgTypeFlagWithEvent,
                    serial_method=JSON,
                ).as_bytes()
                optional = Optional(
                    event=EVENT_CancelSession, sessionId=session_id
                ).as_bytes()
                payload = str.encode("{}")
                await self.send_event(self.ws, header, optional, payload)
                logger.bind(tag=TAG).debug("Session cancellation request sent")
        except Exception as e:
            logger.bind(tag=TAG).error(f"Session cancellation failed: {str(e)}")
            # Ensure resources are cleaned up
            await self.close()
            raise

    async def close(self):
        """Resource cleanup method"""
        await super().close()
        self.activate_session = False
        # Cancel monitoring task
        if self._monitor_task:
            try:
                self._monitor_task.cancel()
                await self._monitor_task
            except asyncio.CancelledError:
                pass
            except Exception as e:
                logger.bind(tag=TAG).warning(f"Error canceling monitoring task during close: {e}")
            self._monitor_task = None
        if self.ws:
            try:
                await self.ws.close()
            except:
                pass
            self.ws = None

    async def _start_monitor_tts_response(self):
        """Monitor TTS response - long running"""
        try:
            while not self.conn.stop_event.is_set():
                try:
                    # Ensure `recv()` runs in the same event loop
                    msg = await self.ws.recv()
                    res = self.parser_response(msg)
                    self.print_response(res, "send_text res:")

                    # Prioritize handling connection level events
                    if res.optional.event == EVENT_ConnectionFinished:
                         logger.bind(tag=TAG).debug(f"Connection closed successfully~~")
                        break

                    # Only process the response of the current active session
                    if res.optional.sessionId and self.conn.sentence_id != res.optional.sessionId:
                        # If it is a session termination related event, even if the session ID does not match, reset the state
                        if res.optional.event in [EVENT_SessionCanceled, EVENT_SessionFailed, EVENT_SessionFinished]:
                            logger.bind(tag=TAG).debug(f"Received remaining downlink end response, reset session state~~")
                            self.activate_session = False
                        continue
                     
                    if res.optional.event == EVENT_SessionCanceled:
                        logger.bind(tag=TAG).debug(f"Successfully released server resources~~")
                        self.activate_session = False
                    elif not self.resource_type and res.optional.event == EVENT_TTSSentenceStart:
                        json_data = json.loads(res.payload.decode("utf-8"))
                
                    self.tts_text = json_data.get("text", "")
                    logger.bind(tag=TAG).debug(f"Sentence voice generation started: {self.tts_text}")
                    self.tts_audio_queue.put(
                        (SentenceType.FIRST, [], self.tts_text)
                    )
                elif (
                    res.optional.event == EVENT_TTSResponse
                    and res.header.message_type == AUDIO_ONLY_RESPONSE
                ):
                    # Process seed-tts-2.0 text subtitles
                    if self.resource_type:
                        tts_text = self.get_tts_text(self.conn.sentence_id)
                        if tts_text:
                            logger.bind(tag=TAG).info(
                               f"Sentence voice generation successful: {tts_text}"
                            )
                            self.tts_audio_queue.put(
                               (SentenceType.FIRST, [], tts_text)
                            )
                            self.clear_tts_text(self.conn.sentence_id)
                            self.wav_to_opus_data_audio_raw_stream(res.payload, callback=self.handle_opus)
                elif res.optional.event == EVENT_SessionFinished:
                    logger.bind(tag=TAG).debug(f"Session finished~~")
                    self.activate_session = False
                    self._process_before_stop_play_files()
                    # In non-reuse mode, send FinishConnection after the session ends
                    if not self.enable_ws_reuse:
                        await self.finish_connection()
        except websockets.ConnectionClosed:
            logger.bind(tag=TAG).warning("WebSocket connection closed")
            break
        except Exception as e:
            logger.bind(tag=TAG).error(
                f"Error in _start_monitor_tts_response: {e}"
            )
            traceback.print_exc()
            # Close WebSocket when connection error occurs
            if self.ws:
                try:
                    await self.ws.close()
                except:
                    pass
                self.ws = None
        # Clean up references when monitoring task exits
        finally:
            self.activate_session = False
            self._monitor_task = None
