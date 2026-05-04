-- Add Aliyun Streaming TTS Provider
delete from `ai_model_provider` where id = 'SYSTEM_TTS_AliyunStreamTTS';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_TTS_AliyunStreamTTS', 'TTS', 'aliyun_stream', 'Aliyun Voice Synthesis (Streaming)', '[{"key":"appkey","label":"Application AppKey","type":"string"},{"key":"token","label":"Temporary Token","type":"string"},{"key":"access_key_id","label":"AccessKey ID","type":"string"},{"key":"access_key_secret","label":"AccessKey Secret","type":"string"},{"key":"host","label":"Service Address","type":"string"},{"key":"voice","label":"Default Voice","type":"string"},{"key":"format","label":"Audio Format","type":"string"},{"key":"sample_rate","label":"Sample Rate","type":"number"},{"key":"volume","label":"Volume","type":"number"},{"key":"speech_rate","label":"Speech Rate","type":"number"},{"key":"pitch_rate","label":"Pitch Rate","type":"number"},{"key":"output_dir","label":"Output Directory","type":"string"}]', 15, 1, NOW(), 1, NOW());

-- Add Aliyun Streaming TTS Model Configuration
delete from `ai_model_config` where id = 'TTS_AliyunStreamTTS';
INSERT INTO `ai_model_config` VALUES ('TTS_AliyunStreamTTS', 'TTS', 'AliyunStreamTTS', 'Aliyun Voice Synthesis (Streaming)', 0, 1, '{"type": "aliyun_stream", "appkey": "", "token": "", "access_key_id": "", "access_key_secret": "", "host": "nls-gateway-cn-beijing.aliyuncs.com", "voice": "longxiaochun", "format": "pcm", "sample_rate": 16000, "volume": 50, "speech_rate": 0, "pitch_rate": 0, "output_dir": "tmp/"}', NULL, NULL, 18, NULL, NULL, NULL, NULL);

-- Update Aliyun Streaming TTS Configuration Description
UPDATE `ai_model_config` SET 
`doc_link` = 'https://nls-portal.console.aliyun.com/',
`remark` = 'Aliyun Streaming TTS Configuration Description:
1. The difference between Aliyun TTS and Aliyun (Streaming) TTS is: Aliyun TTS is one-time synthesis, while Aliyun (Streaming) TTS is real-time streaming synthesis.
2. Streaming TTS has lower latency and better real-time performance, suitable for voice interaction scenarios.
3. You need to create an application and obtain credentials in the Aliyun Intelligent Speech Interaction console.
4. Supports CosyVoice large model voices, with more natural sound quality.
5. Supports real-time adjustment of parameters such as volume, speech rate, and pitch.
Application Steps:
1. Visit https://nls-portal.console.aliyun.com/ to activate the Intelligent Speech Interaction service.
2. Visit https://nls-portal.console.aliyun.com/applist to create a project and obtain the appkey.
3. Visit https://nls-portal.console.aliyun.com/overview to obtain a temporary token (or configure access_key_id and access_key_secret for automatic acquisition).
4. If dynamic token management is needed, it is recommended to configure access_key_id and access_key_secret.
5. You can select servers in different regions such as Beijing, Shanghai, etc. to optimize latency.
6. The voice parameter supports CosyVoice large model voices, such as longxiaochun, longyueyue, etc.
For more parameter configuration details, please refer to: https://help.aliyun.com/zh/isi/developer-reference/real-time-speech-synthesis'
WHERE `id` = 'TTS_AliyunStreamTTS';

-- Add Aliyun Streaming TTS Voices
-- Gentle Female Voice Series
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0001', 'TTS_AliyunStreamTTS', 'Long Xiao Chun - Gentle Sister', 'longxiaochun', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0002', 'TTS_AliyunStreamTTS', 'Long Xiao Xia - Gentle Female Voice', 'longxiaoxia', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0003', 'TTS_AliyunStreamTTS', 'Long Mei - Gentle Female Voice', 'longmei', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 3, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0004', 'TTS_AliyunStreamTTS', 'Long Gui - Gentle Female Voice', 'longgui', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 4, NULL, NULL, NULL, NULL);
-- Mature Female Voice Series
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0005', 'TTS_AliyunStreamTTS', 'Long Yu - Mature Female Voice', 'longyu', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 5, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0006', 'TTS_AliyunStreamTTS', 'Long Jiao - Mature Female Voice', 'longjiao', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 6, NULL, NULL, NULL, NULL);
-- Male Voice Series
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0007', 'TTS_AliyunStreamTTS', 'Long Chen - Dubbing Male Voice', 'longchen', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 7, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0008', 'TTS_AliyunStreamTTS', 'Long Xiu - Young Male Voice', 'longxiu', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 8, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0009', 'TTS_AliyunStreamTTS', 'Long Cheng - Sunny Male Voice', 'longcheng', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 9, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0010', 'TTS_AliyunStreamTTS', 'Long Zhe - Mature Male Voice', 'longzhe', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 10, NULL, NULL, NULL, NULL);
-- Professional Broadcasting Series
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0011', 'TTS_AliyunStreamTTS', 'Bella2.0 - News Female Voice', 'loongbella', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 11, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0012', 'TTS_AliyunStreamTTS', 'Stella2.0 - Dashing Female Voice', 'loongstella', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 12, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0013', 'TTS_AliyunStreamTTS', 'Long Shu - News Male Voice', 'longshu', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 13, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0014', 'TTS_AliyunStreamTTS', 'Long Jing - Serious Female Voice', 'longjing', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 14, NULL, NULL, NULL, NULL);
-- Special Voice Series
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0015', 'TTS_AliyunStreamTTS', 'Long Qi - Lively Child Voice', 'longqi', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 15, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0016', 'TTS_AliyunStreamTTS', 'Long Hua - Lively Girl Voice', 'longhua', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 16, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0017', 'TTS_AliyunStreamTTS', 'Long Wu - Witty Male Voice', 'longwu', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 17, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0018', 'TTS_AliyunStreamTTS', 'Long Da Chui - Humorous Male Voice', 'longdachui', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 18, NULL, NULL, NULL, NULL);
-- Cantonese Series
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0019', 'TTS_AliyunStreamTTS', 'Long Jia Yi - Cantonese Female Voice', 'longjiayi', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 19, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliyunStreamTTS_0020', 'TTS_AliyunStreamTTS', 'Long Tao - Cantonese Female Voice', 'longtao', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 20, NULL, NULL, NULL, NULL);
 
 