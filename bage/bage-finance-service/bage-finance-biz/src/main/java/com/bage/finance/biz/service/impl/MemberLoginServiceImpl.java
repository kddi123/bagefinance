package com.bage.finance.biz.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.bage.common.constant.ApiResponseCode;
import com.bage.common.dto.TokenResponse;
import com.bage.common.exception.BizException;
import com.bage.common.exception.ParameterException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.common.util.MyUtil;
import com.bage.finance.biz.constant.RedisKeyConstant;
import com.bage.finance.biz.domain.Member;
import com.bage.finance.biz.domain.MemberBindPhone;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.enums.SmsCodeTypeEnum;
import com.bage.finance.biz.service.MemberBindPhoneService;
import com.bage.finance.biz.service.MemberLoginService;
import com.bage.finance.biz.service.MemberService;
import com.bage.finance.biz.service.SmsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberLoginServiceImpl implements MemberLoginService {
    final RedisTemplate<String, Object> redisTemplate;
    final MemberBindPhoneService memberBindPhoneService;
    final MemberService memberService;
    final PasswordEncoder passwordEncoder;
    final ObjectMapper jsonMapper;
    final TokenService<AdminDTO> adminTokenService;
    final SmsService smsService;

    /**
     * 获取客户端id
     *
     * @return
     */
    @Override
    public String getClientId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取图形验证码
     *
     * @param form
     * @return
     */
    @Override
    public String getBase64Code(GetBase64CodeForm form) {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(300, 192, 5, 1000);
        String code = lineCaptcha.getCode();
        // 将图形验证码写到redis缓存中
        redisTemplate.opsForValue().set(RedisKeyConstant.GRAPHIC_VERIFICATION_CODE + form.getClientId(),
                code, 15, TimeUnit.MINUTES);
        log.info("图形验证码{}", code);
        return lineCaptcha.getImageBase64();
    }

    /**
     * 获取短信验证码
     *
     * @param form
     * @return
     */
    @Override
    public void sendSmsCode(GetSmsCodeForm form) {
        //校验图形验证码
        checkBase64Code(form.getClientId(), form.getCode());
        String key = RedisKeyConstant.SMS_CODE + form.getSmsCodeType() + form.getPhone();
        SmsCodeResult smsCodeResult = (SmsCodeResult) redisTemplate.opsForValue().get(key);
        if (smsCodeResult != null) {
            Duration duration = DateUtil.getDuration(smsCodeResult.getGetTime(), DateUtil.getSystemTime());
            if (duration.getSeconds() < 60) {
                throw new BizException("验证码获取太频繁，请稍后重试");
            }
        }
        MemberBindPhone memberBindPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        if (form.getSmsCodeType().equals(SmsCodeTypeEnum.REG.getCode()) && memberBindPhone != null) {
            throw new ParameterException("phone", "该手机号已注册！");
        }
        if (form.getSmsCodeType().equals(SmsCodeTypeEnum.LOGIN.getCode()) && memberBindPhone == null) {
            throw new ParameterException("phone", "该手机号未注册！");
        }
        int smsCode = MyUtil.getRandom(6);
        smsCodeResult = new SmsCodeResult();
        smsCodeResult.setCode(String.valueOf(smsCode));
        smsCodeResult.setGetTime(DateUtil.getSystemTime());
        redisTemplate.opsForValue().set(key, smsCodeResult, 15, TimeUnit.MINUTES);
        log.info("客户端id{},手机号：{},短信验证码：{}", form.getClientId(), form.getPhone(), smsCode);
        smsService.sendSmsCode(form.getPhone(), smsCodeResult.getCode(), form.getSmsCodeType());
    }

    /**
     * 校验短信验证码
     *
     * @param phone
     * @param smsCode
     * @param smsCodeType
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode, String smsCodeType) {
        // 阿里云号码认证服务（Dypnsapi）：验证码由阿里云服务端生成（##code## 机制），
        // 后端 Redis 中的码与手机收到的码不一致，必须委托给阿里云校验接口比对。
        if (smsService.isAliyunDypns()) {
            smsService.checkSms(phone, smsCode);
            // 校验通过后清理本地占位记录（本地码仅用于限频，不用于比对）
            redisTemplate.delete(RedisKeyConstant.SMS_CODE + smsCodeType + phone);
            return true;
        }
        // 短信宝等后端生成码的渠道：走本地 Redis 比对
        SmsCodeResult cacheSmsCode = (SmsCodeResult) redisTemplate.opsForValue().get(RedisKeyConstant.SMS_CODE + smsCodeType + phone);
        redisTemplate.delete(RedisKeyConstant.SMS_CODE + smsCodeType + phone);
        if (cacheSmsCode == null || !smsCode.equals(cacheSmsCode.getCode())) {
            throw new ParameterException("smsCode", "短信证码错误，请重新获取验证码！");
        }
        return true;
    }

    /**
     * 校验图形验证码
     *
     * @param clientId
     * @return
     */
    @Override
    public boolean checkBase64Code(String clientId, String code) {
        //生成图片，获取base64编码字符串
        String cacheCode = (String) redisTemplate.opsForValue().get(RedisKeyConstant.GRAPHIC_VERIFICATION_CODE + clientId);
        redisTemplate.delete(RedisKeyConstant.GRAPHIC_VERIFICATION_CODE + clientId);
        if (!code.equalsIgnoreCase(cacheCode)) {
            throw new ParameterException("code", "图形验证码错误！");
        }
        return true;
    }


    /**
     * 手机账号密码登录
     *
     * @param form
     * @return
     */
    @Override
    public TokenResponse phonePasswordLogin(PhonePasswordLoginForm form) {
        checkBase64Code(form.getClientId(), form.getCode());
        MemberBindPhone memberBindPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        if (memberBindPhone == null || Strings.isBlank(memberBindPhone.getPassword())) {
            throw new BizException(ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getCode(),
                    ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getMessage());
        }
        if (!passwordEncoder.matches(form.getPassword(), memberBindPhone.getPassword())) {
            throw new BizException(ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getCode(),
                    ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getMessage());
        }
        Member member = memberService.get(memberBindPhone.getMemberId());
        return loginSuccess(member, form.getClientId());
    }

    /**
     * 手机短信登录
     *
     * @param form
     * @return
     */
    @Override
    public TokenResponse phoneSmsCodeLogin(PhoneSmsCodeLoginForm form) {
        checkSmsCode(form.getPhone(), form.getSmsCode(), SmsCodeTypeEnum.LOGIN.getCode());
        MemberBindPhone memberBindPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        //手机号未注册
        if (memberBindPhone == null) {
            throw new ParameterException("phone",
                    "该手机号未注册");
        }
        Member member = memberService.get(memberBindPhone.getMemberId());
        return loginSuccess(member, form.getClientId());
    }

    /**
     * 获取客户端token
     *
     * @param clientId
     * @return
     */
    @Override
    public TokenResponse getClientToken(String clientId) {
        return (TokenResponse) redisTemplate.opsForValue().get(RedisKeyConstant.CLIENT_TOKEN_KEY + clientId);
    }

    @Override
    public TokenResponse loginSuccess(Member member, String clientId) {
        try {
            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setId(member.getId());
            adminDTO.setTenantId(member.getTenantId());
            adminDTO.setSysRoleIds(jsonMapper.readValue(member.getSysRoleIds(), new TypeReference<Set<Long>>() {
            }));
            adminTokenService.setToken(adminDTO);
//        redisTemplate.opsForValue().set(RedisKeyConstant.CLIENT_TOKEN_KEY + memberId, adminDTO.getToken(), 10, TimeUnit.MINUTES);
            return adminDTO.getToken();
        } catch (Exception ex) {
            throw new BizException("登录失败", ex);
        }
    }
}
