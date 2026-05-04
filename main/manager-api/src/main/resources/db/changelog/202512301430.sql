-- Add Aliyun Bailian Paraformer Real-time Speech Recognition Service Configuration
delete from `ai_model_provider` where id = 'SYSTEM_ASR_AliyunBLStream';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_ASR_AliyunBLStream', 'ASR', 'aliyunbl_stream', 'Aliyun Bailian Paraformer Real-time Speech Recognition', '[{"key":"api_key","label":"API Key","type":"password"},{"key":"model","label":"Model Name","type":"string"},{"key":"format","label":"Audio Format","type":"string"},{"key":"sample_rate","label":"Sample Rate","type":"number"},{"key":"output_dir","label":"Output Directory","type":"string"}]', 18, 1, NOW(), 1, NOW());

delete from `ai_model_config` where id = 'ASR_AliyunBLStream';
INSERT INTO `ai_model_config` VALUES ('ASR_AliyunBLStream', 'ASR', 'AliyunBLStream', 'Aliyun Bailian Paraformer Real-time Speech Recognition', 0, 1, '{"type": "aliyunbl_stream", "api_key": "", "model": "paraformer-realtime-v2", "format": "pcm", "sample_rate": 16000, "disfluency_removal_enabled": false, "semantic_punctuation_enabled": false, "max_sentence_silence": 200, "multi_threshold_mode_enabled": false, "punctuation_prediction_enabled": true, "inverse_text_normalization_enabled": true, "output_dir": "tmp/"}', 'https://help.aliyun.com/zh/model-studio/websocket-for-paraformer-real-time-service', 'Supports multi-language, hotword customization, semantic sentence breaking and other advanced features', 21, NULL, NULL, NULL, NULL);

-- Update Aliyun Bailian Paraformer model configuration description documentation
UPDATE `ai_model_config` SET
`doc_link` = 'https://help.aliyun.com/zh/model-studio/websocket-for-paraformer-real-time-service',
`remark` = 'Aliyun Bailian Paraformer Real-time Speech Recognition Configuration Instructions:
1. Log in to Aliyun Bailian platform https://bailian.console.aliyun.com/
2. Create API-KEY https://bailian.console.aliyun.com/#/api-key
3. Supported models: paraformer-realtime-v2 (recommended), paraformer-realtime-8k-v2, paraformer-realtime-v1, paraformer-realtime-8k-v1
4. Feature highlights:
   - Multi-language support (Chinese including dialects, English, Japanese, Korean, German, French, Russian)
   - Hotword customization (vocabulary_id parameter), for details refer to: https://help.aliyun.com/zh/model-studio/custom-hot-words?
   - Semantic sentence breaking / VAD sentence breaking (semantic_punctuation_enabled parameter)
   - Automatic punctuation, ITN, filtering of modal particles, etc.
5. Parameter explanations:
   - model: Model name, recommended paraformer-realtime-v2
   - sample_rate: Sample rate (Hz), v2 supports any sample rate, v1 only supports 16000, 8k version only supports 8000
   - semantic_punctuation_enabled: false for VAD sentence breaking (low latency), true for semantic sentence breaking (high accuracy)
   - max_sentence_silence: VAD sentence breaking silence duration threshold (200-6000ms)
' WHERE `id` = 'ASR_AliyunBLStream';


-- Update Doubao streaming ASR provider, add configuration
delete from `ai_model_provider` where id = 'SYSTEM_ASR_DoubaoStreamASR';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_ASR_DoubaoStreamASR', 'ASR', 'doubao_stream', 'Volcano Engine Speech Recognition (Streaming)', '[{"key":"appid","label":"App ID","type":"string"},{"key":"access_token","label":"Access Token","type":"string"},{"key":"cluster","label":"Cluster","type":"string"},{"key":"boosting_table_name","label":"Hotword File Name","type":"string"},{"key":"correct_table_name","label":"Replacement Word File Name","type":"string"},{"key":"output_dir","label":"Output Directory","type":"string"},{"key":"end_window_size","label":"Silence Judgment Duration (ms)","type":"number"},{"key":"enable_multilingual","label":"Whether to enable multi-language recognition mode","type":"boolean"},{"key":"language","label":"Specify language code","type":"string"}]', 3, 1, NOW(), 1, NOW());
UPDATE `ai_model_config` SET 
`remark` = 'Doubao ASR Configuration Instructions:
1. The difference between Doubao ASR and Doubao (streaming) ASR is: Doubao ASR is pay-per-use, while Doubao (streaming) ASR is pay-per-time.
2. Generally, pay-per-use is cheaper, but Doubao (streaming) ASR uses large model technology, resulting in better performance.
3. You need to create an application in the Volcano Engine console and obtain appid and access_token.
4. Supports Chinese speech recognition.
5. Requires network connection.
6. Output files are saved in the tmp/ directory.
Application steps:
1. Visit https://console.volcengine.com/speech/app
2. Create a new application
3. Obtain appid and access_token
4. Fill in the configuration file
If you need to set hotwords, please refer to: https://www.volcengine.com/docs/6561/155738
If you enable multi-language recognition mode, please set the language parameter. When this key is empty, the model supports Chinese, English, Shanghainese, Minnanese, Sichuan, Shaanxi, and Cantonese recognition. For other languages, please refer to: https://www.volcengine.com/docs/6561/1354869
' WHERE `id` = 'ASR_DoubaoStreamASR';

