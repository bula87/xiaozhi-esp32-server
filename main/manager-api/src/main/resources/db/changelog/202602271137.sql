-- Update Tencent TTS provider configuration, add speed, volume and format parameters
UPDATE `ai_model_provider`
SET fields = '[{"key":"appid","label":"App ID","type":"string"},{"key":"secret_id","label":"Secret ID","type":"string"},{"key":"secret_key","label":"Secret Key","type":"string"},{"key":"format","label":"Audio Format","type":"string"},{"key":"speed","label":"Speech Speed","type":"number"},{"key":"volume","label":"Volume","type":"number"},{"key":"output_dir","label":"Output Directory","type":"string"},{"key":"voice","label":"Voice ID","type":"string"},{"key":"region","label":"Region","type":"string"}]'
WHERE id = 'SYSTEM_TTS_TencentTTS';

-- Update Tencent TTS model configuration, add speed and volume parameters, supplement parameter explanation
UPDATE `ai_model_config` SET 
    `config_json` = JSON_SET(`config_json`, '$.speed', 0, '$.volume', 0),
    `remark` = 'Tencent TTS configuration description:
1. Need to enable intelligent voice interaction service on Tencent Cloud platform
2. Supports multiple voices, current configuration uses 101001
3. Requires network connection
4. Output files are saved in tmp/ directory
Application steps:
1. Visit https://console.cloud.tencent.com/cam/capi to obtain keys
2. Visit https://console.cloud.tencent.com/tts/resourcebundle to claim free resources
3. Create a new application
4. Obtain appid, secret_id and secret_key
5. Fill into the configuration file
Audio parameters:
- format: Audio format, supports pcm, wav, mp3
- speed: Speech speed, range -2~6, default 0
- volume: Volume, range -10~10, default 0'
WHERE `id` = 'TTS_TencentTTS';

-- Update CozeCnTTS provider configuration, add speed and loudness_rate parameters
UPDATE `ai_model_provider`
SET fields = '[{"key":"voice","label":"Voice","type":"string"},{"key":"access_token","label":"Access Token","type":"string"},{"key":"speed","label":"Speech Speed","type":"number"},{"key":"loudness_rate","label":"Volume Gain","type":"number"},{"key":"output_dir","label":"Output Directory","type":"string"},{"key":"response_format","label":"Response Format","type":"string"}]'
WHERE id = 'SYSTEM_TTS_cozecn';

-- Update CozeCnTTS model configuration, add speed and loudness_rate parameters, supplement parameter explanation
UPDATE `ai_model_config` SET 
    `config_json` = JSON_SET(`config_json`, '$.speed', 1, '$.loudness_rate', 0),
    `remark` = 'Coze Chinese speech synthesis configuration description:
1. Visit https://www.coze.cn/ to register and log in
2. Create application and obtain access_token
3. Select appropriate voice ID
Audio parameters:
- response_format: Audio format, supports pcm, wav, mp3
- speed: Speech speed, range 0.5~2, default 1
- loudness_rate: Volume gain, range -50~100, default 0'
WHERE `id` = 'TTS_CozeCnTTS';
 