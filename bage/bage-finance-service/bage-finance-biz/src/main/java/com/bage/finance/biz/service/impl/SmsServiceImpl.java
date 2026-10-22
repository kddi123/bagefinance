package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.SmsCommonService;
import com.bage.finance.biz.dto.SmsTemplateDTO;
import com.bage.finance.biz.enums.SmsCodeTypeEnum;
import com.bage.finance.biz.service.SmsService;
import com.bage.finance.biz.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    final SmsCommonService smsCommonService;
    final SysConfigService sysConfigService;

    /**
     * 发送短信验证码
     *
     * @param phone
     * @param smsCode
     * @param smsCodeType
     */
    @Override
    public void sendSmsCode(String phone, String smsCode, String smsCodeType) {
        if (SmsCodeTypeEnum.REG.getCode().equals(smsCodeType)) {
            sendRegSmsCode(phone, smsCode);
        }
        if (SmsCodeTypeEnum.LOGIN.getCode().equals(smsCodeType)) {
            sendLoginSmsCode(phone, smsCode);
        }
        if (SmsCodeTypeEnum.UPDATE_PHONE.getCode().equals(smsCodeType)) {
            sendUpdatePhoneSmsCode(phone, smsCode);
        }
    }

    /**
     * 发送注册短信验证码
     *
     * @param phone
     * @param smsCode
     */
    @Override
    public void sendRegSmsCode(String phone, String smsCode) {
        SmsTemplateDTO smsTemplate = sysConfigService.getSmsTemplateByCache(SmsCodeTypeEnum.REG.getCode());
        if (smsTemplate == null) {
            throw new BizException("短信模板未配置或未加载到缓存中");
        }
        Map<String, String> templateParam = new HashMap<>();
        // min：号码认证服务验证码有效分钟数（模板需要时传入，代码默认 15）
        templateParam.put("min", smsTemplate.getMin() == null ? "15" : smsTemplate.getMin());
        templateParam.put("code", smsCode);
        smsCommonService.sendSms(smsTemplate.getSignName(), smsTemplate.getTemplateCode(),
                phone, templateParam);
    }

    /**
     * 发送登录短信验证码
     *
     * @param phone
     * @param smsCode
     */
    @Override
    public void sendLoginSmsCode(String phone, String smsCode) {
        SmsTemplateDTO smsTemplate = sysConfigService.getSmsTemplateByCache(SmsCodeTypeEnum.LOGIN.getCode());
        if (smsTemplate == null) {
            throw new BizException("短信模板未配置或未加载到缓存中");
        }
        Map<String, String> templateParam = new HashMap<>();
        // min：号码认证服务验证码有效分钟数（模板需要时传入，代码默认 15）
        templateParam.put("min", smsTemplate.getMin() == null ? "15" : smsTemplate.getMin());
        templateParam.put("code", smsCode);
        smsCommonService.sendSms(smsTemplate.getSignName(), smsTemplate.getTemplateCode(),
                phone, templateParam);
    }

    /**
     * 发送修改手机号短信验证码
     *
     * @param phone
     * @param smsCode
     */
    @Override
    public void sendUpdatePhoneSmsCode(String phone, String smsCode) {
        SmsTemplateDTO smsTemplate = sysConfigService.getSmsTemplateByCache(SmsCodeTypeEnum.UPDATE_PHONE.getCode());
        if (smsTemplate == null) {
            throw new BizException("短信模板未配置或未加载到缓存中");
        }
        Map<String, String> templateParam = new HashMap<>();
        // min：号码认证服务验证码有效分钟数（模板需要时传入，代码默认 15）
        templateParam.put("min", smsTemplate.getMin() == null ? "15" : smsTemplate.getMin());
        templateParam.put("code", smsCode);
        smsCommonService.sendSms(smsTemplate.getSignName(), smsTemplate.getTemplateCode(),
                phone, templateParam);
    }

    /**
     * 校验短信验证码（委托给底层短信渠道）
     *
     * @param phone   手机号
     * @param smsCode 用户输入的短信验证码
     * @return true 校验通过
     */
    @Override
    public boolean checkSms(String phone, String smsCode) {
        return smsCommonService.checkSms(phone, smsCode);
    }

    /**
     * 当前短信渠道是否为阿里云号码认证服务（Dypnsapi）
     *
     * @return true 表示当前为阿里云号码认证服务渠道
     */
    @Override
    public boolean isAliyunDypns() {
        return smsCommonService instanceof com.bage.common.service.impl.ALiSmsServiceImpl;
    }
}
