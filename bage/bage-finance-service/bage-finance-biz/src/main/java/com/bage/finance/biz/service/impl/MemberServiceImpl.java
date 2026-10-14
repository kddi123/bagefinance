package com.bage.finance.biz.service.impl;

import com.bage.common.constant.CommonConstant;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.Member;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.UpdateEmailAndNameForm;
import com.bage.finance.biz.dto.vo.ListMemberVo;
import com.bage.finance.biz.mapper.MemberMapper;
import com.bage.finance.biz.service.MemberService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.bage.finance.biz.domain.MemberField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    final MemberMapper memberMapper;
    final TokenService<AdminDTO> tokenService;
    final ObjectConvertor objectConvertor;

    /**
     * 注册
     *
     * @param tenantId 租户id
     * @return 会员id
     */
    @Override
    public long reg(long tenantId) {
        Member member = new Member();
        member.setTenantId(tenantId);
        member.setSysRoleIds("[" + CommonConstant.ROLE_MEMBER + "]");
        member.initDefault();
        memberMapper.insert(member);
        return member.getId();
    }

    /**
     * 获取会员信息
     *
     * @param id
     * @return
     */
    @Override
    public Member get(long id) {
        MyBatisWrapper<Member> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.select(NickName, Id, Disable, Name, AvatarUrl, SysRoleIds,
                TenantId, Email)
                .whereBuilder().andEq(setId(id));
        return memberMapper.topOne(myBatisWrapper);
    }

    /**
     * 修改邮箱和姓名
     *
     * @param form
     * @return
     */
    @Override
    public boolean updateEmailAndName(UpdateEmailAndNameForm form) {
        MyBatisWrapper<Member> wrapper = new MyBatisWrapper<>();
        wrapper.update(setEmail(form.getEmail()))
                .update(setName(form.getName()))
                .whereBuilder()
                .andEq(setId(tokenService.getThreadLocalUserId()))
                .andEq(setDisable(false));
        return memberMapper.updateField(wrapper) > 0;
    }

    /**
     * 查询用户列表
     *
     * @return
     */
    @Override
    public List<ListMemberVo> listMember() {
        MyBatisWrapper<Member> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, NickName, Name)
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(Disable, false);
        List<Member> members = memberMapper.list(wrapper);
        return objectConvertor.toListMemberVo(members);
    }
    /**
     * 根据id查询会员列表
     *
     * @param ids
     * @return
     */
    @Override
    public List<Member> listByIds(Set<Long> ids) {
        MyBatisWrapper<Member> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, NickName, Name)
                .whereBuilder()
                .andIn(Id, ids);
        return memberMapper.list(wrapper);
    }
}
