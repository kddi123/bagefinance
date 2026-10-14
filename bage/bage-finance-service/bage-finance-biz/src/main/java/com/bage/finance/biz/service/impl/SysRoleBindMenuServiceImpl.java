package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.domain.SysRoleBindMenu;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.RoleBindMenuForm;
import com.bage.finance.biz.mapper.SysRoleBindMenuMapper;
import com.bage.finance.biz.service.SysRoleBindMenuService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.SysRoleBindMenuField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SysRoleBindMenuServiceImpl implements SysRoleBindMenuService {
    final SysRoleBindMenuMapper mapper;
    final TokenService<AdminDTO> tokenService;
    final TransactionTemplate transactionTemplate;

    /**
     * 角色绑定资源列表
     *
     * @param form
     * @return
     */
    @Override
    public boolean roleBindMenu(RoleBindMenuForm form) {
        if (CollectionUtils.isEmpty(form.getBindMenuIds()) && CollectionUtils.isEmpty(form.getUnBindMenuIds())) {
            throw new BizException("您未做任何更改，不能提交绑定");
        }
        List<SysRoleBindMenu> sysRoleBindMenuList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(form.getBindMenuIds())) {
            //检测角色是否已经绑定过资源
            checkRoleBindMenuIds(form.getRoleId(), form.getBindMenuIds());
            for (Integer menuId : form.getBindMenuIds()) {
                SysRoleBindMenu sysRoleBindMenu = new SysRoleBindMenu();
                sysRoleBindMenu.setSysRoleId(form.getRoleId());
                sysRoleBindMenu.setSysMenuId(menuId);
                sysRoleBindMenu.setMemberId(tokenService.getThreadLocalUserId());
                sysRoleBindMenu.setUpdateMemberId(tokenService.getThreadLocalUserId());
                sysRoleBindMenu.initDefault();
                sysRoleBindMenuList.add(sysRoleBindMenu);
            }
        }
        transactionTemplate.execute((transactionStatus) -> {
            if (!CollectionUtils.isEmpty(form.getBindMenuIds())) {
                return mapper.insertBatch(sysRoleBindMenuList) > 0;
            }
            if (!CollectionUtils.isEmpty(form.getUnBindMenuIds())) {
                MyBatisWrapper<SysRoleBindMenu> wrapper = new MyBatisWrapper<>();
                wrapper.update(DelFlag, true)
                        .whereBuilder()
                        .andEq(SysRoleId, form.getRoleId())
                        .andIn(SysMenuId, form.getUnBindMenuIds())
                        .andEq(DelFlag, false);
                int updateRows = mapper.updateField(wrapper);
                if (updateRows != form.getUnBindMenuIds().size()) {
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
    public List<Integer> listBindMenuIdByRoleId(int roleId) {
        MyBatisWrapper<SysRoleBindMenu> wrapper = new MyBatisWrapper<>();
        wrapper.select(SysMenuId)
                .whereBuilder()
                .andEq(SysRoleId, roleId)
                .andEq(DelFlag, false);
        List<SysRoleBindMenu> sysRoleBindMenuList = mapper.list(wrapper);
        return sysRoleBindMenuList.stream().map(SysRoleBindMenu::getSysMenuId)
                .collect(Collectors.toList());
    }

    private void checkRoleBindMenuIds(int roleId, List<Integer> menuIds) {
        MyBatisWrapper<SysRoleBindMenu> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andEq(SysRoleId, roleId)
                .andIn(SysMenuId, menuIds)
                .andEq(DelFlag, false);
        if (mapper.count(wrapper) > 0) {
            throw new BizException("角色不要重复绑定菜单");
        }
    }
}
