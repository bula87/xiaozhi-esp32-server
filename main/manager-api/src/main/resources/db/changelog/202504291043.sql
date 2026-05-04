-- Add FunASR Service Speech Recognition model provider and configuration
DELETE FROM `ai_model_provider` WHERE `id` = 'SYSTEM_ASR_FunASRServer';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_ASR_FunASRServer', 'ASR', 'fun_server', 'FunASR Service Speech Rec', '[{"key":"host","label":"Server Address","type":"string"},{"key":"port","label":"Port Number","type":"number"}]', 4, 1, NOW(), 1, NOW());

DELETE FROM `ai_model_config` WHERE `id` = 'ASR_FunASRServer';
INSERT INTO `ai_model_config` VALUES ('ASR_FunASRServer', 'ASR', 'FunASRServer', 'FunASR Service Speech Rec', 0, 1, '{\"type\": \"fun_server\", \"host\": \"127.0.0.1\", \"port\": 10096}', NULL, NULL, 5, NULL, NULL, NULL, NULL);

-- Change the remark field type of ai_model_config table to TEXT
ALTER TABLE `ai_model_config` MODIFY COLUMN `remark` TEXT COMMENT 'Remark'; 

-- Update ASR model configuration documentation
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/modelscope/FunASR/blob/main/runtime/docs/SDK_advanced_guide_online_zh.md',
`remark` = 'Independent deployment of FunASR using the FunASR API service requires only five commands:
First command: mkdir -p ./funasr-runtime-resources/models
Second command: sudo docker run -d -p 10096:10095 --privileged=true -v $PWD/funasr-runtime-resources/models:/workspace/models registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.12
After executing the above command, you will enter the container. Continue with the third command: cd FunASR/runtime
Do not exit the container. Continue executing the fourth command: nohup bash run_server_2pass.sh --download-model-dir /workspace/models --vad-dir damo/speech_fsmn_vad_zh-cn-16k-common-onnx --model-dir damo/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx  --online-model-dir damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx  --punc-dir damo/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx --lm-dir damo/speech_ngram_lm_zh-cn-ai-wesp-fst --itn-dir thuduj12/fst_itn_zh --hotword /workspace/models/hotwords.txt > log.txt 2>&1 &
After executing the above, continue with the fifth command: tail -f log.txt
After the fifth command executes, you will see the model download logs. Once downloaded, you can connect and use it.
The above uses CPU inference. If you have a GPU, please refer to: https://github.com/modelscope/FunASR/blob/main/runtime/docs/SDK_advanced_guide_online_zh.md' WHERE `id` = 'ASR_FunASRServer';

-- Update FunASR local model configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/modelscope/FunASR',
`remark` = 'FunASR Local Model Configuration Notes:
1. Model files need to be downloaded to the xiaozhi-server/models/SenseVoiceSmall directory.
2. Supports Chinese, Japanese, Korean, and Cantonese speech recognition.
3. Local inference, no network connection required.
4. Files to be recognized are saved in the tmp/ directory.' WHERE `id` = 'ASR_FunASR';

-- Update SherpaASR configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/k2-fsa/sherpa-onnx',
`remark` = 'SherpaASR Configuration Notes:
1. Model files are automatically downloaded to models/sherpa-onnx-sense-voice-zh-en-ja-ko-yue-2024-07-17 upon runtime.
2. Supports Chinese, English, Japanese, Korean, Cantonese, and other languages.
3. Local inference, no network connection required.
4. Output files are saved in the tmp/ directory.' WHERE `id` = 'ASR_SherpaASR';

-- Update Doubao ASR configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.volcengine.com/speech/app',
`remark` = 'Doubao ASR Configuration Notes:
1. Create an application in the Volcengine console and obtain the appid and access_token.
2. Supports Chinese speech recognition.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://console.volcengine.com/speech/app
2. Create a new application
3. Obtain appid and access_token
4. Fill them into the configuration file' WHERE `id` = 'ASR_DoubaoASR';

-- Update Tencent ASR configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.cloud.tencent.com/cam/capi',
`remark` = 'Tencent ASR Configuration Notes:
1. Create an application in the Tencent Cloud console and obtain appid, secret_id, and secret_key.
2. Supports Chinese speech recognition.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://console.cloud.tencent.com/cam/capi to get keys
2. Visit https://console.cloud.tencent.com/asr/resourcebundle to claim free resources
3. Obtain appid, secret_id, and secret_key
4. Fill them into the configuration file' WHERE `id` = 'ASR_TencentASR';

