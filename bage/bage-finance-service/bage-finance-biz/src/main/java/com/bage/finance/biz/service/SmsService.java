package com.bage.finance.biz.service;

public interface SmsService {
    /**
     * 发送短信验证码
     * @param phone
     * @param smsCode
     * @param smsCodeType
     */
    void sendSmsCode(String phone, String smsCode, String smsCodeType);

    /**
     * 发送注册短信验证码
     *
     * @param phone
     * @param smsCode
     */
    void sendRegSmsCode(String phone, String smsCode);

    /**
     * 发送登录短信验证码
     *
     * @param phone
     * @param smsCode
     */
    void sendLoginSmsCode(String phone, String smsCode);

    /**
     * 发送修改手机号短信验证码
     *
     * @param phone
     * @param smsCode
     */
    void sendUpdatePhoneSmsCode(String phone, String smsCode);
}