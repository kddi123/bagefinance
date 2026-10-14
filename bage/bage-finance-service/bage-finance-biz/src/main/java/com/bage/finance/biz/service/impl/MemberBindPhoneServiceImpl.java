package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.exception.ParameterException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.domain.MemberBindPhone;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.UpdatePasswordForm;
import com.bage.finance.biz.dto.form.UpdatePhoneForm;
import com.bage.finance.biz.mapper.MemberBindPhoneMapper;
import com.bage.finance.biz.service.MemberBindPhoneService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.bage.finance.biz.domain.MemberBindPhoneField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberBindPhoneServiceImpl implements MemberBindPhoneService {
    final MemberBindPhoneMapper memberBindPhoneMapper;
    final PasswordEncoder passwordEncoder;
    final TokenService<AdminDTO> tokenService;

    /**
     * 根据手机号获取用户信息
     *
     * @param phone
     * @return
     */
    @Override
    public MemberBindPhone getMemberByPhone(String phone) {
        MyBatisWrapper<MemberBindPhone> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.select(MemberId, Phone, Password)
                .whereBuilder().andEq(setPhone(phone))
                .andEq(setDisable(false));
        // select member_id,phone,password from member_bind_phone where phone = ?
        return memberBindPhoneMapper.topOne(myBatisWrapper);
    }

    /**
     * 手机号注册
     *
     * @param phone
     * @param memberId
     * @param password
     * @return
     */
    @Override
    public boolean reg(String phone, long memberId, String password) {
        MemberBindPhone memberBindPhone = new MemberBindPhone();
        memberBindPhone.setMemberId(memberId);
        memberBindPhone.setPhone(phone);
        memberBindPhone.setPassword(passwordEncoder.encode(password));
        memberBindPhone.initDefault();
        return memberBindPhoneMapper.insert(memberBindPhone) > 0;
    }

    /**
     * 修改密码
     *
     * @param form
     * @return
     */
    @Override
    public boolean updatePassword(UpdatePasswordForm form) {
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            throw new ParameterException("两次输入的密码不一致");
        }
        MemberBindPhone memberBindPhone = getById(tokenService.getThreadLocalUserId());
        if (memberBindPhone == null) {
            throw new BizException("账号信息不存在");
        }
        if (memberBindPhone.getDisable()) {
            throw new BizException("账号已被禁用，无法修改密码");
        }
        if (!passwordEncoder.matches(form.getOldPassword(), memberBindPhone.getPassword())) {
            throw new BizException("旧密码不正确");
        }
        String newPassword = passwordEncoder.encode(form.getPassword());
        MyBatisWrapper<MemberBindPhone> wrapper = new MyBatisWrapper<>();
        wrapper.update(setPassword(newPassword))
                .whereBuilder()
                .andEq(setMemberId(tokenService.getThreadLocalUserId()))
                .andEq(setDisable(false));
        if (memberBindPhoneMapper.updateField(wrapper) == 0) {
            throw new BizException("密码修改失败");
        }
        tokenService.clearToken();
        return true;
    }

    /**
     * 获取手机账号信息
     *
     * @param memberId
     * @return
     */
    @Override
    public MemberBindPhone getById(long memberId) {
        MyBatisWrapper<MemberBindPhone> wrapper = new MyBatisWrapper<>();
        wrapper.select(Password, Disable)
                .whereBuilder()
                .andEq(setMemberId(tokenService.getThreadLocalUserId()));
        return memberBindPhoneMapper.get(wrapper);
    }

    /**
     * 修改手机号
     *
     * @param form
     * @return
     */
    @Override
    public boolean updatePhone(UpdatePhoneForm form) {
        MyBatisWrapper<MemberBindPhone> wrapper = new MyBatisWrapper<>();
        wrapper.update(setPhone(form.getPhone()))
                .whereBuilder()
                .andEq(setMemberId(tokenService.getThreadLocalUserId()))
                .andEq(setDisable(false));
        return memberBindPhoneMapper.updateField(wrapper) > 0;
    }
}