-- Update TTS model configuration notes
-- EdgeTTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/rany2/edge-tts',
`remark` = 'Edge TTS Configuration Notes:
1. Uses Microsoft Edge TTS service.
2. Supports multiple languages and voices.
3. Free to use, no registration required.
4. Requires network connection.
5. Output files are saved in the tmp/ directory.' WHERE `id` = 'TTS_EdgeTTS';

-- Doubao TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.volcengine.com/speech/service/8',
`remark` = 'Doubao TTS Configuration Notes:
1. Visit https://console.volcengine.com/speech/service/8
2. Create an application in the Volcengine console and obtain appid and access_token.
3. Volcengine speech services must be purchased. The starting price is 30 RMB for 100 concurrency. The free tier only has 2 concurrency, which frequently causes TTS errors.
4. After purchasing the service and acquiring free voices, it may take about half an hour before it can be used.
5. Fill them into the configuration file.' WHERE `id` = 'TTS_DoubaoTTS';

-- SiliconFlow TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://cloud.siliconflow.cn/account/ak',
`remark` = 'SiliconFlow TTS Configuration Notes:
1. Visit https://cloud.siliconflow.cn/account/ak
2. Register and obtain the API key.
3. Fill it into the configuration file.' WHERE `id` = 'TTS_CosyVoiceSiliconflow';

-- CozeCN TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://www.coze.cn/open/oauth/pats',
`remark` = 'CozeCN TTS Configuration Notes:
1. Visit https://www.coze.cn/open/oauth/pats
2. Obtain a Personal Access Token.
3. Fill it into the configuration file.' WHERE `id` = 'TTS_CozeCnTTS';

-- FishSpeech configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/fishaudio/fish-speech',
`remark` = 'FishSpeech Configuration Notes:
1. Requires local deployment of the FishSpeech service.
2. Supports custom voice cloning.
3. Local inference, no network connection required.
4. Output files are saved in the tmp/ directory.
5. Example service run command: python -m tools.api_server --listen 0.0.0.0:8080 --llama-checkpoint-path "checkpoints/fish-speech-1.5" --decoder-checkpoint-path "checkpoints/fish-speech-1.5/firefly-gan-vq-fsq-8x1024-21hz-generator.pth" --decoder-config-name firefly_gan_vq --compile' WHERE `id` = 'TTS_FishSpeech';

-- GPT-SoVITS V2 configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/RVC-Boss/GPT-SoVITS',
`remark` = 'GPT-SoVITS V2 Configuration Notes:
1. Requires local deployment of the GPT-SoVITS service.
2. Supports custom voice cloning.
3. Local inference, no network connection required.
4. Output files are saved in the tmp/ directory.
Deployment Steps:
1. Example service run command: python api_v2.py -a 127.0.0.1 -p 9880 -c GPT_SoVITS/configs/demo.yaml' WHERE `id` = 'TTS_GPT_SOVITS_V2';

-- GPT-SoVITS V3 configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/RVC-Boss/GPT-SoVITS',
`remark` = 'GPT-SoVITS V3 Configuration Notes:
1. Requires local deployment of the GPT-SoVITS V3 service.
2. Supports custom voice cloning.
3. Local inference, no network connection required.
4. Output files are saved in the tmp/ directory.' WHERE `id` = 'TTS_GPT_SOVITS_V3';

-- MiniMax TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://platform.minimaxi.com/',
`remark` = 'MiniMax TTS Configuration Notes:
1. Create an account and add balance on the MiniMax platform.
2. Supports multiple voices, current config uses female-shaonv.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://platform.minimaxi.com/ to register an account
2. Visit https://platform.minimaxi.com/user-center/payment/balance to add balance
3. Visit https://platform.minimaxi.com/user-center/basic-information to get group_id
4. Visit https://platform.minimaxi.com/user-center/basic-information/interface-key to get api_key
5. Fill them into the configuration file' WHERE `id` = 'TTS_MinimaxTTS';

-- Aliyun TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://nls-portal.console.aliyun.com/',
`remark` = 'Aliyun TTS Configuration Notes:
1. Open Intelligent Speech Interaction service on the Aliyun platform.
2. Supports multiple voices, current config uses xiaoyun.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://nls-portal.console.aliyun.com/ to open the service
2. Visit https://nls-portal.console.aliyun.com/applist to get appkey
3. Visit https://nls-portal.console.aliyun.com/overview to get token
4. Fill them into the configuration file
Note: The token is temporary and valid for 24 hours. For long-term use, configure access_key_id and access_key_secret.' WHERE `id` = 'TTS_AliyunTTS';

