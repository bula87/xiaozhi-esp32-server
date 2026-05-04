-- Add paddle_speech streaming TTS provider
DELETE FROM `ai_model_provider` WHERE id = 'SYSTEM_TTS_PaddleSpeechTTS';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) 
VALUES ('SYSTEM_TTS_PaddleSpeechTTS', 'TTS', 'paddle_speech', 'PaddleSpeechTTS', 
'[{"key":"protocol","label":"Protocol Type","type":"string","options":["websocket","http"]},{"key":"url","label":"Service Address","type":"string"},{"key":"spk_id","label":"Voice","type":"int"},{"key":"sample_rate","label":"Sample Rate","type":"float"},{"key":"speed","label":"Speech Speed","type":"float"},{"key":"volume","label":"Volume","type":"float"},{"key":"save_path","label":"Save Path","type":"string"}]', 
17, 1, NOW(), 1, NOW());

-- Add paddle_speech streaming TTS model configuration
DELETE FROM `ai_model_config` WHERE id = 'TTS_PaddleSpeechTTS';
INSERT INTO `ai_model_config` VALUES ('TTS_PaddleSpeechTTS', 'TTS', 'PaddleSpeechTTS', 'PaddleSpeechTTS', 0, 1, 
'{"type": "paddle_speech", "protocol": "websocket", "url": "ws://127.0.0.1:8092/paddlespeech/tts/streaming", "spk_id": "0", "sample_rate": 24000, "speed": 1.0, "volume": 1.0, "save_path": "./streaming_tts.wav"}', 
NULL, NULL, 20, NULL, NULL, NULL, NULL);

-- Update PaddleSpeechTTS configuration description
UPDATE `ai_model_config` SET 
`doc_link` = 'https://github.com/PaddlePaddle/PaddleSpeech',
`remark` = 'PaddleSpeechTTS Configuration Instructions:
1. PaddleSpeech is an open-source speech synthesis tool from Baidu PaddlePaddle, supporting local offline deployment and model training. PaddlePaddle framework address: https://www.paddlepaddle.org.cn/
2. Supports WebSocket and HTTP protocols, with WebSocket used by default for streaming (refer to deployment documentation: https://github.com/xinnan-tech/xiaozhi-esp32-server/blob/main/docs/paddlespeech-deploy.md).
3. Before use, deploy the paddlespeech service locally, which runs by default at ws://127.0.0.1:8092/paddlespeech/tts/streaming
4. Custom speaker, speed, volume, and sample rate are supported.
' WHERE `id` = 'TTS_PaddleSpeechTTS';

-- Delete old voice and add default voice
DELETE FROM `ai_tts_voice` WHERE tts_model_id = 'TTS_PaddleSpeechTTS';
INSERT INTO `ai_tts_voice` VALUES ('TTS_PaddleSpeechTTS_0000', 'TTS_PaddleSpeechTTS', 'Default', '0', 'Chinese', NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL);
 
 