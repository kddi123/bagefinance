package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.domain.SysRoleBindResource;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.RoleBindResourceForm;
import com.bage.finance.biz.mapper.SysRoleBindResourceMapper;
import com.bage.finance.biz.service.SysRoleBindResourceService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.SysRoleBindResourceField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SysRoleBindResourceServiceImpl implements SysRoleBindResourceService {
    final SysRoleBindResourceMapper mapper;
    final TokenService<AdminDTO> tokenService;
    final TransactionTemplate transactionTemplate;

    /**
     * 角色绑定资源列表
     *
     * @param form
     * @return
     */
    @Override
    public boolean roleBindResource(RoleBindResourceForm form) {
        if (CollectionUtils.isEmpty(form.getBindResourceIds()) && CollectionUtils.isEmpty(form.getUnBindResourceIds())) {
            throw new BizException("您未做任何更改，不能提交绑定");
        }
        List<SysRoleBindResource> sysRoleBindResourceList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(form.getBindResourceIds())) {
            //检测角色是否已经绑定过资源
            checkRoleBindResourceIds(form.getRoleId(), form.getBindResourceIds());
            for (Integer resourceId : form.getBindResourceIds()) {
                SysRoleBindResource sysRoleBindResource = new SysRoleBindResource();
                sysRoleBindResource.setSysRoleId(form.getRoleId());
                sysRoleBindResource.setSysResourceId(resourceId);
                sysRoleBindResource.setMemberId(tokenService.getThreadLocalUserId());
                sysRoleBindResource.setUpdateMemberId(tokenService.getThreadLocalUserId());
                sysRoleBindResource.initDefault();
                sysRoleBindResourceList.add(sysRoleBindResource);
            }
        }
        transactionTemplate.execute((transactionStatus) -> {
            if (!CollectionUtils.isEmpty(form.getBindResourceIds())) {
                return mapper.insertBatch(sysRoleBindResourceList) > 0;
            }
            if (!CollectionUtils.isEmpty(form.getUnBindResourceIds())) {
                MyBatisWrapper<SysRoleBindResource> wrapper = new MyBatisWrapper<>();
                wrapper.update(DelFlag, true)
                        .whereBuilder()
                        .andEq(SysRoleId, form.getRoleId())
                        .andIn(SysResourceId, form.getUnBindResourceIds())
                        .andEq(DelFlag, false);
                int updateRows = mapper.updateField(wrapper);
                if (updateRows != form.getUnBindResourceIds().size()) {
                    throw new BizException("解绑失败");
                }
            }
            return true;
        });
        return true;
    }

    /**
     * 查询绑定的资源列表
     *
     * @param roleId
     * @return
     */
    @Override
    public List<Integer> listBindResourceIdByRoleId(int roleId) {
        MyBatisWrapper<SysRoleBindResource> wrapper = new MyBatisWrapper<>();
        wrapper.select(SysResourceId)
                .whereBuilder()
                .andEq(SysRoleId, roleId)
                .andEq(DelFlag, false);
        List<SysRoleBindResource> sysRoleBindResourceList = mapper.list(wrapper);
        if (CollectionUtils.isEmpty(sysRoleBindResourceList)) {
            return null;
        }
        return sysRoleBindResourceList.stream().map(SysRoleBindResource::getSysResourceId)
                .collect(Collectors.toList());
    }

    private void checkRoleBindResourceIds(int roleId, List<Integer> resourceIds) {
        MyBatisWrapper<SysRoleBindResource> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andEq(SysRoleId, roleId)
                .andIn(SysResourceId, resourceIds)
                .andEq(DelFlag, false);
        if (mapper.count(wrapper) > 0) {
            throw new BizException("角色不要重复绑定资源");
        }
    }
}