-- Tencent TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.cloud.tencent.com/cam/capi',
`remark` = 'Tencent TTS Configuration Notes:
1. Open Intelligent Speech Interaction service on the Tencent Cloud platform.
2. Supports multiple voices, current config uses 101001.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://console.cloud.tencent.com/cam/capi to get keys
2. Visit https://console.cloud.tencent.com/tts/resourcebundle to claim free resources
3. Create a new application
4. Get appid, secret_id, and secret_key
5. Fill them into the configuration file' WHERE `id` = 'TTS_TencentTTS';

-- 302AI TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://dash.302.ai/',
`remark` = '302AI TTS Configuration Notes:
1. Create an account on the 302 platform and obtain an API key.
2. Supports multiple voices, current config uses Wanwan Xiaohe voice.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://dash.302.ai/ to register an account
2. Visit https://dash.302.ai/apis/list to get the API key
3. Fill it into the configuration file
Pricing: $35 / 1 million characters' WHERE `id` = 'TTS_TTS302AI';

-- Gizwits TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://agentrouter.gizwitsapi.com/panel/token',
`remark` = 'Gizwits TTS Configuration Notes:
1. Obtain an API key from the Gizwits platform.
2. Supports multiple voices, current config uses Wanwan Xiaohe voice.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://agentrouter.gizwitsapi.com/panel/token to get the API key
2. Fill it into the configuration file
Note: The first 10,000 registered users receive a 5 RMB trial balance.' WHERE `id` = 'TTS_GizwitsTTS';

-- ACGN TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://acgn.ttson.cn/',
`remark` = 'ACGN TTS Configuration Notes:
1. Purchase tokens on the ttson platform.
2. Supports multiple character voices, current config uses Role ID: 1695.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://acgn.ttson.cn/ to view the character list
2. Visit www.ttson.cn to purchase tokens
3. Fill them into the configuration file
For development-related questions, please submit them to the QQ on the website.' WHERE `id` = 'TTS_ACGNTTS';

-- OpenAI TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://platform.openai.com/api-keys',
`remark` = 'OpenAI TTS Configuration Notes:
1. Obtain an API key from the OpenAI platform.
2. Supports multiple voices, current config uses onyx.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Application Steps:
1. Visit https://platform.openai.com/api-keys to get the API key
2. Fill it into the configuration file
Note: Proxy access is required if using within China.' WHERE `id` = 'TTS_OpenAITTS';

-- Custom TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = NULL,
`remark` = 'Custom TTS Configuration Notes:
1. Supports custom TTS API services.
2. Uses GET request method.
3. Requires network connection.
4. Output files are saved in the tmp/ directory.
Configuration Guide:
1. Configure request parameters in params.
2. Configure request headers in headers.
3. Set the returned audio format.' WHERE `id` = 'TTS_CustomTTS';

-- Volcengine AI Gateway TTS configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.volcengine.com/vei/aigateway/',
`remark` = 'Volcengine AI Gateway TTS Configuration Notes:
1. Visit https://console.volcengine.com/vei/aigateway/
2. Create a gateway access key, search for and check Doubao-Speech Synthesis.
3. If LLM is needed, also check Doubao-pro-32k-functioncall.
4. Visit https://console.volcengine.com/vei/aigateway/tokens-list to get the key.
5. Fill it into the configuration file.
Voice list reference: https://www.volcengine.com/docs/6561/1257544' WHERE `id` = 'TTS_VolcesAiGatewayTTS';

-- Update LLM model configuration notes
-- ChatGLM configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://bigmodel.cn/usercenter/proj-mgmt/apikeys',
`remark` = 'ChatGLM Configuration Notes:
1. Visit https://bigmodel.cn/usercenter/proj-mgmt/apikeys
2. Register and obtain the API key.
3. Fill it into the configuration file.' WHERE `id` = 'LLM_ChatGLMLLM';

-- Ollama configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://ollama.com/',
`remark` = 'Ollama Configuration Notes:
1. Install the Ollama service.
2. Run command: ollama pull qwen2.5
3. Ensure the service is running at http://localhost:11434' WHERE `id` = 'LLM_OllamaLLM';

-- Tongyi Qianwen configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://bailian.console.aliyun.com/?apiKey=1#/api-key',
`remark` = 'Tongyi Qianwen Configuration Notes:
1. Visit https://bailian.console.aliyun.com/?apiKey=1#/api-key
2. Obtain the API key.
3. Fill it into the configuration file, current config uses qwen-turbo model.
4. Supports custom parameters: temperature=0.7, max_tokens=500, top_p=1, top_k=50' WHERE `id` = 'LLM_AliLLM';

