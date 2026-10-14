package com.bage.common.service.impl;

import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.bage.common.exception.BizException;
import com.bage.common.service.SmsCommonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 阿里云短信
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(AsyncClient.class)
public class ALiSmsServiceImpl implements SmsCommonService {
    final AsyncClient asyncClient;
    final ObjectMapper jsonMapper;

    /**
     * 发送短信
     *
     * @param signName      签名
     * @param templateCode  模板编号
     * @param phoneNumbers  手机号
     * @param templateParam 模板参数
     * @throws Exception
     */
    @Override
    public void sendSms(String signName, String templateCode, String phoneNumbers, Map<String, String> templateParam) {
        try {
            // Parameter settings for API request
            SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
            .signName(signName)
            .templateCode(templateCode)
            .phoneNumbers(phoneNumbers)
            .templateParam(jsonMapper.writeValueAsString(templateParam))
            // Request-level configuration rewrite, can set Http request parameters, etc.
            // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
            .build();
            // Asynchronously get the return value of the API request
            CompletableFuture<SendSmsResponse> response = asyncClient.sendSms(sendSmsRequest);
            // Synchronously get the return value of the API request
            SendSmsResponse resp = response.get();
            log.info("短信发送结果：{}", jsonMapper.writeValueAsString(resp));
        } catch (Exception ex) {
            throw new BizException("发送短信验证码失败", ex);
        }
    }
}