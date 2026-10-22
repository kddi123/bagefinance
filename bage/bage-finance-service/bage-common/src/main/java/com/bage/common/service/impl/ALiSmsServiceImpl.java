package com.bage.common.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.exception.ParameterException;
import com.bage.common.service.SmsCommonService;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 阿里云短信（号码认证服务 Dypnsapi）
 * <p>
 * 签名模板在号码认证服务后台配置（固定签名+模板编号），通过
 * sendSmsVerifyCode 接口发送短信验证码。验证码由号码认证服务端生成
 * （模板参数 ##code## 被替换为随机码），后端无法得知该码，
 * 因此校验时通过 checkSmsVerifyCode 接口委托阿里云比对，而非本地 Redis。
 * 是否生效由 ali.sms.enable 开关控制（见 ALiProperties 上的
 * @ConditionalOnProperty），与短信宝实现互斥，同一时刻只允许一个
 * SmsCommonService 存在，否则 SmsServiceImpl 构造注入会因候选 bean
 * 冲突或缺失而启动失败。
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(com.aliyun.dypnsapi20170525.Client.class)
@ConditionalOnProperty(prefix = "ali.sms", name = "enable", havingValue = "true")
public class ALiSmsServiceImpl implements SmsCommonService {
    final com.aliyun.dypnsapi20170525.Client dypnsClient;
    final ObjectMapper jsonMapper;

    /**
     * 发送短信验证码
     *
     * @param signName      签名（由号码认证服务后台配置，此参数仅作日志留痕）
     * @param templateCode  模板编号（如 100001）
     * @param phoneNumbers  手机号
     * @param templateParam 模板参数（code=验证码，min=有效分钟数）
     */
    @Override
    public void sendSms(String signName, String templateCode, String phoneNumbers, Map<String, String> templateParam) {
        try {
            // ##code## 会被号码认证服务替换为随机验证码；min 为验证码有效时长（分钟）
            String templateParamJson = "{\"code\":\"##code##\",\"min\":\"" +
                    templateParam.getOrDefault("min", "15") + "\"}";
            SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setPhoneNumber(phoneNumbers)
                    .setTemplateParam(templateParamJson);
            RuntimeOptions runtime = new RuntimeOptions();
            SendSmsVerifyCodeResponse resp = dypnsClient.sendSmsVerifyCodeWithOptions(request, runtime);
            log.info("短信发送结果：{}", jsonMapper.writeValueAsString(resp));
            if (resp.getBody() == null || !Boolean.TRUE.equals(resp.getBody().getSuccess())) {
                String msg = resp.getBody() == null ? "响应为空" : resp.getBody().getMessage();
                throw new BizException("发送短信验证码失败：" + msg);
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException("发送短信验证码失败", ex);
        }
    }

    /**
     * 校验短信验证码（委托阿里云号码认证服务 CheckSmsVerifyCode 接口）
     * <p>
     * 验证码由号码认证服务端生成（##code## 机制），后端 Redis 中没有
     * 与手机收到的码一致的记录，无法做本地比对，必须调用阿里云校验接口。
     *
     * @param phone   手机号
     * @param smsCode 用户输入的短信验证码
     * @return true 校验通过（PASS）
     */
    @Override
    public boolean checkSms(String phone, String smsCode) {
        try {
            CheckSmsVerifyCodeRequest request = new CheckSmsVerifyCodeRequest()
                    .setPhoneNumber(phone)
                    .setVerifyCode(smsCode)
                    .setCountryCode("86");
            RuntimeOptions runtime = new RuntimeOptions();
            CheckSmsVerifyCodeResponse resp = dypnsClient.checkSmsVerifyCodeWithOptions(request, runtime);
            log.info("短信验证码校验结果：{}", jsonMapper.writeValueAsString(resp));
            if (resp.getBody() == null || !Boolean.TRUE.equals(resp.getBody().getSuccess())) {
                String msg = resp.getBody() == null ? "响应为空" : resp.getBody().getMessage();
                throw new ParameterException("smsCode", "短信验证码校验失败：" + msg);
            }
            com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponseBody.CheckSmsVerifyCodeResponseBodyModel model = resp.getBody().getModel();
            if (model == null || !"PASS".equals(model.getVerifyResult())) {
                throw new ParameterException("smsCode", "短信验证码错误，请重新获取验证码！");
            }
            return true;
        } catch (ParameterException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException("短信验证码校验失败", ex);
        }
    }
}