-- Tongyi Bailian configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://bailian.console.aliyun.com/?apiKey=1#/api-key',
`remark` = 'Tongyi Bailian Configuration Notes:
1. Visit https://bailian.console.aliyun.com/?apiKey=1#/api-key
2. Obtain the app_id and api_key.
3. Fill them into the configuration file.' WHERE `id` = 'LLM_AliAppLLM';

-- Doubao LLM configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.volcengine.com/ark/region:ark+cn-beijing/openManagement',
`remark` = 'Doubao LLM Configuration Notes:
1. Visit https://console.volcengine.com/ark/region:ark+cn-beijing/openManagement
2. Activate the Doubao-1.5-pro service.
3. Visit https://console.volcengine.com/ark/region:ark+cn-beijing/apiKey to get the API key.
4. Fill it into the configuration file.
5. Currently recommended to use doubao-1-5-pro-32k-250115
Note: There is a free quota of 500,000 tokens.' WHERE `id` = 'LLM_DoubaoLLM';

-- DeepSeek configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://platform.deepseek.com/',
`remark` = 'DeepSeek Configuration Notes:
1. Visit https://platform.deepseek.com/
2. Register and obtain the API key.
3. Fill it into the configuration file.' WHERE `id` = 'LLM_DeepSeekLLM';

-- Dify configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://cloud.dify.ai/',
`remark` = 'Dify Configuration Notes:
1. Visit https://cloud.dify.ai/
2. Register and obtain the API key.
3. Fill it into the configuration file.
4. Supports multiple chat modes: workflows/run, chat-messages, completion-messages
5. Role definitions set in this platform will be voided; they must be set in the Dify console.
Note: It is recommended to use locally deployed Dify APIs. Public cloud APIs might be restricted in some regions.' WHERE `id` = 'LLM_DifyLLM';

-- Gemini configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://aistudio.google.com/apikey',
`remark` = 'Gemini Configuration Notes:
1. Uses Google Gemini API service.
2. Current config uses gemini-2.0-flash model.
3. Requires network connection.
4. Supports proxy configuration.
Application Steps:
1. Visit https://aistudio.google.com/apikey
2. Create an API key.
3. Fill it into the configuration file.
Note: If using within China, please comply with the "Interim Measures for the Management of Generative AI Services".' WHERE `id` = 'LLM_GeminiLLM';

-- Coze configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://www.coze.cn/open/oauth/pats',
`remark` = 'Coze Configuration Notes:
1. Uses Coze platform services.
2. Requires bot_id, user_id, and personal token.
3. Requires network connection.
Application Steps:
1. Visit https://www.coze.cn/open/oauth/pats
2. Obtain personal token.
3. Manually calculate bot_id and user_id.
4. Fill them into the configuration file.' WHERE `id` = 'LLM_CozeLLM';

-- LM Studio configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://lmstudio.ai/',
`remark` = 'LM Studio Configuration Notes:
1. Uses locally deployed LM Studio service.
2. Current config uses deepseek-r1-distill-llama-8b@q4_k_m model.
3. Local inference, no network connection required.
4. Models need to be downloaded in advance.
Deployment Steps:
1. Install LM Studio.
2. Download model from community.
3. Ensure service is running at http://localhost:1234/v1' WHERE `id` = 'LLM_LMStudioLLM';

-- FastGPT configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://cloud.tryfastgpt.ai/account/apikey',
`remark` = 'FastGPT Configuration Notes:
1. Uses FastGPT platform services.
2. Requires network connection.
3. Prompt in the config file is void; must be set in FastGPT console.
4. Supports custom variables.
Application Steps:
1. Visit https://cloud.tryfastgpt.ai/account/apikey
2. Obtain API key.
3. Fill it into the configuration file.' WHERE `id` = 'LLM_FastgptLLM';

-- Xinference configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/xorbitsai/inference',
`remark` = 'Xinference Configuration Notes:
1. Uses locally deployed Xinference service.
2. Current config uses qwen2.5:72b-AWQ model.
3. Local inference, no network connection required.
4. The corresponding model must be launched beforehand.
Deployment Steps:
1. Install Xinference.
2. Start the service and load the model.
3. Ensure service is running at http://localhost:9997' WHERE `id` = 'LLM_XinferenceLLM';

