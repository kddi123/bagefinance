package com.bage.finance.biz.service;

import com.bage.finance.biz.dto.form.GetUserSmsCodeForm;
import com.bage.finance.biz.dto.form.UpdatePhoneForm;
import com.bage.finance.biz.dto.vo.CurrentInfoVo;

public interface MemberComService {
    /**
     * 获取用户信息
     *
     * @return
     */
    CurrentInfoVo getCurrentInfo();

    /**
     * 修改手机号
     *
     * @param form
     * @return
     */
    boolean updatePhone(UpdatePhoneForm form);

    /**
     * 校验短信验证码
     *
     * @param phone
     * @param smsCode
     * @param smsCodeType
     * @return
     */
    boolean checkSmsCode(String phone, String smsCode, String smsCodeType);

    /**
     * 生成图形验证码（登录之后的获取）
     *
     * @return
     */
    String getBase64Code();

    /**
     * 获取短信验证码
     *
     * @param form
     * @return
     */
    void sendSmsCode(GetUserSmsCodeForm form);

    /**
     * 检验图形验证码
     *
     * @param code
     * @return
     */
    boolean checkBase64Code(String code);
}