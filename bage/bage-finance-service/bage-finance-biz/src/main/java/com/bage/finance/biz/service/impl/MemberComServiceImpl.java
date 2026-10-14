package com.bage.finance.biz.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.bage.common.exception.BizException;
import com.bage.common.exception.ParameterException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.common.util.MyUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.constant.RedisKeyConstant;
import com.bage.finance.biz.domain.Member;
import com.bage.finance.biz.domain.MemberBindPhone;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.GetUserSmsCodeForm;
import com.bage.finance.biz.dto.form.SmsCodeResult;
import com.bage.finance.biz.dto.form.UpdatePhoneForm;
import com.bage.finance.biz.dto.vo.CurrentInfoVo;
import com.bage.finance.biz.enums.SmsCodeTypeEnum;
import com.bage.finance.biz.service.MemberBindPhoneService;
import com.bage.finance.biz.service.MemberComService;
import com.bage.finance.biz.service.MemberService;
import com.bage.finance.biz.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class MemberComServiceImpl implements MemberComService {
    final MemberService memberService;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final MemberBindPhoneService memberBindPhoneService;
    final RedisTemplate<String, Object> redisTemplate;
    final SmsService smsService;
    final RedissonClient redissonClient;

    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public CurrentInfoVo getCurrentInfo() {
        Member member = memberService.get(tokenService.getThreadLocalUserId());
        if (Objects.isNull(member)) {
            throw new BizException("用户不存在");
        }
        if (member.getDisable()) {
            throw new BizException("用户被禁用");
        }
        return objectConvertor.toCurrentInfoVo(member);
    }

    /**
     * 修改手机号
     *
     * @param form
     * @return
     */
    @Override
    public boolean updatePhone(UpdatePhoneForm form) {
        RLock rLock = redissonClient.getLock(RedisKeyConstant.PHONE_CHANGE + form.getPhone());

        try {
            rLock.lock();
            checkSmsCode(form.getPhone(), form.getSmsCode(), SmsCodeTypeEnum.UPDATE_PHONE.getCode());
            MemberBindPhone memberBindPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
            if (memberBindPhone != null) {
                throw new ParameterException("phone", "该手机号已注册！");
            }
            if (!memberBindPhoneService.updatePhone(form)) {
                throw new BizException("修改手机号失败");
            }
        } catch (Exception ex) {
            throw new RuntimeException("修改手机号失败", ex);
        } finally {
            rLock.unlock();
        }
        return true;
    }

    /**
     * 校验短信验证码
     *
     * @param smsCode
     * @param smsCodeType
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode, String smsCodeType) {
        SmsCodeResult cacheSmsCode = (SmsCodeResult) redisTemplate.opsForValue().get(RedisKeyConstant.SMS_CODE + smsCodeType + tokenService.getThreadLocalUserId());
        redisTemplate.delete(RedisKeyConstant.SMS_CODE + smsCodeType + tokenService.getThreadLocalUserId());
        if (cacheSmsCode == null || !smsCode.equals(cacheSmsCode.getCode())) {
            throw new ParameterException("smsCode", "短信证码错误，请重新获取！");
        }
        if (!Objects.equals(phone, cacheSmsCode.getPhone())) {
            throw new ParameterException("phone", "非法操作，请重新获取验证码");
        }
        return true;
    }

    /**
     * 生成图形验证码（登录之后的获取）
     *
     * @return
     */
    @Override
    public String getBase64Code() {
        //生成验证码: hutool工具包里面有现成的工具类可以直接使用，可以生成三种图片验证码：LineCaptcha、CircleCaptcha、ShearCaptcha
        LineCaptcha shearCaptcha = CaptchaUtil.createLineCaptcha(300, 192, 5, 1000);
        //获取到4位数的验证码
        String code = shearCaptcha.getCode();
        log.info("客户端member_id：{}，图形验证码：{}", tokenService.getThreadLocalUserId(), code);
        //生成图片，获取base64编码字符串
        redisTemplate.opsForValue().set(RedisKeyConstant.USER_GRAPHIC_VERIFICATION_CODE + tokenService.getThreadLocalUserId(), code, 15, TimeUnit.MINUTES);
        return shearCaptcha.getImageBase64();
    }

    /**
     * 获取短信验证码
     *
     * @param form
     * @return
     */
    @Override
    public void sendSmsCode(GetUserSmsCodeForm form) {
        checkBase64Code(form.getCode());
        String key = RedisKeyConstant.SMS_CODE + form.getSmsCodeType() + tokenService.getThreadLocalUserId();
        SmsCodeResult smsCodeResult = (SmsCodeResult) redisTemplate.opsForValue().get(key);
        if (smsCodeResult != null) {
            Duration duration = DateUtil.getDuration(smsCodeResult.getGetTime(), DateUtil.getSystemTime());
            if (duration.getSeconds() < 60) {
                throw new BizException("验证码获取太频繁，请稍后重试");
            }
        }
        int smsCode = MyUtil.getRandom(6);
        smsCodeResult = new SmsCodeResult();
        smsCodeResult.setCode(String.valueOf(smsCode));
        smsCodeResult.setGetTime(DateUtil.getSystemTime());
        smsCodeResult.setPhone(form.getPhone());
        redisTemplate.opsForValue().set(key, smsCodeResult, 15, TimeUnit.MINUTES);
        log.info("用户id{},手机号：{},短信验证码：{}", tokenService.getThreadLocalUserId(), form.getPhone(), smsCode);
        smsService.sendSmsCode(form.getPhone(), smsCodeResult.getCode(), form.getSmsCodeType());
    }

    /**
     * 检验图形验证码
     *
     * @param code
     * @return
     */
    @Override
    public boolean checkBase64Code(String code) {
        //生成图片，获取base64编码字符串
        String cacheCode = (String) redisTemplate.opsForValue().get(RedisKeyConstant.USER_GRAPHIC_VERIFICATION_CODE + tokenService.getThreadLocalUserId());
        redisTemplate.delete(RedisKeyConstant.USER_GRAPHIC_VERIFICATION_CODE + tokenService.getThreadLocalUserId());
        if (!code.equalsIgnoreCase(cacheCode)) {
            throw new ParameterException("code", "图形验证码错误！");
        }
        return true;
    }
}
