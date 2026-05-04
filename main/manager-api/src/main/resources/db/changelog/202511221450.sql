-- Update HuoshanDoubleStreamTTS provider configuration, add enable connection reuse option
UPDATE `ai_model_provider`
SET fields = '[{"key": "ws_url", "type": "string", "label": "WebSocket address"}, {"key": "appid", "type": "string", "label": "Application ID"}, {"key": "access_token", "type": "string", "label": "Access token"}, {"key": "resource_id", "type": "string", "label": "Resource ID"}, {"key": "speaker", "type": "string", "label": "Default voice"}, {"key": "enable_ws_reuse", "type": "boolean", "label": "Whether to enable connection reuse", "default": true}, {"key": "speech_rate", "type": "number", "label": "Speech rate(-50~100)"}, {"key": "loudness_rate", "type": "number", "label": "Volume(-50~100)"}, {"key": "pitch", "type": "number", "label": "Pitch(-12~12)"}]'
WHERE id = 'SYSTEM_TTS_HSDSTTS';

UPDATE `ai_model_config` SET 
`doc_link` = 'https://console.volcengine.com/speech/service/10007',
`remark` = 'VolcEngine speech synthesis service configuration instructions:
1. Visit https://www.volcengine.com/ to register and activate a VolcEngine account
2. Visit https://console.volcengine.com/speech/service/10007 to activate the speech synthesis large model and purchase voices
3. Obtain appid and access_token at the bottom of the page
5. Resource ID is fixed as: volc.service_type.10029 (large model speech synthesis and mixing)
6. Connection reuse: Enable WebSocket connection reuse, default true reduces connection consumption (note: after reuse, when the device is in listening state, idle connection will occupy concurrent number)
7. Speech rate: -50~100, can be left empty, normal default value 0, can fill -50~100
8. Volume: -50~100, can be left empty, normal default value 0, can fill -50~100
9. Pitch: -12~12, can be left empty, normal default value 0, can fill -12~12
10. Fill into the configuration file' WHERE `id` = 'TTS_HuoshanDoubleStreamTTS';
 