package xiaozhi.modules.security.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Captcha
 * Copyright (c) Everyone Open Source All rights reserved.
 * Website: https://www.renren.io
 */
public interface CaptchaService {

    /**
     * Image Captcha
     */
    void create(HttpServletResponse response, String uuid) throws IOException;

    /**
     * Captcha validation
     * 
     * @param uuid   uuid
     * @param code   Captcha
     * @param delete Whether to delete the captcha
     * @return true：success false：failure
     */
    boolean validate(String uuid, String code, Boolean delete);

    /**
     * Send SMS verification code
     * 
     * @param phone phone
     */
    void sendSMSValidateCode(String phone);

    /**
     * Validate SMS verification code
     * 
     * @param phone  phone
     * @param code   Captcha
     * @param delete Whether to delete the captcha
     * @return true：success false：failure
     */
    boolean validateSMSValidateCode(String phone, String code, Boolean delete);
}
 