package com.bage.finance.biz.service;


import com.bage.common.dto.PageHelperRequest;
import com.bage.finance.biz.domain.SysRole;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.GetRoleDetailVo;
import com.bage.finance.biz.dto.vo.ListRoleVo;
import com.bage.finance.biz.dto.vo.MenuDataItemVo;
import com.bage.mybatis.help.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface SysRoleService {

    /**
     * 保存角色路由
     *
     * @param form
     * @return
     */
    boolean create(CreateSysRoleForm form);

    /**
     * 查看角色列表
     *
     * @param form
     * @return
     */
    PageInfo<ListRoleVo> list(ListRoleForm form);

    /**
     * 查看角色列表
     *
     * @param form
     * @return
     */
    List<SysRole> list(PageHelperRequest form);

    /**
     * 删除角色
     *
     * @param id
     * @return
     */
    boolean del(int id);

    /**
     * 禁用角色
     *
     * @param form
     * @return
     */
    boolean updateDisable(UpdateRoleDisableForm form) throws JsonProcessingException;

    /**
     * 修改角色
     *
     * @param form
     * @return
     */
    boolean update(UpdateRoleForm form);

    /**
     * 获取角色明细
     *
     * @param id
     * @return
     */
    GetRoleDetailVo getById(int id);

    /**
     * 角色绑定踩菜单列表
     *
     * @param form
     * @return
     */
    boolean roleBindMenu(RoleBindMenuForm form);

    /**
     * 将所有角色绑定的菜单设置到缓存中(通过定时任务触发)
     */
    void setSysRoleMenuCache();

    /**
     * 查询当前登录用户角色绑定的菜单列表
     *
     * @return
     */
    List<MenuDataItemVo> listRoleBindMenu();

    /**
     * 角色绑定资源列表
     *
     * @param form
     * @return
     */
    boolean roleBindResource(RoleBindResourceForm form);

    /**
     * 将所有角色绑定的资源设置到缓存中(通过定时任务触发)
     */
    void setSysRoleResourceCache();
}

