package com.bage.common.service;

import java.util.Map;

public interface SmsCommonService {
    /**
     * 发送短信
     *
     * @param signName      签名
     * @param templateCode  模板编号
     * @param phoneNumbers  手机号
     * @param templateParam 模板参数
     * @throws Exception
     */
    void sendSms(String signName, String templateCode, String phoneNumbers, Map<String, String> templateParam);

    /**
     * 校验短信验证码（默认实现：无本地验证码可校验时直接放行，由业务层决定如何处理）
     * <p>
     * 阿里云号码认证服务（Dypnsapi）由服务端生成验证码（##code## 机制），
     * 后端 Redis 中不存在与手机收到的码一致的记录，因此不能做本地比对，
     * 需由具体实现委托给阿里云 CheckSmsVerifyCode 接口校验。
     *
     * @param phone   手机号
     * @param smsCode 用户输入的短信验证码
     * @return true 校验通过
     */
    default boolean checkSms(String phone, String smsCode) {
        // 默认实现：不做校验，由上层业务（如 SmsServiceImpl）自行决定。
        // 短信宝等"后端生成码"的渠道仍走 Redis 比对，不经过此方法。
        return true;
    }
}
