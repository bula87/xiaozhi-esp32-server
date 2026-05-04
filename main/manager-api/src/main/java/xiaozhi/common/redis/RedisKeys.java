package xiaozhi.common.redis;

/**
 * Redis Key Constants Class
 * Copyright (c) RuoYi Open Source All rights reserved.
 * Website: https://www.ruoyi.io
 */
public class RedisKeys {
    /**
     * System Parameter Key
     */
    public static String getSysParamsKey() {
        return "sys:params";
    }

    /**
     * Captcha Key
     */
    public static String getCaptchaKey(String uuid) {
        return "sys:captcha:" + uuid;
    }

    /**
     * Unregistered Device Captcha Key
     */
    public static String getDeviceCaptchaKey(String captcha) {
        return "sys:device:captcha:" + captcha;
    }

    /**
     * User ID Key
     */
    public static String getUserIdKey(Long userid) {
        return "sys:username:id:" + userid;
    }

    /**
     * Model Name Key
     */
    public static String getModelNameById(String id) {
        return "model:name:" + id;
    }

    /**
     * Model Configuration Key
     */
    public static String getModelConfigById(String id) {
        return "model:data:" + id;
    }

    /**
     * Get Timbre Name Cache Key
     */
    public static String getTimbreNameById(String id) {
        return "timbre:name:" + id;
    }

    /**
     * Get Agent Device Count Cache Key
     */
    public static String getAgentDeviceCountById(String id) {
        return "agent:device:count:" + id;
    }

    /**
     * Get Agent Last Connection Time Cache Key
     */
    public static String getAgentDeviceLastConnectedAtById(String id) {
        return "agent:device:lastConnected:" + id;
    }

    /**
     * Get Server Configuration Cache Key
     */
    public static String getServerConfigKey() {
        return "server:config";
    }

    /**
     * Get Timbre Details Cache Key
     */
    public static String getTimbreDetailsKey(String id) {
        return "timbre:details:" + id;
    }

    /**
     * Get Version Key
     */
    public static String getVersionKey() {
        return "sys:version";
    }

    /**
     * OTA Firmware ID Key
     */
    public static String getOtaIdKey(String uuid) {
        return "ota:id:" + uuid;
    }

    /**
     * OTA Firmware Download Count Key
     */
    public static String getOtaDownloadCountKey(String uuid) {
        return "ota:download:count:" + uuid;
    }

    /**
     * Get Dictionary Data Cache Key
     */
    public static String getDictDataByTypeKey(String dictType) {
        return "sys:dict:data:" + dictType;
    }

    /**
     * Get Agent Audio ID Cache Key
     */
    public static String getAgentAudioIdKey(String uuid) {
        return "agent:audio:id:" + uuid;
    }

    /**
     * Get SMS Validation Code Cache Key
     */
    public static String getSMSValidateCodeKey(String phone) {
        return "sms:Validate:Code:" + phone;
    }

    /**
     * Get SMS Last Send Time Cache Key
     */
    public static String getSMSLastSendTimeKey(String phone) {
        return "sms:Validate:Code:" + phone + ":last_send_time";
    }

    /**
     * Get SMS Today Count Cache Key
     */
    public static String getSMSTodayCountKey(String phone) {
        return "sms:Validate:Code:" + phone + ":today_count";
    }

    /**
     * Chat History UUID Mapping Key
     */
    public static String getChatHistoryKey(String uuid) {
        return "agent:chat:history:" + uuid;
    }

    /**
     * Get Voice Clone Audio ID Cache Key
     */
    public static String getVoiceCloneAudioIdKey(String uuid) {
        return "voiceClone:audio:id:" + uuid;
    }

    /**
     * Get Knowledge Base Cache Key
     */
    public static String getKnowledgeBaseCacheKey(String datasetId) {
        return "knowledge:base:" + datasetId;
    }

    /**
     * Get Temporary Registration Device Marker Key
     */
    public static String getTmpRegisterMacKey(String deviceId) {
        return "tmp_register_mac:" + deviceId;
    }

    /**
     * OTA Activate Device
     */
    public static String getOtaActivationCode(String activationCode) {
        return "ota:activation:code:" + activationCode;
    }

    /**
     * OTA Get Device MAC Information
     */
    public static String getOtaDeviceActivationInfo(String deviceId) {
        return "ota:activation:data:" + deviceId;
    }

    /**
     * OTA Upload Count
     */
    public static String getOtaUploadCountKey(Long username) {
        return "ota:upload:count:" + username;
    }

}
 