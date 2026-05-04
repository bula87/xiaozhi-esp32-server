-- VOSK ASR model provider
delete from `ai_model_provider` where id = 'SYSTEM_ASR_VoskASR';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_ASR_VoskASR', 'ASR', 'vosk', 'VOSK offline speech recognition', '[{"key": "model_path", "type": "string", "label": "model path"}, {"key": "output_dir", "type": "string", "label": "output directory"}]', 11, 1, NOW(), 1, NOW());

-- VOSK ASR model configuration
delete from `ai_model_config` where id = 'ASR_VoskASR';
INSERT INTO `ai_model_config` VALUES ('ASR_VoskASR', 'ASR', 'VoskASR', 'VOSK offline speech recognition', 0, 1, '{\"type\": \"vosk\", \"model_path\": \"\", \"output_dir\": \"tmp/\"}', NULL, NULL, 11, NULL, NULL, NULL, NULL);

-- Update VOSK ASR configuration description
UPDATE `ai_model_config` SET 
`doc_link` = 'https://alphacephei.com/vosk/',
`remark` = 'VOSK ASR configuration description：
1. VOSK is an offline speech recognition library supporting multiple languages
2. Need to download model files first：https://alphacephei.com/vosk/models
3. Chinese model recommended use vosk-model-small-cn-0.22 or vosk-model-cn-0.22
4. Fully offline operation，no network connection required
5. Output files are saved in tmp/ directory
Usage steps：
1. Visit https://alphacephei.com/vosk/models download the Chinese model
2. Extract the model files into the project directorys models/vosk/ folder
3. In the configuration specify the correct model path
4. Note：VOSK Chinese model output does not include punctuation，and there will be spaces between words
' WHERE `id` = 'ASR_VoskASR';
 