import time
import json
import asyncio
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from core.connection import ConnectionHandler
from core.utils.util import audio_to_data
from core.handle.abortHandle import handleAbortMessage
from core.handle.intentHandler import handle_user_intent
from core.utils.output_counter import check_device_output_limit
from core.handle.sendAudioHandle import send_stt_message, SentenceType

TAG = __name__


async def handleAudioPassthrough(conn: "ConnectionHandler", audio_data):
    """
    Handle audio data directly by sending it to LLM without ASR processing
    """
    # Save audio data to a temporary file
    import tempfile
    import os

    # Create a temporary file for the audio data
    with tempfile.NamedTemporaryFile(suffix='.wav', delete=False) as temp_file:
        temp_file.write(audio_data)
        temp_file_path = temp_file.name

    try:
        # Check if the current LLM supports direct audio input
        if hasattr(conn.llm, 'supports_audio_input') and conn.llm.supports_audio_input():
            # Send audio directly to LLM
            try:
                # Call the LLM with the audio file
                response = await conn.llm.response_with_audio("", temp_file_path)
                # Process the response
                await startToChat(conn, response)
            except Exception as e:
                conn.logger.bind(tag=TAG).error(f"Error processing audio with LLM: {e}")
        else:
            # Fallback to normal ASR processing
            # This would typically be handled by the existing ASR system
            pass
    except Exception as e:
        # Clean up temporary file
        try:
            os.unlink(temp_file_path)
        except:
            pass
        raise e

    # Clean up temporary file
    try:
        os.unlink(temp_file_path)
    except:
        pass


async def handleAudioMessage(conn: "ConnectionHandler", audio):
    # Is there anyone speaking in the current segment
    have_voice = conn.vad.is_vad(conn, audio)
    # If the device was just woken up, briefly ignore VAD detection
    if hasattr(conn, "just_woken_up") and conn.just_woken_up:
        have_voice = False
        # Resume VAD detection after a short delay
        if not hasattr(conn, "vad_resume_task") or conn.vad_resume_task.done():
            conn.vad_resume_task = asyncio.create_task(resume_vad_detection(conn))
        return
    # Detect device long idle time, used for saying goodbye
    await no_voice_close_connect(conn, have_voice)

    # Check if we're in audio passthrough mode
    config = conn.config
    asr_module = config.get("selected_module", {}).get("ASR", "")
    audio_passthrough_enabled = asr_module == "Audio Passthru"

    # Handle audio passthrough mode
    if audio_passthrough_enabled:
        # Check if current LLM supports direct audio input
        llm_models = config.get("llm", {}).get("models", [])
        direct_audio_supported = any(model.get("supports_direct_audio_input", False) for model in llm_models)

        if direct_audio_supported:
            # Send audio directly to LLM
            await handleAudioPassthrough(conn, audio)
            return

    # Receive audio (existing ASR processing)
    await conn.asr.receive_audio(conn, audio, have_voice)


async def resume_vad_detection(conn: "ConnectionHandler"):
    # Wait 2 seconds and resume VAD detection
    await asyncio.sleep(2)
    conn.just_woken_up = False


async def startToChat(conn: "ConnectionHandler", text):
    # Check if the input is in JSON format (including speaker information)
    speaker_name = None
    actual_text = text

    try:
        # Try to parse the input in JSON format
        if text.strip().startswith("{") and text.strip().endswith("}"):
            data = json.loads(text)
            if "speaker" in data and "content" in data:
                speaker_name = data["speaker"]
                actual_text = data["content"]
                conn.logger.bind(tag=TAG).info(
                    f"Parsed speaker information: {speaker_name}"
                )
    except (json.JSONDecodeError, KeyError):
        # If parsing fails, continue using the original text
        pass

    # Save speaker information to the connection object
    if speaker_name:
        conn.current_speaker = speaker_name
    else:
        conn.current_speaker = None

    if conn.need_bind:
        await check_bind_device(conn)
        return

    # If the daily output character count is greater than the limit
    if conn.max_output_size > 0:
        if check_device_output_limit(
            conn.headers.get("device-id"), conn.max_output_size
        ):
            await max_out_size(conn)
            return

    # Do not interrupt the content that is currently playing in manual mode
    if conn.client_is_speaking and conn.client_listen_mode != "manual":
        await handleAbortMessage(conn)

    # First perform intent analysis, using actual text content
    intent_handled = await handle_user_intent(conn, actual_text)

    if intent_handled:
        # If intent has been handled, no longer chat
        return

    # If intent is not handled, continue regular chat flow, using actual text content
    await send_stt_message(conn, actual_text)

    # Prepare to start new session
    conn.client_abort = False

    conn.executor.submit(conn.chat, actual_text)


