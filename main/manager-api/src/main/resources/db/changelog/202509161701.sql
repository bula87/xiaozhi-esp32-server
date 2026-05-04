-- Add Aliyun Bailian Streaming TTS Provider
delete from `ai_model_provider` where id = 'SYSTEM_TTS_AliBLStreamTTS';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_TTS_AliBLStreamTTS', 'TTS', 'alibl_stream', 'Aliyun Bailian Streaming Speech Synthesis', '[{"key":"api_key","label":"API Key","type":"string"},{"key":"output_dir","label":"Output Directory","type":"string"},{"key":"model","label":"Model","type":"string"},{"key":"voice","label":"Voice","type":"string"},{"key":"format","label":"Audio Format","type":"string"},{"key":"sample_rate","label":"Sample Rate","type":"number"},{"key": "volume", "type": "number", "label": "Volume"},{"key": "rate", "type": "number", "label": "Rate"},{"key": "pitch", "type": "number", "label": "Pitch"}]', 19, 1, NOW(), 1, NOW());

-- Add Aliyun Bailian Streaming TTS Model Configuration
delete from `ai_model_config` where id = 'TTS_AliBLStreamTTS';
INSERT INTO `ai_model_config` VALUES ('TTS_AliBLStreamTTS', 'TTS', 'AliBLStreamTTS', 'Aliyun Bailian Streaming Speech Synthesis', 0, 1, '{\"type\": \"alibl_stream\", \"appkey\": \"\", \"output_dir\": \"tmp/\", \"model\": \"cosyvoice-v2\", \"voice\": \"longcheng_v2\", \"format\": \"pcm\", \"sample_rate\": 24000, \"volume\": 50, \"rate\": 1, \"pitch\": 1}', NULL, NULL, 22, NULL, NULL, NULL, NULL);

-- Update Aliyun Bailian Streaming TTS Configuration Description
UPDATE `ai_model_config` SET 
`doc_link` = 'https://bailian.console.aliyun.com/?apiKey=1#/api-key',
`remark` = 'Aliyun Bailian Streaming TTS Notes:
1. Visit https://bailian.console.aliyun.com/?apiKey=1#/api-key to create a project and obtain the appkey
2. Supports real-time streaming synthesis with low latency
3. Supports multiple voice settings and audio parameter adjustments
4. Supports CosyVoice-V3 large model voices, cost-effective (0.4 yuan per 10,000 characters)
5. Supports real-time adjustment of volume, speed, pitch and other parameters
6. If you need to use the CosyVoice-V3 model and some restricted voice types, please contact Aliyun Bailian customer service to apply
' WHERE `id` = 'TTS_AliBLStreamTTS';

-- Add Aliyun Bailian Streaming TTS Voice
delete from `ai_tts_voice` where tts_model_id = 'TTS_AliBLStreamTTS';

-- Voice Assistant
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0001', 'TTS_AliBLStreamTTS', 'dragonlittlehonest-Intellectual and Positive Female', 'longxiaochun_v2', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0002', 'TTS_AliBLStreamTTS', 'dragonlittlesummer-Steady and Authoritative Female', 'longxiaoxia_v2', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL);

-- Live Streaming Sales
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0003', 'TTS_AliBLStreamTTS', 'dragonpeaceburn-Lively and Textured Female', 'longanran', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 3, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0004', 'TTS_AliBLStreamTTS', 'dragonpeaceannounce-Classic Live Streaming Female', 'longanxuan', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 4, NULL, NULL, NULL, NULL);

-- Social Companionship
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0005', 'TTS_AliBLStreamTTS', 'dragoncold-Warm and Infatuated Male', 'longhan_v2', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 5, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0006', 'TTS_AliBLStreamTTS', 'dragonface-Warm Spring Breeze Female', 'longyan_v2', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 6, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0007', 'TTS_AliBLStreamTTS', 'dragonsplendidsplendid-Sweet and Sensitive Female', 'longfeifei_v2', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 7, NULL, NULL, NULL, NULL);

-- Dialect
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0008', 'TTS_AliBLStreamTTS', 'dragonoldiron-Northeast Straightforward Male', 'longlaotie_v2', 'Chinese (Northeast) and Chinese-English mixed', NULL, NULL, NULL, NULL, 8, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0009', 'TTS_AliBLStreamTTS', 'dragongoodjoyful-Intellectual Cantonese Female', 'longjiayi_v2', 'Chinese (Cantonese) and Chinese-English mixed', NULL, NULL, NULL, NULL, 9, NULL, NULL, NULL, NULL);

-- Child Voice
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0010', 'TTS_AliBLStreamTTS', 'dragonexcellentpowerbean-Sunny and Playful Male', 'longjielidou_v2', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 10, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0011', 'TTS_AliBLStreamTTS', 'dragonbell-Innocent and Stiff Female', 'longling_v2', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 11, NULL, NULL, NULL, NULL);

-- Poetry Recitation
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0012', 'TTS_AliBLStreamTTS', 'plumwhite-Ancient Poetry Immortal Male', 'libai_v2', 'Chinese and Chinese-English mixed', NULL, NULL, NULL, NULL, 12, NULL, NULL, NULL, NULL);

-- Overseas Marketing
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0013', 'TTS_AliBLStreamTTS', 'loongeva-Intellectual English Female', 'loongeva_v2', 'British English', NULL, NULL, NULL, NULL, 13, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0014', 'TTS_AliBLStreamTTS', 'loongbrian-Steady English Male', 'loongbrian_v2', 'British English', NULL, NULL, NULL, NULL, 14, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0015', 'TTS_AliBLStreamTTS', 'loongkyong-Korean Female', 'loongkyong_v2', 'Korean', NULL, NULL, NULL, NULL, 15, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0016', 'TTS_AliBLStreamTTS', 'loongtomoka-Japanese Female', 'loongtomoka_v2', 'Japanese', NULL, NULL, NULL, NULL, 16, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_AliBLStreamTTS_0017', 'TTS_AliBLStreamTTS', 'loongtomoya-Japanese Male', 'loongtomoya_v2', 'Japanese', NULL, NULL, NULL, NULL, 17, NULL, NULL, NULL, NULL);
 