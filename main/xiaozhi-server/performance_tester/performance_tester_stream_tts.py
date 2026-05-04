import asyncio
import time
import json
import uuid
import websockets
from config.settings import load_config

description = "Stream TTS Speech Synthesis First Word Latency Test"


class StreamTTSPerformanceTester:
    def __init__(self):
        self.config = load_config()
        self.test_texts = ["Hello, this is a sentence."]
        self.results = []

    async def test_aliyun_tts(self, text=None, test_count=5):
        """Test Aliyun Stream TTS First Word Latency (Test multiple times and take the average)"""
        text = text or self.test_texts[0]
        latencies = []

        for i in range(test_count):
            try:
                tts_config = self.config["TTS"]["AliyunStreamTTS"]
                appkey = tts_config["appkey"]
                token = tts_config["token"]
                voice = tts_config["voice"]
                host = tts_config["host"]
                ws_url = f"wss://{host}/ws/v1"

                # Unified timing starting point: Start timing before establishing the connection
                start_time = time.time()
                async with websockets.connect(
                    ws_url, extra_headers={"X-NLS-Token": token}
                ) as ws:
                    task_id = str(uuid.uuid4())
                    message_id = str(uuid.uuid4())

                    start_request = {
                        "header": {
                            "message_id": message_id,
                            "task_id": task_id,
                            "namespace": "FlowingSpeechSynthesizer",
                            "name": "StartSynthesis",
                            "appkey": appkey,
                        },
                        "payload": {
                            "voice": voice,
                            "format": "pcm",
                            "sample_rate": 16000,
                            "volume": 50,
                            "speech_rate": 0,
                            "pitch_rate": 0,
                            "enable_subtitle": True,
                        },
                    }
                    await ws.send(json.dumps(start_request))

                    start_response = json.loads(await ws.recv())
                    if (
                        start_response.get("header", {}).get("name")
                        != "SynthesisStarted"
                    ):
                        raise Exception("Failed to start synthesis")

                    run_request = {
                        "header": {
                            "message_id": str(uuid.uuid4()),
                            "task_id": task_id,
                            "namespace": "FlowingSpeechSynthesizer",
                            "name": "RunSynthesis",
                            "appkey": appkey,
                        },
                        "payload": {"text": text},
                    }
                    await ws.send(json.dumps(run_request))

                    # Wait for the audio response
                    while True:
                        response = await ws.recv()
                        if isinstance(response, bytes):
                            latency = time.time() - start_time
                            print(
                               f"[Alibaba Cloud TTS] The {i + 1}th first word latency: {latency:.3f}s"
                            )
                            latencies.append(latency)
                            break
                        elif isinstance(response, str):
                            data = json.loads(response)
                            if data.get("header", {}).get("name") == "TaskFailed":
                               raise Exception(
                                   f"Synthesis failed: {data.get('payload', {}).get('error_info', 'Unknown error')}"
                               )

            except Exception as e:
                print(f"[Alibaba Cloud TTS] The {i + 1}th test failed: {str(e)}")
                latencies.append(None)

        return self._calculate_result("Alibaba Cloud TTS", latencies, test_count)

    async def test_alibl_tts(self, text=None, test_count=5):
        """Test Alibaba Cloud Bailian CosyVoice streaming TTS first word latency"""
        text = text or self.test_texts[0]
        latencies = []

        for i in range(test_count):
            try:
                tts_config = self.config["TTS"]["AliBLTTS"]
                api_key = tts_config["api_key"]
                model = tts_config.get("model", "cosyvoice-v2")
                voice = tts_config.get("voice", "longxiaochun_v2")
                format_type = tts_config.get("format", "pcm")
                sample_rate = int(tts_config.get("sample_rate", "24000"))

                ws_url = "wss://dashscope.aliyuncs.com/api-ws/v1/inference/"
                headers = {
                    "Authorization": f"Bearer {api_key}",
                    "X-DashScope-DataInspection": "enable",
                }

                start_time = time.time()

                async with websockets.connect(
                    ws_url,
                    additional_headers=headers,
                    ping_interval=30,
                    ping_timeout=10,
                    close_timeout=10,
                    max_size=10 * 1024 * 1024,
                ) as ws:
                    session_id = uuid.uuid4().hex

                    # 1. Send run-task (start task)
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
                            "model": model,
                            "parameters": {
                               "text_type": "PlainText",
                               "voice": voice,
                               "format": format_type,
                               "sample_rate": sample_rate,
                               "volume": 50,
                               "rate": 1.0,
                               "pitch": 1.0,
                            },
                            "input": {},
                        },
                    }
                    await ws.send(json.dumps(run_task_message))

                    # 2. Wait for task-started event
                    task_started = False
                    while not task_started:
                        msg = await ws.recv()
                        if isinstance(msg, str):
                            data = json.loads(msg)
                            header = data.get("header", {})
                            event = header.get("event")
                            if event == "task-started":
                               task_started = True
                               print(
                                   f"[Aliyun Bailian TTS] The {i + 1}th task started successfully"
                               )
                            elif event == "task-failed":
                               raise Exception(
                                   f"Task failed: {data.get('error_message', 'Unknown error')}"
                               )

                    # 3. Send continue-task (send text!)
                    continue_task_message = {
                        "header": {
                            "action": "continue-task",
                            "task_id": session_id,
                            "streaming": "duplex",
                        },
                        "payload": {"input": {"text": text}},
                    }
                    await ws.send(json.dumps(continue_task_message))

                    # 4. Send finish-task (end task)
                    finish_task_message = {
                        "header": {
                            "action": "finish-task",
                            "task_id": session_id,
                            "streaming": "duplex",
                        },
                        "payload": {"input": {}},
                    }
                    await ws.send(json.dumps(finish_task_message))

                    # 5. Wait for the first audio data block
                    while True:
                        try:
                            msg = await asyncio.wait_for(ws.recv(), timeout=15.0)
                            if isinstance(msg, (bytes, bytearray)) and len(msg) > 0:
                                latency = time.time() - start_time
                                print(
                                    f"[Alibaba Cloud BaiLian TTS] The {i + 1}th time first word latency: {latency:.3f}s"
                                )
                                latencies.append(latency)
                                break
                            elif isinstance(msg, str):
                                data = json.loads(msg)
                                event = data.get("header", {}).get("event")
                                if event == "task-failed":
                                    raise Exception(f"Synthesis failed: {data}")
                                elif event == "task-finished":
                                    if not latencies or latencies[-1] is None:
                                        raise Exception(
                                            "The task has ended but no audio was received"
                                        )
                        except asyncio.TimeoutError:
                            raise Exception("Timeout waiting for audio data")
                        except Exception as e:
                            # Re-raise exceptions encountered during waiting
                            raise e

            except Exception as e:
                print(f"[Alibaba Cloud's TTS] {i + 1}th failure: {str(e)}")
                latencies.append(None)

        return self._calculate_result("Doubao TTS", latencies, test_count)<unused56>