-- Update Doubao streaming ASR model configuration, add enable_multilingual default value
UPDATE `ai_model_config` SET
`config_json` = JSON_SET(
    `config_json`, 
    '$.enable_multilingual', false,
    '$.language', 'zh-CN'
)
WHERE `id` = 'ASR_DoubaoStreamASR' 
AND JSON_EXTRACT(`config_json`, '$.enable_multilingual') IS NULL 
AND JSON_EXTRACT(`config_json`, '$.language') IS NULL;


-- Update HuoshanDoubleStreamTTS provider configuration, add multi-emotion tone parameters
UPDATE `ai_model_provider`
SET `fields` = '[{"key": "ws_url", "type": "string", "label": "WebSocket Address"}, {"key": "appid", "type": "string", "label": "App ID"}, {"key": "access_token", "type": "string", "label": "Access Token"}, {"key": "resource_id", "type": "string", "label": "Resource ID"}, {"key": "speaker", "type": "string", "label": "Default Speaker"}, {"key": "enable_ws_reuse", "type": "boolean", "label": "Whether to enable connection reuse", "default": true}, {"key": "speech_rate", "type": "number", "label": "Speech Rate (-50~100)"}, {"key": "loudness_rate", "type": "number", "label": "Volume (-50~100)"}, {"key": "pitch", "type": "number", "label": "Pitch (-12~12)"}, {"key": "emotion_scale", "type": "number", "label": "Emotion Intensity (1-5)"}, {"key": "emotion", "type": "string", "label": "Emotion Type"}]'
WHERE `id` = 'SYSTEM_TTS_HSDSTTS';

-- Update default values
UPDATE `ai_model_config` SET
`config_json` = JSON_SET(
    `config_json`,
    '$.emotion', 'neutral',
    '$.emotion_scale', 4
)
WHERE `id` = 'TTS_HuoshanDoubleStreamTTS'
AND JSON_EXTRACT(`config_json`, '$.emotion') IS NULL 
AND JSON_EXTRACT(`config_json`, '$.emotion_scale') IS NULL;

-- Add document link and remark
UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.volcengine.com/speech/service/10007',
`remark` = 'Volcano Engine Speech Synthesis Service Configuration Instructions:
1. Visit https://www.volcengine.com/ to register and activate a Volcano Engine account
2. Visit https://console.volcengine.com/speech/service/10007 to activate the speech synthesis large model and purchase voices
3. Obtain appid and access_token at the bottom of the page
5. Resource ID is fixed as: volc.service_type.10029 (large model speech synthesis and mixing)
6. Connection reuse: Enable WebSocket connection reuse, default true to reduce connection overhead (note: after reuse, idle connections while the device is in listening state will occupy concurrency)
7. Speech rate: -50~100, can be left blank, normal default value 0, can fill -50~100
8. Volume: -50~100, can be left blank, normal default value 0, can fill -50~100
9. Pitch: -12~12, can be left blank, normal default value 0, can fill -12~12
10. Multi-emotion parameters (currently only some voices support setting emotion):
    Related voice list: https://www.volcengine.com/docs/6561/1257544
    - emotion_scale: Emotion intensity, optional values: 1~5, default value 4
    - emotion: Emotion type, optional values: neutral, happy, sad, angry, fearful, disgusted, surprised
' WHERE `id` = 'TTS_HuoshanDoubleStreamTTS';
 