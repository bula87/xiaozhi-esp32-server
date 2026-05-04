-- Update HuoshanDoubleStreamTTS provider configuration, change scattered parameters to JSON dictionary configuration
UPDATE `ai_model_provider`
SET `fields` = '[
  {"key": "ws_url", "type": "string", "label": "WebSocket Address"},
  {"key": "appid", "type": "string", "label": "App ID"},
  {"key": "access_token", "type": "string", "label": "Access Token"},
  {"key": "resource_id", "type": "string", "label": "Resource ID"},
  {"key": "speaker", "type": "string", "label": "Default Voice"},
  {"key": "enable_ws_reuse", "type": "boolean", "label": "Whether to Enable Connection Reuse", "default": true},
  {"key": "audio_params", "type": "dict", "label": "Audio Output Configuration"},
  {"key": "additions", "type": "dict", "label": "Advanced Text Processing Configuration"},
  {"key": "mix_speaker", "type": "dict", "label": "Mixing Control Configuration"}
]'
WHERE `id` = 'SYSTEM_TTS_HSDSTTS';

-- Consolidate speech_rate, loudness_rate, pitch, emotion, emotion_scale etc parameters into audio_params, additions, mix_speaker three JSON dictionaries
-- Update existing configuration, migrate old scattered parameters to new JSON dictionary structure
UPDATE `ai_model_config`
SET `config_json` = JSON_SET(
    `config_json`,
    '$.audio_params', JSON_OBJECT(
        'speech_rate', CAST(COALESCE(NULLIF(JSON_UNQUOTE(JSON_EXTRACT(`config_json`, '$.speech_rate')), ''), '0') AS SIGNED),
        'loudness_rate', CAST(COALESCE(NULLIF(JSON_UNQUOTE(JSON_EXTRACT(`config_json`, '$.loudness_rate')), ''), '0') AS SIGNED)
    ),
    '$.additions', JSON_OBJECT(
        'aigc_metadata', JSON_OBJECT(),
        'cache_config', JSON_OBJECT(),
        'post_process', JSON_OBJECT(
            'pitch', CAST(COALESCE(NULLIF(JSON_UNQUOTE(JSON_EXTRACT(`config_json`, '$.pitch')), ''), '0') AS SIGNED)
        )
    ),
    '$.mix_speaker', JSON_OBJECT()
)
WHERE `id` = 'TTS_HuoshanDoubleStreamTTS';

-- Delete old scattered parameter fields
UPDATE `ai_model_config`
SET `config_json` = JSON_REMOVE(
    `config_json`,
    '$.speech_rate',
    '$.loudness_rate',
    '$.pitch',
    '$.emotion',
    '$.emotion_scale'
)
WHERE `id` = 'TTS_HuoshanDoubleStreamTTS';

-- Update documentation link and remark description
UPDATE `ai_model_config` SET
`doc_link` = 'https://www.volcengine.com/docs/6561/1329505',
`remark` = 'Volcano Engine Bidirectional Streaming TTS Configuration Description:
1. Visit https://www.volcengine.com/ to register and activate a Volcano Engine account
2. Visit https://console.volcengine.com/speech/service/10007 to activate the speech synthesis large model and purchase voices
3. Obtain the appid and access_token at the bottom of the page
4. The resource ID is fixed as: volc.service_type.10029 (large model speech synthesis and mixing)
5. Connection reuse: Enable WebSocket connection reuse, default true reduces connection loss (note: after reuse, when the device is in listening state, idle connections will occupy concurrent connections)

Detailed parameter documentation: https://www.volcengine.com/docs/6561/1329505
[audio_params] Audio Output Configuration - Users can custom-add any audio parameters supported by Volcano Engine
  - speech_rate: Speech rate (-50~100), default 0
  - loudness_rate: Volume (-50~100), default 0
  - emotion: Emotion type (only supported by some voices), optional values: neutral, happy, sad, angry, fearful, disgusted, surprised
  - emotion_scale: Emotion intensity (1~5), default 4
  Example: {"speech_rate": 10, "loudness_rate": 5, "emotion": "happy", "emotion_scale": 4}

[additions] Advanced Text Processing Configuration - Users can custom-add any advanced parameters supported by Volcano Engine
  - post_process.pitch: Pitch (-12~12), default 0
  - aigc_metadata: AIGC metadata configuration
  - cache_config: Cache configuration
  Example: {"post_process": {"pitch": 2}, "aigc_metadata": {}, "cache_config": {}}

[mix_speaker] Mixing Control Configuration - Multi-voice mixing (TTS 1.0 only)
  Example:
    {"speakers": [
      {"source_speaker": "zh_male_bvlazysheep","mix_factor": 0.3}, 
      {"source_speaker": "BV120_streaming","mix_factor": 0.3}, 
      {"source_speaker": "zh_male_ahu_conversation_wvae_bigtts","mix_factor": 0.4}
    ]}

Note:
- Multi-emotion voice parameters (emotion, emotion_scale) are only supported by some voices
- Related voice list: https://www.volcengine.com/docs/6561/1257544
- Users can add more parameters based on the Volcano Engine API documentation
- The mixing function mainly applies to the Doubao voice synthesis model 1.0 voices; when using it, you need to set req_params.speaker to custom_mix_bigtts
'
WHERE `id` = 'TTS_HuoshanDoubleStreamTTS';
 
 