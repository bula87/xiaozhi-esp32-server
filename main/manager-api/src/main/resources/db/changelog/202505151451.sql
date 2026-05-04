-- Modify custom TTS interface request definition
update `ai_model_provider` set `fields` =
'[{"key":"url","label":"Service Address","type":"string"},{"key":"method","label":"Request Method","type":"string"},{"key":"params","label":"Request Parameters","type":"dict","dict_name":"params"},{"key":"headers","label":"Request Headers","type":"dict","dict_name":"headers"},{"key":"format","label":"Audio Format","type":"string"},{"key":"output_dir","label":"Output Directory","type":"string"}]'
where `id` = 'SYSTEM_TTS_custom';

-- Modify custom TTS configuration description
UPDATE `ai_model_config` SET
`doc_link` = NULL,
`remark` = 'Custom TTS Configuration Description：
1. Custom TTS interface service, request parameters can be customized, can access many TTS services
2. Taking locally deployed KokoroTTS as an example
3. If only CPU running：docker run -p 8880:8880 ghcr.io/remsky/kokoro-fastapi-cpu:latest
4. If only GPU running：docker run --gpus all -p 8880:8880 ghcr.io/remsky/kokoro-fastapi-gpu:latest
Configuration Description：
1. Configure request parameters in params, using JSON format
   For example KokoroTTS：{ "input": "{prompt_text}", "speed": 1, "voice": "zm_yunxi", "stream": true, "download_format": "mp3", "response_format": "mp3", "return_download_link": true }
2. Configure request headers in headers
3. Set the returned audio format'
WHERE `id` = 'TTS_CustomTTS';
 