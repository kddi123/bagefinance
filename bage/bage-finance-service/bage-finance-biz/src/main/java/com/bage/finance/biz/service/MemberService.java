package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.Member;
import com.bage.finance.biz.dto.form.UpdateEmailAndNameForm;
import com.bage.finance.biz.dto.vo.ListMemberVo;

import java.util.List;
import java.util.Set;

public interface MemberService {
    /**
     * 注册
     * @param tenantId 租户id
     * @return 会员id
     */
    long reg(long tenantId);

    /**
     * 获取会员信息
     *
     * @param id
     * @return
     */
    Member get(long id);

    /**
     * 修改邮箱和姓名
     * @param form
     * @return
     */
    boolean updateEmailAndName(UpdateEmailAndNameForm form);

    /**
     * 查询用户列表
     * @return
     */
    List<ListMemberVo> listMember();

    /**
     * 根据id查询会员列表
     *
     * @param ids
     * @return
     */
    List<Member> listByIds(Set<Long> ids);
}