-- Xinference Small LLM configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/xorbitsai/inference',
`remark` = 'Xinference Small LLM Configuration Notes:
1. Uses locally deployed Xinference service.
2. Current config uses qwen2.5:3b-AWQ model.
3. Local inference, no network connection required.
4. Used for intent recognition.
Deployment Steps:
1. Install Xinference.
2. Start the service and load the model.
3. Ensure service is running at http://localhost:9997' WHERE `id` = 'LLM_XinferenceSmallLLM';

-- Volcengine AI Gateway LLM configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.volcengine.com/vei/aigateway/',
`remark` = 'Volcengine AI Gateway LLM Configuration Notes:
1. Uses Volcengine AI Gateway services.
2. Requires gateway access key.
3. Requires network connection.
4. Supports function_call feature.
Application Steps:
1. Visit https://console.volcengine.com/vei/aigateway/
2. Create gateway access key, search for and check Doubao-pro-32k-functioncall.
3. If Speech Synthesis is needed, also check Doubao-Speech Synthesis.
4. Visit https://console.volcengine.com/vei/aigateway/tokens-list to get the key.
5. Fill it into the configuration file.' WHERE `id` = 'LLM_VolcesAiGatewayLLM';

-- Update Memory model configuration notes
-- No Memory configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = NULL,
`remark` = 'No Memory Configuration Notes:
1. Does not save chat history.
2. Each conversation is independent.
3. No extra config needed.
4. Suitable for scenarios with high privacy requirements.' WHERE `id` = 'Memory_nomem';

-- Local Short Memory configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = NULL,
`remark` = 'Local Short Memory Configuration Notes:
1. Uses local storage to save chat history.
2. Summarizes chat content using selected_module.LLM.
3. Data is saved locally and not uploaded to servers.
4. Suitable for privacy-focused scenarios.
5. No extra config needed.' WHERE `id` = 'Memory_mem_local_short';

-- Mem0AI Memory configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://app.mem0.ai/dashboard/api-keys',
`remark` = 'Mem0AI Memory Configuration Notes:
1. Uses Mem0AI service to save chat history.
2. Requires API key.
3. Requires network connection.
4. Has 1000 free calls per month.
Application Steps:
1. Visit https://app.mem0.ai/dashboard/api-keys
2. Obtain API key.
3. Fill it into the configuration file.' WHERE `id` = 'Memory_mem0ai';

-- Update Intent model configuration notes
-- No Intent Recognition configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = NULL,
`remark` = 'No Intent Recognition Configuration Notes:
1. Does not perform intent recognition.
2. All conversations are passed directly to LLM for processing.
3. No extra config needed.
4. Suitable for simple chat scenarios.' WHERE `id` = 'Intent_nointent';

-- LLM Intent Recognition configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = NULL,
`remark` = 'LLM Intent Recognition Configuration Notes:
1. Uses an independent LLM for intent recognition.
2. Uses the model specified in selected_module.LLM by default.
3. Can be configured to use a separate LLM (e.g., free ChatGLMLLM).
4. Highly versatile but increases processing time.
5. Does not support IoT operations like volume control.
Configuration Guide:
1. Specify the LLM model in the llm field.
2. If unspecified, defaults to the selected_module.LLM model.' WHERE `id` = 'Intent_intent_llm';

-- Function Call Intent Recognition configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = NULL,
`remark` = 'Function Call Intent Recognition Configuration Notes:
1. Uses the LLMs function_call feature for intent recognition.
2. Requires the chosen LLM to support function_call.
3. Calls tools on demand, with fast processing speeds.
4. Supports all IoT instructions.
5. Default loaded functions:
   - handle_exit_intent (Exit Recognition)
   - play_music (Music Playback)
   - change_role (Role Switch)
   - get_weather (Weather Check)
   - get_news (News Check)
Configuration Guide:
1. Configure required function modules in the functions field.
2. Base functions are loaded by default; no need to re-configure.
3. Custom function modules can be added.' WHERE `id` = 'Intent_function_call';

-- Update VAD model configuration notes
-- SileroVAD configuration notes
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/snakers4/silero-vad',
`remark` = 'SileroVAD Configuration Notes:
1. Uses SileroVAD model for voice activity detection.
2. Local inference, no network connection required.
3. Requires downloading the model file to the models/snakers4_silero-vad directory.
4. Configurable parameters:
   - threshold: 0.5 (Voice detection threshold)
   - min_silence_duration_ms: 700 (Minimum silence duration in milliseconds)
5. If speaking pauses are long, you can suitably increase the min_silence_duration_ms value.' WHERE `id` = 'VAD_SileroVAD';