async def no_voice_close_connect(conn: "ConnectionHandler", have_voice):
    if have_voice:
        conn.last_activity_time = time.time() * 1000
        return
    # Only perform timeout check when the timestamp has been initialized
    if conn.last_activity_time > 0.0:
        no_voice_time = time.time() * 1000 - conn.last_activity_time
        close_connection_no_voice_time = int(
            conn.config.get("close_connection_no_voice_time", 120)
        )
        if (
            not conn.close_after_chat
            and no_voice_time > 1000 * close_connection_no_voice_time
        ):
            conn.close_after_chat = True
            conn.client_abort = False
            end_prompt = conn.config.get("end_prompt", {})
            if end_prompt and end_prompt.get("enable", True) is False:
                conn.logger.bind(tag=TAG).info(
                    "End conversation, no need to send ending prompt"
                )
                await conn.close()
                return
            prompt = end_prompt.get("prompt")
            if not prompt:
                prompt = "Please use the future of ```time passes so quickly```, and end this conversation with emotional, reluctant words.!"
            await startToChat(conn, prompt)


async def max_out_size(conn: "ConnectionHandler"):
    # Play the prompt for exceeding the maximum output word count
    conn.client_abort = False
    text = "I'm sorry, I have something to do right now, let's talk tomorrow at this time, deal! See you tomorrow, bye bye!"
    await send_stt_message(conn, text)
    file_path = "config/assets/max_output_size.wav"
    opus_packets = await audio_to_data(file_path)
    conn.tts.tts_audio_queue.put((SentenceType.LAST, opus_packets, text))
    conn.close_after_chat = True


async def check_bind_device(conn: "ConnectionHandler"):
    if conn.bind_code:
        # Ensure bind_code is a 6-digit number
        if len(conn.bind_code) != 6:
            conn.logger.bind(tag=TAG).error(
                f"Invalid binding code format: {conn.bind_code}"
            )
            text = "Binding code format error, please check configuration."
            await send_stt_message(conn, text)
            return

        text = f"Please log in to the control panel, enter{conn.bind_code}, and bind the device."
        await send_stt_message(conn, text)

        # Play prompt sound
        music_path = "config/assets/bind_code.wav"
        opus_packets = await audio_to_data(music_path)
        conn.tts.tts_audio_queue.put((SentenceType.FIRST, opus_packets, text))

        # Play numbers individually
        for i in range(6):  # Ensure only 6 digits are played
            try:
                digit = conn.bind_code[i]
                num_path = f"config/assets/bind_code/{digit}.wav"
                num_packets = await audio_to_data(num_path)
                conn.tts.tts_audio_queue.put((SentenceType.MIDDLE, num_packets, None))
            except Exception as e:
                conn.logger.bind(tag=TAG).error(f"Failed to play digit audio: {e}")
                continue
        conn.tts.tts_audio_queue.put((SentenceType.LAST, [], None))
    else:
        # Play unbound prompt
        conn.client_abort = False
        text = "Version information for this device was not found, please correctly configure the OTA address and recompile the firmware."
        music_path = "config/assets/bind_not_found.wav"
        opus_packets = await audio_to_data(music_path)
        conn.tts.tts_audio_queue.put((SentenceType.LAST, opus_packets, text))
