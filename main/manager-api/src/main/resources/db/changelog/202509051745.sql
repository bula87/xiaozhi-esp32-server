-- Add MinimaxHTTPStream streaming TTS provider
delete from `ai_model_provider` where id = 'SYSTEM_TTS_MinimaxStreamTTS';
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
('SYSTEM_TTS_MinimaxStreamTTS', 'TTS', 'minimax_httpstream', 'Minimax Streaming Speech Synthesis', '[{"key":"group_id","label":"Group ID","type":"string"},{"key":"api_key","label":"API Key","type":"string"},{"key":"model","label":"Model","type":"string"},{"key":"voice_id","label":"Voice ID","type":"string"},{"key":"output_dir","label":"Output Directory","type":"string"},{"key":"voice_setting","label":"Voice Settings","type":"dict","dict_name":"voice_setting"},{"key":"pronunciation_dict","label":"Pronunciation Dictionary","type":"dict","dict_name":"pronunciation_dict"},{"key":"audio_setting","label":"Audio Settings","type":"dict","dict_name":"audio_setting"},{"key":"timber_weights","label":"Voice Weights","type":"string"}]', 18, 1, NOW(), 1, NOW());

-- Add Minimax streaming TTS model configuration
delete from `ai_model_config` where id = 'TTS_MinimaxStreamTTS';
INSERT INTO `ai_model_config` VALUES ('TTS_MinimaxStreamTTS', 'TTS', 'MinimaxStreamTTS', 'Minimax Streaming Speech Synthesis', 0, 1, '{"type": "minimax_httpstream", "group_id": "", "api_key": "", "model": "speech-01-turbo", "voice_id": "female-shaonv", "output_dir": "tmp/", "voice_setting": {"speed": 1, "vol": 1, "pitch": 0, "emotion": "happy"}, "pronunciation_dict": {"tone": ["Processing/(chu3)(li3)", "Dangerous/dangerous"]}, "audio_setting": {"sample_rate": 24000, "bitrate": 128000, "format": "pcm", "channel": 1}}', NULL, NULL, 21, NULL, NULL, NULL, NULL);

-- Update Minimax streaming TTS configuration description
UPDATE `ai_model_config` SET 
`doc_link` = 'https://platform.minimaxi.com/',
`remark` = 'Minimax Streaming TTS Configuration Instructions:
1. Need to apply for a Minimax API Key first
2. Need to fill in the Group ID
3. Supports multiple voice settings and audio parameter adjustments
4. Supports real-time streaming synthesis with low latency
5. Supports custom pronunciation dictionary and voice weights
6. Hidden parameter configurations: Voice Settings (voice_setting), Pronunciation Dictionary (pronunciation_dict), Voice Weights (timber_weights)
   - Speed (speed): Range [0.5,2], default 1.0, the larger the value, the faster the speech
   - Volume (vol): Range (0,10], default 1.0, the larger the value, the higher the volume
   - Pitch (pitch): Range [-12,12], default 0, value must be an integer
   - Emotion (emotion): Controls the emotion of synthesized speech, supports 7 values: ["happy", "sad", "angry", "fearful", "disgusted", "surprised", "calm"], this parameter only applies to speech-2.5-hd-preview, speech-2.5-turbo-preview, speech-02-hd, speech-02-turbo, speech-01-turbo, speech-01-hd
   - Either timbre_weights or voice_id must be filled in
   - voice_id (requested voice ID, must be filled in together with the weight parameter)
   - weight (weight, supports up to 4 voice blends. Range [1,100])
' WHERE `id` = 'TTS_MinimaxStreamTTS';

-- Add Minimax streaming TTS voice
delete from `ai_tts_voice` where tts_model_id = 'TTS_MinimaxStreamTTS';

-- Default voice
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0001', 'TTS_MinimaxStreamTTS', 'Young Girl Voice', 'female-shaonv', 'Chinese', NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0002', 'TTS_MinimaxStreamTTS', 'Mature Female Voice', 'female-chengshu', 'Chinese', NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0003', 'TTS_MinimaxStreamTTS', 'Bossy Young Master', 'badao_shaoye', 'Chinese', NULL, NULL, NULL, NULL, 3, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0004', 'TTS_MinimaxStreamTTS', 'Tsundere Little Brother', 'bingjiao_didi', 'Chinese', NULL, NULL, NULL, NULL, 4, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0005', 'TTS_MinimaxStreamTTS', 'Innocent Junior', 'chunzhen_xuedi', 'Chinese', NULL, NULL, NULL, NULL, 5, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0006', 'TTS_MinimaxStreamTTS', 'Indifferent Senior', 'lengdan_xiongzhang', 'Chinese', NULL, NULL, NULL, NULL, 6, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0007', 'TTS_MinimaxStreamTTS', 'Sweet Little Ling', 'tianxin_xiaoling', 'Chinese', NULL, NULL, NULL, NULL, 7, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0008', 'TTS_MinimaxStreamTTS', 'Playful Cute Girl', 'qiaopi_mengmei', 'Chinese', NULL, NULL, NULL, NULL, 8, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0009', 'TTS_MinimaxStreamTTS', 'Charming Big Sister', 'wumei_yujie', 'Chinese', NULL, NULL, NULL, NULL, 9, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0010', 'TTS_MinimaxStreamTTS', 'Cute Little Sister', 'diadia_xuemei', 'Chinese', NULL, NULL, NULL, NULL, 7, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0011', 'TTS_MinimaxStreamTTS', 'Elegant Senior', 'danya_xuejie', 'Chinese', NULL, NULL, NULL, NULL, 8, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0012', 'TTS_MinimaxStreamTTS', 'Santa Claus', 'Santa_Claus', 'Chinese', NULL, NULL, NULL, NULL, 9, NULL, NULL, NULL, NULL);
INSERT INTO `ai_tts_voice` VALUES ('TTS_MinimaxStreamTTS_0013', 'TTS_MinimaxStreamTTS', 'Grinch', 'Grinch', 'Chinese', NULL, NULL, NULL, NULL, 10, NULL, NULL, NULL, NULL);
 
 