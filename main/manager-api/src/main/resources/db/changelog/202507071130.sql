-- Add Alibaba Cloud Streaming ASR Provider
delete from `ai_model_provider` where id = 'SYSTEM_ASR_AliyunStreamASR';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_ASR_AliyunStreamASR', 'ASR', 'aliyun_stream', 'Aliyun Speech Recognition (Streaming)', '[{"key":"appkey","label":"AppKey","type":"string"},{"key":"token","label":"Temporary Token","type":"string"},{"key":"access_key_id","label":"AccessKey ID","type":"string"},{"key":"access_key_secret","label":"AccessKey Secret","type":"string"},{"key":"host","label":"Service Address","type":"string"},{"key":"max_sentence_silence","label":"Sentence Detection Time (ms)","type":"number"},{"key":"output_dir","label":"Output Directory","type":"string"}]', 6, 1, NOW(), 1, NOW());

-- Add Alibaba Cloud Streaming ASR Model Configuration
delete from `ai_model_config` where id = 'ASR_AliyunStreamASR';
INSERT INTO `ai_model_config` VALUES ('ASR_AliyunStreamASR', 'ASR', 'AliyunStreamASR', 'Aliyun Speech Recognition (Streaming)', 0, 1, '{\"type\": \"aliyun_stream\", \"appkey\": \"\", \"token\": \"\", \"access_key_id\": \"\", \"access_key_secret\": \"\", \"host\": \"nls-gateway-cn-shanghai.aliyuncs.com\", \"max_sentence_silence\": 800, \"output_dir\": \"tmp/\"}', NULL, NULL, 8, NULL, NULL, NULL, NULL);

-- Update Alibaba Cloud Streaming ASR Configuration Description
UPDATE `ai_model_config` SET 
`doc_link` = 'https://nls-portal.console.aliyun.com/',
`remark` = 'Aliyun Streaming ASR Configuration Instructions:
1. The difference between Aliyun ASR and Aliyun (Streaming) ASR: Aliyun ASR is one-time recognition, Aliyun (Streaming) ASR is real-time streaming recognition
2. Streaming ASR has lower latency and better real-time performance, suitable for voice interaction scenarios
3. You need to create an application and obtain authentication info in the Aliyun Intelligent Voice Interaction Console
4. Supports real-time Chinese speech recognition, punctuation prediction, and inverse text normalization
5. Requires network connection, output files are saved in the tmp/ directory
Application steps:
1. Visit https://nls-portal.console.aliyun.com/ to enable the Intelligent Voice Interaction service
2. Visit https://nls-portal.console.aliyun.com/applist to create a project and get the appkey
3. Visit https://nls-portal.console.aliyun.com/overview to get a temporary token (or configure access_key_id and access_key_secret for automatic retrieval)
4. For dynamic token management, it is recommended to configure access_key_id and access_key_secret
5. The max_sentence_silence parameter controls sentence detection time (ms), default is 800ms
For more parameter configuration, see: https://help.aliyun.com/zh/isi/developer-reference/real-time-speech-recognition
' WHERE `id` = 'ASR_AliyunStreamASR';
 