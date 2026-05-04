-- Fix Doubao speech synthesis model 2.0 provider_code duplicate issue, add ASR 2.0 support
-- ==================== Doubao Speech Synthesis Model 2.0 ====================
-- Delete TTS 2.0 provider (no longer need a separate provider)
delete from `ai_model_provider` where id = 'SYSTEM_TTS_HSDSTTS_V2';
-- ==================== Doubao Speech Recognition (Streaming) ====================
-- Correct the existing Doubao speech recognition (streaming) provider, remove cluster field, add resource_id field
UPDATE `ai_model_provider` SET `fields` = '[{"key":"appid","type":"string","label":"App ID"},{"key":"access_token","type":"string","label":"Access Token"},{"key":"boosting_table_name","type":"string","label":"Hotword File Name"},{"key":"correct_table_name","type":"string","label":"Replacement Word File Name"},{"key":"output_dir","type":"string","label":"Output Directory"},{"key":"end_window_size","type":"number","label":"Silence Judgment Duration (ms)"},{"key":"enable_multilingual","type":"boolean","label":"Whether to Enable Multilingual Recognition Mode"},{"key":"language","type":"string","label":"Specified Language Code"},{"key":"resource_id","type":"string","label":"Resource ID"}]' WHERE `id` = 'SYSTEM_ASR_DoubaoStreamASR';
-- Correct the existing Doubao speech recognition (streaming) configuration, remove cluster field, add resource_id default value
UPDATE `ai_model_config` SET `config_json` = JSON_REMOVE(JSON_SET(`config_json`, '$.resource_id', 'volc.bigasr.sauc.duration'), '$.cluster') WHERE `id` = 'ASR_DoubaoStreamASR';
-- ==================== Doubao Speech Recognition Model 2.0 ====================
-- Insert Doubao Speech Recognition Model 2.0 configuration
delete from `ai_model_config` where id = 'ASR_DoubaoStreamASRV2';
INSERT INTO `ai_model_config` VALUES ('ASR_DoubaoStreamASRV2', 'ASR', 'DoubaoStreamASRV2', 'Doubao Speech Recognition Model 2.0', 0, 1, '{
  "type": "doubao_stream",
  "appid": "",
  "access_token": "",
  "resource_id": "volc.seedasr.sauc.duration",
  "end_window_size": 200,
  "enable_multilingual": false,
  "language": "zh-CN",
  "output_dir": "tmp/"
}', NULL, NULL, 6, NULL, NULL, NULL, NULL);
-- Doubao Speech Recognition Model 2.0 configuration documentation
UPDATE `ai_model_config` SET
`doc_link` = 'https://www.volcengine.com/docs/6561/109979',
`remark` = 'Doubao Speech Recognition Model 2.0 configuration instructions (based on Volcano Engine seed-asr):
1. Visit https://www.volcengine.com/ to register and activate a Volcano Engine account
2. Visit https://console.volcengine.com/speech/service/10038 to enable Doubao Streaming Speech Recognition Model 2.0
3. Obtain appid and access_token at the bottom of the page
4. There are two types of resource IDs: hourly version (volc.seedasr.sauc.duration) and concurrent version (volc.seedasr.sauc.concurrent)
   - Hourly version: fixed as volc.seedasr.sauc.duration (Doubao Speech Recognition Model 2.0)
   - Concurrent version: fixed as volc.seedasr.sauc.concurrent (Doubao Speech Recognition Model 2.0)

Detailed parameter documentation: https://www.volcengine.com/docs/6561/109979

Note:
- Doubao Speech Recognition Model 2.0 uses resource ID volc.seedasr.sauc.duration, which differs from Doubao Speech Recognition (Streaming) (volc.bigasr.sauc.duration)
- Speech Recognition Model 2.0 is more affordable; it is recommended to use the concurrent version resource ID in high-concurrency scenarios
' WHERE `id` = 'ASR_DoubaoStreamASRV2';
 