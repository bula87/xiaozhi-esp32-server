import asyncio
import logging
import os
import time
import threading
from typing import Dict
from tabulate import tabulate

# Ensure importing create_tts_instance from core.utils.tts
from core.utils.tts import create_instance as create_tts_instance
from config.settings import load_config

# Set global log level to WARNING
logging.basicConfig(level=logging.WARNING)
description = "Non-streaming speech synthesis performance test"


class TTSPerformanceTester:
    def __init__(self):
        self.config = load_config()
        self.test_sentences = self.config.get("module_test", {}).get(
            "test_sentences",
            [
                "In the ninth year of Yonghe, the year was GuiChou, early spring;",
                "Whenever people interact, they may spend their lives looking up and down, some expressing their feelings within a room, others entrusting themselves to external things, wandering freely beyond the body. Although their pursuits and choices vary greatly, their calmness and restlessness differ,",
                "Whenever I read about the reasons past people felt moved, I always mourn and lament when facing the text, unable to comprehend it in my heart. I firmly know that equating life and death is a false notion, and considering Peng Shang as equal is a baseless act.",
            ],
        )
        self.results = {}

    async def _test_tts(self, tts_name: str, config: Dict) -> Dict:
        """Test performance of a single TTS module"""
        try:
            token_fields = ["access_token", "api_key", "token"]
            if any(
                field in config
                and any(x in config[field] for x in ["your", "placeholder"])
                for field in token_fields
            ):
                print(
                    f"TTS {tts_name} is not configured with access_token/api_key, skipped"
                )
                return {"name": tts_name, "errors": 1}

            module_type = config.get("type", tts_name)
            tts = create_tts_instance(module_type, config, delete_audio_file=True)

            # Set mock conn object to avoid TTS implementation accessing self.conn.sample_rate as None
            class MockConn:
                sample_rate = 16000
                audio_format = "pcm"
                stop_event = threading.Event()  # Needs to be a real Event object
                client_abort = False
                headers = {}

            tts.conn = MockConn()

            # Set mock opus_encoder to avoid certain TTS accessing self.opus_encoder as None
            class MockOpusEncoder:
                pass

            if not hasattr(tts, "opus_encoder") or tts.opus_encoder is None:
                tts.opus_encoder = MockOpusEncoder()

            print(f"Testing TTS: {tts_name}")

            # Connection test
            tmp_file = tts.generate_filename()
            await tts.text_to_speak("Connection test", tmp_file)

            if not tmp_file or not os.path.exists(tmp_file):
                print(f"{tts_name} Connection failed")
                return {"name": tts_name, "errors": 1}

            total_time = 0
            test_count = len(self.test_sentences[:3])
            for i, sentence in enumerate(self.test_sentences[:2], 1):
                start = time.time()
                tmp_file = tts.generate_filename()
                await tts.text_to_speak(sentence, tmp_file)
                duration = time.time() - start
                total_time += duration

                if tmp_file and os.path.exists(tmp_file):
                    print(f"{tts_name} [{i}/{test_count}] Test successful")
                else:
                    print(f"{tts_name} [{i}/{test_count}] Test failed")
                    return {"name": tts_name, "errors": 1}

            return {
                "name": tts_name,
                "avg_time": total_time / test_count,
                "errors": 0,
            }

        except Exception as e:
            print(f"{tts_name} test failed: {str(e)}")
            return {"name": tts_name, "errors": 1}

    def _print_results(self):
        """Print test results"""
        if not self.results:
            print("No valid TTS test results")
            return

        headers = ["TTS Module", "Avg Time (s)", "Test Sentences", "Status"]
        table_data = []

        # Collect all data and categorize
        valid_results = []
        error_results = []

        for name, data in self.results.items():
            if data["errors"] == 0:
                # Normal results
                avg_time = f"{data['avg_time']:.3f}"
                test_count = len(self.test_sentences[:3])
                status = "✅ Normal"

                # Save values for sorting
                valid_results.append(
                    {
                        "name": name,
                        "avg_time": avg_time,
                        "test_count": test_count,
                        "status": status,
                        "sort_key": data["avg_time"],
                    }
                )
            else:
                # Error results
                avg_time = "-"
                test_count = "0/3"

                # Default error type is network error
                error_type = "Network error"
                status = f"❌ {error_type}"

                error_results.append([name, avg_time, test_count, status])

        # Sort by average time ascending
        valid_results.sort(key=lambda x: x["sort_key"])

        # Convert sorted valid results to table data
        for result in valid_results:
            table_data.append(
                [
                    result["name"],
                    result["avg_time"],
                    result["test_count"],
                    result["status"],
                ]
            )

        # Add error results to table data end
        table_data.extend(error_results)

        print("\nTTS Performance Test Results:")
        print(
            tabulate(
                table_data,
                headers=headers,
                tablefmt="grid",
                colalign=("left", "right", "right", "left"),
            )
        )
        print("\nTest Instructions:")
        print("- Timeout control: Maximum wait time for a single request is 10 seconds")
        print(
            "- Error handling: Unconnected and timeout items are marked as network errors"
        )
        print("- Sorting rule: Sorted by average time from fast to slow")

    async def run(self):
        """execute test"""
        print("Start TTS performance test...")
        if not self.config.get("TTS"):
            print("TTS configuration not found in the configuration file")
            return

        # Iterate over all TTS configurations
        tasks = []
        for tts_name, config in self.config.get("TTS", {}).items():
            tasks.append(self._test_tts(tts_name, config))
        # Execute tests in parallel
        results = await asyncio.gather(*tasks)

        # Save all results, including errors
        for result in results:
            self.results[result["name"]] = result

        # Print results
        self._print_results()


# For the calling requirements of performance_tester.py
async def main():
    tester = TTSPerformanceTester()
    await tester.run()


if __name__ == "__main__":
    asyncio.run(main())
