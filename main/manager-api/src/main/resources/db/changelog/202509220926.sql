-- Add Xunfei Streaming Speech Recognition Service Configuration
delete from `ai_model_provider` where id = 'SYSTEM_ASR_XunfeiStream';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_ASR_XunfeiStream', 'ASR', 'xunfei_stream', 'Xunfei Streaming Speech Recognition', '[{"key":"app_id","label":"Application ID","type":"string"},{"key":"api_key","label":"API_KEY","type":"password"},{"key":"api_secret","label":"API_SECRET","type":"password"},{"key":"domain","label":"Recognition Domain","type":"string"},{"key":"language","label":"Recognition Language","type":"string"},{"key":"accent","label":"Dialect","type":"string"},{"key":"dwa","label":"Dynamic Correction","type":"string"},{"key":"output_dir","label":"Output Directory","type":"string"}]', 18, 1, NOW(), 1, NOW());

delete from `ai_model_config` where id = 'ASR_XunfeiStream';
INSERT INTO `ai_model_config` VALUES ('ASR_XunfeiStream', 'ASR', 'Xunfei Streaming Speech Recognition', 'Xunfei Streaming Speech Recognition Service', 0, 1, '{"type": "xunfei_stream", "app_id": "", "api_key": "", "api_secret": "", "domain": "slm", "language": "zh_cn", "accent": "mandarin", "dwa": "wpgs", "output_dir": "tmp/"}', 'https://www.xfyun.cn/doc/spark/spark_zh_iat.html', 'Supports real-time streaming speech recognition, suitable for Chinese Mandarin and multiple dialect recognition', 21, NULL, NULL, NULL, NULL);

-- Update Xunfei Streaming Speech Recognition Model Configuration Documentation
UPDATE `ai_model_config` SET
`doc_link` = 'https://www.xfyun.cn/doc/spark/spark_zh_iat.html',
`remark` = 'Xunfei Streaming Speech Recognition Configuration Description：
1. Log in to the Xunfei Open Platform https://www.xfyun.cn/
2. Create a speech recognition application to obtain APPID, APISecret, and APIKey
3. Parameter Description：
   - app_id: Application ID, obtained after creating an application on the Xunfei Open Platform
   - api_key: API Key, used for interface authentication
   - api_secret: API Secret, used to generate signature
   - domain: Recognition domain, default slm (intelligent speech transcription)
   - language: Recognition language, default zh_cn (Chinese)
   - accent: Dialect type, default mandarin (Mandarin), supports cantonese (Cantonese) etc
   - dwa: Dynamic correction, default wpgs (enable dynamic correction)
   - output_dir: Audio file output directory, default tmp/
4. Supports real-time streaming recognition, suitable for real-time voice interaction scenarios
5. Supports multiple dialect and language recognition
' WHERE `id` = 'ASR_XunfeiStream';
 