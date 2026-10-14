package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.MemberBindPhone;
import com.bage.finance.biz.dto.form.UpdatePasswordForm;
import com.bage.finance.biz.dto.form.UpdatePhoneForm;

public interface MemberBindPhoneService {
    /**
     * 根据手机号获取用户信息
     *
     * @param phone
     * @return
     */
    MemberBindPhone getMemberByPhone(String phone);

    /**
     * 手机号注册
     *
     * @param phone
     * @param memberId
     * @param password
     * @return
     */
    boolean reg(String phone, long memberId, String password);

    /**
     * 修改密码
     *
     * @param form
     * @return
     */
    boolean updatePassword(UpdatePasswordForm form);

    /**
     * 获取手机账号信息
     * @param memberId
     * @return
     */
    MemberBindPhone getById(long memberId);

    /**
     * 修改手机号
     * @param form
     * @return
     */
    boolean updatePhone(UpdatePhoneForm form);
}
