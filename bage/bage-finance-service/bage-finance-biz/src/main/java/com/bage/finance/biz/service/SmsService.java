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

    /**
     * 校验短信验证码（委托给底层短信渠道）
     * <p>
     * 阿里云号码认证服务（Dypnsapi）由服务端生成验证码，后端 Redis 中
     * 不存在与手机收到的码一致的记录，无法本地比对，需要调用渠道的
     * 校验接口（阿里云 CheckSmsVerifyCode）。短信宝等后端生成码的渠道
     * 走 Redis 比对逻辑（见 MemberLoginServiceImpl.checkSmsCode）。
     *
     * @param phone   手机号
     * @param smsCode 用户输入的短信验证码
     * @return true 校验通过
     */
    boolean checkSms(String phone, String smsCode);

    /**
     * 当前短信渠道是否为阿里云号码认证服务（Dypnsapi）
     * <p>
     * Dypnsapi 的验证码由阿里云服务端生成，checkSmsCode 需要走渠道校验接口；
     * 其他渠道（短信宝等）由后端生成验证码，走本地 Redis 比对。
     *
     * @return true 表示当前为阿里云号码认证服务渠道
     */
    boolean isAliyunDypns();
}