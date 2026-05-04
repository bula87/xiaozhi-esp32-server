import time
import json
import opuslib_next
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from core.connection import ConnectionHandler

from config.manage_api_client import report as manage_report

TAG = __name__


async def report(conn: "ConnectionHandler", type, text, opus_data, report_time):
    """Execute chat record reporting operation

    Args:
        conn: Connection object
        type: Reporting type, 1 is user, 2 is agent, 3 is tool call
        text: Synthesized text
        opus_data: opus audio data
        report_time: report time
    """
    try:
        if opus_data:
            audio_data = opus_to_wav(conn, opus_data)
        else:
            audio_data = None
        # Execute asynchronous report
        await manage_report(
            mac_address=conn.device_id,
            session_id=conn.session_id,
            chat_type=type,
            content=text,
            audio=audio_data,
            report_time=report_time,
        )
    except Exception as e:
        conn.logger.bind(tag=TAG).error(f"Chat record report failed: {e}")


def opus_to_wav(conn: "ConnectionHandler", opus_data):
    """convert Opus data to WAV format byte stream

    Args:
        output_dir: output directory (retain parameters to maintain interface compatibility)
        opus_data: opus audio data

    Returns:
        bytes: WAV format audio data
    """
    decoder = None
    try:
        decoder = opuslib_next.Decoder(16000, 1)  # 16kHz, Mono
        pcm_data = []

        for opus_packet in opus_data:
            try:
                pcm_frame = decoder.decode(opus_packet, 960)  # 960 samples = 60ms
                pcm_data.append(pcm_frame)
            except opuslib_next.OpusError as e:
                conn.logger.bind(tag=TAG).error(
                    f"Opus decoding error: {e}", exc_info=True
                )

        if not pcm_data:
            raise ValueError("No valid PCM data")

        # Create WAV file header
        pcm_data_bytes = b"".join(pcm_data)

        # WAV file header
        wav_header = bytearray()
        wav_header.extend(b"RIFF")  # ChunkID
        wav_header.extend((36 + len(pcm_data_bytes)).to_bytes(4, "little"))  # ChunkSize
        wav_header.extend(b"WAVE")  # Format
        wav_header.extend(b"fmt ")  # Subchunk1ID
        wav_header.extend((16).to_bytes(4, "little"))  # Subchunk1Size
        wav_header.extend((1).to_bytes(2, "little"))  # AudioFormat (PCM)
        wav_header.extend((1).to_bytes(2, "little"))  # NumChannels
        wav_header.extend((16000).to_bytes(4, "little"))  # SampleRate
        wav_header.extend((32000).to_bytes(4, "little"))  # ByteRate
        wav_header.extend((2).to_bytes(2, "little"))  # BlockAlign
        wav_header.extend((16).to_bytes(2, "little"))  # BitsPerSample
        wav_header.extend(b"data")  # Subchunk2ID
        wav_header.extend(len(pcm_data_bytes).to_bytes(4, "little"))  # Subchunk2Size

        # Return the complete WAV data
        return bytes(wav_header) + pcm_data_bytes
    finally:
        if decoder is not None:
            try:
                del decoder
            except Exception as e:
                conn.logger.bind(tag=TAG).debug(
                    f"Error releasing decoder resource: {e}"
                )


def enqueue_tts_report(conn: "ConnectionHandler", text, opus_data):
    """Add TTS data to the report queue

    Args:
        conn: connection object
        text: synthesized text
        opus_data: opus audio data
    """
    if not conn.read_config_from_api or conn.need_bind or not conn.report_tts_enable:
        return
    if conn.chat_history_conf == 0:
        return
    try:
        # Use the connection object's queue, pass text and binary data instead of file path
        if conn.chat_history_conf == 2:
            conn.report_queue.put((2, text, opus_data, int(time.time() * 1000)))
            conn.logger.bind(tag=TAG).debug(
                f"TTS data added to reporting queue: {conn.device_id}, Audio size: {len(opus_data)} "
            )
        else:
            conn.report_queue.put((2, text, None, int(time.time() * 1000)))
            conn.logger.bind(tag=TAG).debug(
                f"TTS data added to reporting queue: {conn.device_id}, No audio reported"
            )
    except Exception as e:
        conn.logger.bind(tag=TAG).error(
            f"Failed to add to TTS reporting queue: {text}, {e}"
        )


def enqueue_tool_report(
    conn: "ConnectionHandler",
    tool_name: str,
    tool_input: dict,
    tool_result: str = None,
    report_tool_call: bool = True,
):
    """Add tool call data to the reporting queue

    Args:
        conn: connection object
        tool_name: tool name
        tool_input: tool input parameters
        tool_result: tool execution result (optional)
        report_tool_call: whether to report the tool call itself, default True; set to False when reporting only the result
    """
    if not conn.read_config_from_api or conn.need_bind:
        return
    if conn.chat_history_conf == 0:
        return

    try:
        timestamp = int(time.time() * 1000)

        # Construct tool call content
        if report_tool_call:
            tool_text = json.dumps(
                [
                    {
                        "type": "tool",
                        "text": f"{tool_name}({json.dumps(tool_input, ensure_ascii=False)})",
                    }
                ]
            )
            conn.report_queue.put((3, tool_text, None, timestamp))

        # Construct tool result content
        if tool_result:
            result_display = f'{{"result":"{str(tool_result)}"}}'
            result_content = json.dumps(
                [{"type": "tool_result", "text": result_display}], ensure_ascii=False
            )
            conn.report_queue.put((3, result_content, None, timestamp + 1))
    except Exception as e:
        conn.logger.bind(tag=TAG).error(f"Failed to add to tool report queue: {e}")


def enqueue_asr_report(conn: "ConnectionHandler", text, opus_data):
    """Add ASR data to the report queue

    Args:
        conn: connection object
        text: synthesized text
        opus_data: opus audio data
    """
    if not conn.read_config_from_api or conn.need_bind or not conn.report_asr_enable:
        return
    if conn.chat_history_conf == 0:
        return
    # Use the connection object's queue, pass text and binary data instead of file path
    try:
        if conn.chat_history_conf == 2:
            conn.report_queue.put((1, text, opus_data, int(time.time() * 1000)))
            conn.logger.bind(tag=TAG).debug(
                f"ASR data has been added to the reporting queue: {conn.device_id}, audio size: {len(opus_data)} "
            )
        else:
            conn.report_queue.put((1, text, None, int(time.time() * 1000)))
            conn.logger.bind(tag=TAG).debug(
                f"ASR data has been added to the reporting queue: {conn.device_id}, no audio reported"
            )
    except Exception as e:
        conn.logger.bind(tag=TAG).debug(
            f"Failed to join ASR reporting queue: {text}, {e}"
        )
