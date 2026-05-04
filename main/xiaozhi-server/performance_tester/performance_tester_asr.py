import asyncio
import logging
import os
import time
import concurrent.futures
from typing import Dict, Optional
from tabulate import tabulate
import yaml
from core.utils.asr import create_instance as create_stt_instance

# Set global log level to WARNING, suppress INFO level logs
logging.basicConfig(level=logging.WARNING)

description = "Speech recognition model performance test"


class ASRPerformanceTester:
    def __init__(self):
        self.config = self._load_config_from_data_dir()
        self.test_wav_list = self._load_test_wav_files()
        self.results = {"stt": {}}

        # Debug log
        print(f"[DEBUG] Loaded ASR configuration: {self.config.get('ASR', {})}")
        print(f"[DEBUG] Number of audio files: {len(self.test_wav_list)}")

    def _load_config_from_data_dir(self) -> Dict:
        """Load all .config.yaml files configuration from data directory"""
        config = {"ASR": {}}
        data_dir = os.path.join(os.getcwd(), "data")
        print(f"[DEBUG] Scanning configuration file directory: {data_dir}")

        for root, _, files in os.walk(data_dir):
            for file in files:
                if file.endswith(".config.yaml"):
                    file_path = os.path.join(root, file)
                    try:
                        with open(file_path, "r", encoding="utf-8") as f:
                            file_config = yaml.safe_load(f)
                            # Compatible with case-insensitive ASR/asr configuration
                            asr_config = file_config.get("ASR") or file_config.get(
                                "asr"
                            )
                            if asr_config:
                                config["ASR"].update(asr_config)
                                print(
                                    f"[DEBUG] Successfully loaded ASR configuration from {file_path}"
                                )
                    except Exception as e:
                        print(
                            f" Failed to load configuration file {file_path}: {str(e)}"
                        )
        return config

    def _load_test_wav_files(self) -> list:
        """Load test audio files (with path debugging)"""
        wav_root = os.path.join(os.getcwd(), "config", "assets")
        print(f"[DEBUG] Audio file directory: {wav_root}")
        test_wav_list = []

        if os.path.exists(wav_root):
            file_list = os.listdir(wav_root)
            print(f"[DEBUG] Found audio files: {file_list}")
            for file_name in file_list:
                file_path = os.path.join(wav_root, file_name)
                if os.path.getsize(file_path) > 300 * 1024:  # 300KB
                    try:
                        with open(file_path, "rb") as f:
                            test_wav_list.append(f.read())
                    except IOError as e:
                        print(f"Warning: Could not read file {file_path}: {e}")
        else:
            print(f" Directory does not exist: {wav_root}")
        return test_wav_list

    async def _test_single_audio(
        self, stt_name: str, stt, audio_data: bytes
    ) -> Optional[float]:
        """Test the performance of a single audio file"""
        try:
            start_time = time.time()
            text, _ = await stt.speech_to_text_wrapper(
                [audio_data], "1", stt.audio_format
            )
            if text is None:
                return None

            duration = time.time() - start_time

            # Detect abnormal time of 0.000s
            if abs(duration) < 0.001:  # less than 1 millisecond is considered abnormal
                print(
                    f"{stt_name} Detected abnormal time: {duration:.6f}s (treated as error)"
                )
                return None

            return duration

        except Exception as e:
            error_msg = str(e).lower()
            if "502" in error_msg or "bad gateway" in error_msg:
                print(f"{stt_name} encountered 502 error")
                return None
            print(f"{stt_name} encountered general error: {str(e)}")
            return None

    async def _test_stt_with_timeout(self, stt_name: str, config: Dict) -> Dict:
        """Asynchronously test single STT performance with timeout control"""
        try:
            # Check configuration validity
            token_fields = ["access_token", "api_key", "token"]
            if any(
                field in config
                and str(config[field]).lower()
                in ["your", "placeholder", "none", "null", ""]
                for field in token_fields
            ):
                print(
                    f"  STT {stt_name} is not configured with valid access_token/api_key, skipped"
                )
                return {
                    "name": stt_name,
                    "type": "stt",
                    "errors": 1,
                    "error_type": "Configuration error",
                }

            module_type = config.get("type", stt_name)
            stt = create_stt_instance(module_type, config, delete_audio_file=True)
            stt.audio_format = "pcm"

            print(f" Testing STT: {stt_name}")

            # Using thread pool and timeout control

            # Testing the first audio file as a connectivity check
            try:
                with concurrent.futures.ThreadPoolExecutor() as executor:
                    future = executor.submit(
                        lambda: asyncio.run(
                            self._test_single_audio(
                                stt_name, stt, self.test_wav_list[0]
                            )
                        )
                    )
                    first_result = await asyncio.wait_for(
                        asyncio.wrap_future(future), timeout=10.0
                    )

                    if first_result is None:
                        print(f" {stt_name} Connection failed")
                        return {
                            "name": stt_name,
                            "type": "stt",
                            "errors": 1,
                            "error_type": "Network error",
                        }
            except asyncio.TimeoutError:
                print(f" {stt_name} Connection timed out (10 seconds), skip")
                return {
                    "name": stt_name,
                    "type": "stt",
                    "errors": 1,
                    "error_type": "Timeout connection",
                }
            except Exception as e:
                error_msg = str(e).lower()
                if "502" in error_msg or "bad gateway" in error_msg:
                    print(f" {stt_name} encountered 502 error, skip")
                    return {
                        "name": stt_name,
                        "type": "stt",
                        "errors": 1,
                        "error_type": "502 network error",
                    }
                print(f" {stt_name} Connection Exception: {str(e)}")
                return {
                    "name": stt_name,
                    "type": "stt",
                    "errors": 1,
                    "error_type": "Network error",
                }

            # Full batch testing, with timeout control
            total_time = 0
            valid_tests = 0
            test_count = len(self.test_wav_list)

            for i, audio_data in enumerate(self.test_wav_list, 1):
                try:
                    with concurrent.futures.ThreadPoolExecutor() as executor:
                        future = executor.submit(
                            lambda: asyncio.run(
                                self._test_single_audio(stt_name, stt, audio_data)
                            )
                        )
                        duration = await asyncio.wait_for(
                            asyncio.wrap_future(future), timeout=10.0
                        )

                        if duration is not None and duration > 0.001:
                            total_time += duration
                            valid_tests += 1
                            print(
                                f" {stt_name} [{i}/{test_count}] Duration: {duration:.2f}s"
                            )
                        else:
                            print(
                                f" {stt_name} [{i}/{test_count}] Test failed (including 0.000s exception)"
                            )

                except asyncio.TimeoutError:
                    print(f" {stt_name} [{i}/{test_count}] Timeout (10s), skip")
                    continue
                except Exception as e:
                    error_msg = str(e).lower()
                    if "502" in error_msg or "bad gateway" in error_msg:
                        print(f" {stt_name} [{i}/{test_count}] 502 error, skip")
                        return {
                            "name": stt_name,
                            "type": "stt",
                            "errors": 1,
                            "error_type": "502 network error",
                        }
                    print(f" {stt_name} [{i}/{test_count}] Exception: {str(e)}")
                    continue

            # Check valid test count
            if valid_tests < test_count * 0.3:  # At least 30% success rate
                print(
                    f" {stt_name} Tested too few successful tests({valid_tests}/{test_count}), network may be unstable"
                )
                return {
                    "name": stt_name,
                    "type": "stt",
                    "errors": 1,
                    "error_type": "Network error",
                }

            if valid_tests == 0:
                return {
                    "name": stt_name,
                    "type": "stt",
                    "errors": 1,
                    "error_type": "Network error",
                }

            avg_time = total_time / valid_tests
            return {
                "name": stt_name,
                "type": "stt",
                "avg_time": avg_time,
                "success_rate": f"{valid_tests}/{test_count}",
                "errors": 0,
            }

        except Exception as e:
            error_msg = str(e).lower()
            if "502" in error_msg or "bad gateway" in error_msg:
                error_type = "502 network error"
            elif "timeout" in error_msg:
                error_type = "timeout connection"
            else:
                error_type = "network error"
            print(f"⚠️ {stt_name} Test failed: {str(e)}")
            return {
                "name": stt_name,
                "type": "stt",
                "errors": 1,
                "error_type": error_type,
            }

    def _print_results(self):
        """Print test results, sorted by response time"""
        print("\n" + "=" * 50)
        print("ASR Performance Test Results")
        print("=" * 50)

        if not self.results.get("stt"):
            print("No available test results")
            return
        headers = ["Model Name", "Average time taken(s)", "Success Rate", "Status"]
        table_data = []

        # Collect all data and classify
        valid_results = []
        error_results = []

        for name, data in self.results["stt"].items():
            if data["errors"] == 0:
                # Normal result
                avg_time = f"{data['avg_time']:.3f}"
                success_rate = data.get("success_rate", "N/A")
                status = "✅ Normal"

                # Save the value used for sorting
                sort_key = data["avg_time"]

                valid_results.append(
                    {
                        "name": name,
                        "avg_time": avg_time,
                        "success_rate": success_rate,
                        "status": status,
                        "sort_key": sort_key,
                    }
                )
            else:
                # Error results
                avg_time = "-"
                success_rate = "0/N"
                # Get specific error type
                error_type = data.get("error_type", "Network error")
                status = f"❌ {error_type}"

                error_results.append([name, avg_time, success_rate, status])

        # Sort by response time in ascending order (from fast to slow)
        valid_results.sort(key=lambda x: x["sort_key"])

        # Convert sorted valid results to table data
        for result in valid_results:
            table_data.append(
                [
                    result["name"],
                    result["avg_time"],
                    result["success_rate"],
                    result["status"],
                ]
            )

        # Append error results to the end of the table data
        table_data.extend(error_results)

        print(tabulate(table_data, headers=headers, tablefmt="grid"))
        print("\nTest Instructions:")
        print(
            "- Timeout Control: Maximum waiting time for a single audio is 10 seconds"
        )
        print(
            "- Error Handling: Automatically skip models with 502 errors, timeouts, and network exceptions"
        )
        print(
            "- Success Rate: Number of successfully recognized audio / Total number of test audio"
        )
        print(
            "- Sorting Rule: Sort by average time from fastest to slowest, error models placed last"
        )
        print("\nTest Complete!")

    async def run(self):
        """Execute full asynchronous test"""
        print("Start filtering available ASR modules...")
        if not self.config.get("ASR"):
            print("ASR module not found in configuration")
            return
        all_tasks = []
        for stt_name, config in self.config["ASR"].items():
            # Check configuration validity
            token_fields = ["access_token", "api_key", "token"]
            if any(
                field in config
                and str(config[field]).lower()
                in ["your", "placeholder", "none", "null", ""]
                for field in token_fields
            ):
                print(
                    f"ASR {stt_name} is not configured with a valid access_token/api_key, skipped"
                )
                continue

            print(f"Adding ASR test task: {stt_name}")
            all_tasks.append(self._test_stt_with_timeout(stt_name, config))

        if not all_tasks:
            print("No available ASR modules to test.")
            return

        print(f"\nFound {len(all_tasks)} available ASR modules")
        print("\nStart concurrent testing of all ASR modules...")
        all_results = await asyncio.gather(*all_tasks, return_exceptions=True)

        # Process results
        for result in all_results:
            if isinstance(result, dict) and result.get("type") == "stt":
                self.results["stt"][result["name"]] = result

        # Print results
        self._print_results()


async def main():
    tester = ASRPerformanceTester()
    await tester.run()


if __name__ == "__main__":
    asyncio.run(main())
