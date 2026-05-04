import os
import uuid
import json
import time
import queue
import asyncio
import traceback
import websockets

from asyncio import Task
from typing import Callable, Any
from config.logger import setup_logging
from core.utils.tts import MarkdownCleaner
from core.providers.tts.base import TTSProviderBase
from core.providers.tts.dto.dto import SentenceType, ContentType, InterfaceType

TAG = __name__
logger = setup_logging()


class TTSProvider(TTSProviderBase):
    TTS_PARAM_CONFIG = [
        ("ttsVolume", "volume", 0, 100, 50, int),
        ("ttsRate", "rate", 0.5, 2.0, 1.0, lambda v: round(v, 1)),
        ("ttsPitch", "pitch", 0.5, 2.0, 1.0, lambda v: round(v, 1)),
    ]

    def __init__(self, config, delete_audio_file):
        super().__init__(config, delete_audio_file)

        self.interface_type = InterfaceType.DUAL_STREAM
        # Basic configuration
        self.api_key = config.get("api_key")
        if not self.api_key:
            raise ValueError("api_key is required for CosyVoice TTS")
        self.report_on_last = True

        # WebSocket configuration
        self.ws_url = "wss://dashscope.aliyuncs.com/api-ws/v1/inference/"
        self.ws = None
        self._monitor_task = None
        self.activate_session = False
        self.last_active_time = None

        # Model and voice configuration
        self.model = config.get("model", "cosyvoice-v2")
        self.voice = config.get("voice", "longxiaochun_v2")  # Default voice
        if config.get("private_voice"):
            self.voice = config.get("private_voice")

        # Audio parameter configuration
        self.format = config.get("format", "pcm")

        volume = config.get("volume", "50")
        self.volume = int(volume) if volume else 50
 
        rate = config.get("rate", "1.0")
        self.rate = float(rate) if rate else 1.0

        pitch = config.get("pitch", "1.0")
        self.pitch = float(pitch) if pitch else 1.0

        # Apply percentage adjustments (if present), otherwise use public configuration
        self._apply_percentage_params(config)

        self.header = {
            "Authorization": f"Bearer {self.api_key}",
            # "user-agent": "your_platform_info", // Optional
            # "X-DashScope-WorkSpace": workspace, // Optional, Alibaba Cloud Bailian business space ID
            "X-DashScope-DataInspection": "enable",
        }

    async def _ensure_connection(self):
        """Ensure WebSocket connection is available, supports connection reuse within 60 seconds"""
        try:
            current_time = time.time()
            if self.ws and current_time - self.last_active_time < 60:
                # Link can only be reused for continuous conversation within one minute
                logger.bind(tag=TAG).debug(f"Using existing link...")
                return self.ws
            logger.bind(tag=TAG).debug("Establishing new connection...")

            self.ws = await websockets.connect(
                self.ws_url,
                additional_headers=self.header,
                ping_interval=30,
                ping_timeout=10,
                close_timeout=10,
            )

            logger.bind(tag=TAG).debug("WebSocket connection established successfully")
            self.last_active_time = current_time
            return self.ws
        except Exception as e:
            logger.bind(tag=TAG).error(f"Connection establishment failed: {str(e)}")
            self.ws = None
            self.last_active_time = None
            raise

    def tts_text_priority_thread(self):
        """Streaming TTS text processing thread"""
        while not self.conn.stop_event.is_set():
            try:
                message = self.tts_text_queue.get(timeout=1)

                if self.conn.client_abort:
                    try:
                        logger.bind(tag=TAG).info("Received abort information, terminate TTS text processing thread")
                        asyncio.run_coroutine_threadsafe(
                            self.finish_session(self.conn.sentence_id),
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
                    f"Received TTS task｜{message.sentence_type.name} ｜ {message.content_type.name} | Session ID: {message.sentence_id}"
                )

                if message.sentence_type == SentenceType.FIRST:
                    # Initialize session
                    try:
                        if not getattr(self.conn, "sentence_id", None):
                            self.conn.sentence_id = uuid.uuid4().hex
                        logger.bind(tag=TAG).debug(f"Automatically generate new Session ID: {self.conn.sentence_id}")
                    except Exception as e:
                        logger.bind(tag=TAG).error(f"Failed to generate Session ID: {str(e)}")
                        continue

                logger.bind(tag=TAG).debug("Start starting TTS session...")
                future = asyncio.run_coroutine_threadsafe(
                    self.start_session(self.conn.sentence_id),
                    loop=self.conn.loop,
                )
                
                try:
                    future.result(timeout=self.tts_timeout)
                    self.before_stop_play_files.clear()
                    logger.bind(tag=TAG).debug("TTS session started successfully")
                except Exception as e:
                    logger.bind(tag=TAG).error(f"Failed to start TTS session: {str(e)}")
                    continue

            except queue.Empty:
                continue
            except Exception as e:
                logger.bind(tag=TAG).error(f"Failed to process TTS text: {str(e)}, Type: {type(e).__name__}, Stack: {traceback.format_exc()}")
                continue

    async def text_to_speak(self, text, _):
        """Send text to TTS service for synthesis"""
        try:
            if self.ws is None:
                logger.bind(tag=TAG).warning("WebSocket connection does not exist, terminate sending text")
                return
            # Filter Markdown
            filtered_text = MarkdownCleaner.clean_markdown(text)
            if self._correct_words_pattern:
                filtered_text = self._correct_words_pattern.sub(lambda m: self.correct_words[m.group(0)], filtered_text)

            if filtered_text:
                # Send continue-task message
                continue_task_message = {
                    "header": {
                        "action": "continue-task",
                        "task_id": self.conn.sentence_id,
                        "streaming": "duplex",
                    },
                    "payload": {"input": {"text": filtered_text}},
                }

                await self.ws.send(json.dumps(continue_task_message))
                self.last_active_time = time.time()
            return
        except Exception as e:
            logger.bind(tag=TAG).error(f"Failed to send TTS text: {str(e)}")
            if self.ws:
                try:
                    await self.ws.close()
                except:
                    pass
                self.ws = None
            raise

    async def start_session(self, session_id):
        """Start TTS session"""
        logger.bind(tag=TAG).debug(f"Start session~~{session_id}")
        try:
            if self.activate_session:
                await self.close()

            # Set session activation flag
            self.activate_session = True

            # Ensure connection is available
            await self._ensure_connection()
 
            # Start monitoring task
            if self._monitor_task is None or self._monitor_task.done():
                logger.bind(tag=TAG).debug("Start monitoring task...")
                self._monitor_task = asyncio.create_task(self._start_monitor_tts_response())

            # Send run-task message to start session
            run_task_message = {
                "header": {
                    "action": "run-task",
                    "task_id": session_id,
                    "streaming": "duplex",
                },
                "payload": {
                    "task_group": "audio",
                    "task": "tts",
                    "function": "SpeechSynthesizer",
                    "model": self.model,
                    "parameters": {
                        "text_type": "PlainText",
                        "voice": self.voice,
                        "format": self.format,
                        "sample_rate": self.conn.sample_rate,
                        "volume": self.volume,
                        "rate": self.rate,
                        "pitch": self.pitch,
                    },
                    "input": {}
                },
            }

            await self.ws.send(json.dumps(run_task_message))
            self.last_active_time = time.time()
            logger.bind(tag=TAG).debug("Session start request has been sent")
        except Exception as e:
            logger.bind(tag=TAG).error(f"Session start failed: {str(e)}")
            await self.close()
            raise

    async def finish_session(self, session_id):
        """End TTS session"""
        logger.bind(tag=TAG).debug(f"Closing session~~{session_id}")
        try:
            if self.ws and session_id:
                # Send finish-task message
                finish_task_message = {
                    "header": {
                        "action": "finish-task",
                        "task_id": session_id,
                        "streaming": "duplex",
                    },
                    "payload": {
                        "input": {}
                    }
                }
 
                await self.ws.send(json.dumps(finish_task_message))
                self.last_active_time = time.time()

        except Exception as e:
            logger.bind(tag=TAG).error(f"session close failed: {str(e)}")
            await self.close()
            raise

    async def close(self):
        """clean up resources"""
        await super().close()
        self.activate_session = False
        # cancel monitoring task
        if self._monitor_task:
            try:
                self._monitor_task.cancel()
                await self._monitor_task
            except asyncio.CancelledError:
                pass
            except Exception as e:
                logger.bind(tag=TAG).warning(f"Error canceling monitoring task during close: {e}")
            self._monitor_task = None

        # Close WebSocket connection
        if self.ws:
            try:
                await self.ws.close()
            except:
                pass
            self.ws = None
            self.last_active_time = None
 
    async def _start_monitor_tts_response(self):
        """Monitor TTS response - long running"""
        try:
            while not self.conn.stop_event.is_set():
                try:
                    msg = await self.ws.recv()
                self.last_active_time = time.time()

                if isinstance(msg, str):  # JSON control message
                    try:
                        data = json.loads(msg)
                        header = data.get("header", {})
                        event = header.get("event")
                        task_id = header.get("task_id")

                        # Only process responses for the current active session
                        if task_id and self.conn.sentence_id != task_id:
                            continue

                        if event in ["task-finished", "task-failed"]:
                            logger.bind(tag=TAG).debug("Received downstream end response reset session status~~")
                            self.activate_session = False
                            if event == "task-finished":
                               logger.bind(tag=TAG).debug("TTS task finished~")
                               self._process_before_stop_play_files()
                            elif event == "task-failed":
                               error_code = header.get("error_code", "unknown")
                               error_message = header.get("error_message", "unknown error")
                               logger.bind(tag=TAG).error(
                                   f"TTS task failed: {error_code} - {error_message}"
                               )
                               break

                    elif isinstance(msg, (bytes, bytearray)):
                        self.opus_encoder.encode_pcm_to_opus_stream(
                            msg, False, callback=self.handle_opus
                        )
                    else:
                        # Handle unexpected message types if necessary, but for now, we just continue
                        pass

                else:
                    # Handle other message types (like task-started, result-generated)
                    if event == "task-started":
                        logger.bind(tag=TAG).debug("TTS task started successfully~")
                        self.tts_audio_queue.put((SentenceType.FIRST, [], None))
                    elif event == "result-generated":
                        # Send cached data
                        tts_text = self.get_tts_text(self.conn.sentence_id)
                        if tts_text:
                            logger.bind(tag=TAG).info(
                               f"Sentence voice generation successful: {tts_text}"
                            )
                            self.tts_audio_queue.put(
                               (SentenceType.FIRST, [], tts_text)
                            )
                            self.clear_tts_text(self.conn.sentence_id)

        except websockets.ConnectionClosed:
            logger.bind(tag=TAG).warning("WebSocket connection closed")
            break
        except Exception as e:
            logger.bind(tag=TAG).error(
                f"Error processing TTS response: {e}\n{traceback.format_exc()}"
            )
            break

        # Close WebSocket when connection exception occurs
        if self.ws:
            try:
                await self.ws.close()
            except:
                pass
            self.ws = None
            self.last_active_time = None
 
        # Clear references when monitoring task exits
        finally:
            self.activate_session = False
            self._monitor_task = None
 
    def audio_to_opus_data_stream(
        self, audio_file_path, callback: Callable[[Any], Any] = None
    ):
        """Override parent method: use an independent temporary encoder to process the audio file, avoid concurrency conflicts with the TTS streaming encoder.
        In dual-stream TTS, the monitor task receives TTS audio in the event loop thread and encodes it using self.opus_encoder,
        at the same time, tts_text_priority_thread also uses self.opus_encoder to process music files,
        The shared encoder.buffer is not thread-safe, concurrent access will lead to SILK resampler assertion failure.
        """
        from core.utils.util import audio_to_data_stream

        return audio_to_data_stream(
            audio_file_path,
            is_opus=True,
            callback=callback,
            sample_rate=self.conn.sample_rate,
            opus_encoder=None,
        )

    def to_tts(self, text: str) -> list:
        """Non-streaming generation of audio data, used for generating audio and testing scenarios"""
        try:
            # Create event loop
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)

            # Generate session ID
            session_id = uuid.uuid4().hex
            # Store audio data
            audio_data = []
            
            async def _generate_audio():
                ws = await websockets.connect(
                    self.ws_url,
                    additional_headers=self.header,
                    ping_interval=30,
                    ping_timeout=10,
                    close_timeout=10,
                    max_size=10 * 1024 * 1024,
                )

                try:
                    # send run-task message to start session
                    run_task_message = {
                        "header": {
                            "action": "run-task",
                            "task_id": session_id,
                            "streaming": "duplex",
                        },
                        "payload": {
                            "task_group": "audio",
                            "task": "tts",
                            "function": "SpeechSynthesizer",
                            "model": self.model,
                            "parameters": {
                                "text_type": "PlainText",
                                "voice": self.voice,
                                "format": self.format,
                                "sample_rate": self.conn.sample_rate,
                                "volume": self.volume,
                                "rate": self.rate,
                                "pitch": self.pitch,
                            },
                            "input": {}
                        },
                    }
                    await ws.send(json.dumps(run_task_message))

                    # Wait for task to start
                    task_started = False
                    while not task_started:
                        msg = await ws.recv()
                        if isinstance(msg, str):
                            data = json.loads(msg)
                            header = data.get("header", {})
                            if header.get("event") == "task-started":
                                task_started = True
                                logger.bind(tag=TAG).debug("TTS task has started")
                            elif header.get("event") == "task-failed":
                                error_code = header.get("error_code", "unknown")
                                error_message = header.get("error_message", "unknown error")
                                raise Exception(
                                    f"Task failed to start: {error_code} - {error_message}"
                                )

                    # Send text
                    filtered_text = MarkdownCleaner.clean_markdown(text)
                    if self._correct_words_pattern:
                        filtered_text = self._correct_words_pattern.sub(lambda m: self.correct_words[m.group(0)], filtered_text)
                    
                    # Send continue-task message
                    continue_task_message = {
                        "header": {
                            "action": "continue-task",
                            "task_id": session_id,
                            "streaming": "duplex",
                        },
                        "payload": {"input": {"text": filtered_text}},
                    }
                    await ws.send(json.dumps(continue_task_message))

                    # Send finish-task message
                    finish_task_message = {
                        "header": {
                            "action": "finish-task",
                            "task_id": session_id,
                            "streaming": "duplex",
                        },
                        "payload": {
                            "input": {}
                        }
                    }
                    await ws.send(json.dumps(finish_task_message))

                    # Receive audio data
                    task_finished = False
                    while not task_finished:
                        msg = await ws.recv()
                        if isinstance(msg, (bytes, bytearray)):
                            self.opus_encoder.encode_pcm_to_opus_stream(
                                msg,
                                end_of_stream=False,
                                callback=lambda opus: audio_data.append(opus)
                            )
                        elif isinstance(msg, str):
                            data = json.loads(msg)
                            header = data.get("header", {})
                            if header.get("event") == "task-finished":
                                task_finished = True
                                logger.bind(tag=TAG).debug("TTS task finished")
                            elif header.get("event") == "task-failed":
                                error_code = header.get("error_code", "unknown")
                                error_message = header.get("error_message", "unknown error")
                                raise Exception(
                                    f"synthesis failed: {error_code} - {error_message}"
                                )

                finally:
                    # clean up resources
                    try:
                        await ws.close()
                    except:
                        pass

            # run asynchronous task
            loop.run_until_complete(_generate_audio())
            loop.close()

            return audio_data

        except Exception as e:
            logger.bind(tag=TAG).error(f"failed to generate audio data: {str(e)}")
            return []
