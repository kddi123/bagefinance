package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.GetRoleDetailVo;
import com.bage.finance.biz.dto.vo.ListRoleVo;
import com.bage.finance.biz.dto.vo.MenuDataItemVo;
import com.bage.finance.biz.service.SysRoleBindMenuService;
import com.bage.finance.biz.service.SysRoleService;
import com.bage.mybatis.help.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 角色管理
 */
@RestController
@RequestMapping(value = "/sysRole")
@RequiredArgsConstructor
public class SysRoleController {
    final SysRoleService sysRoleService;

    /**
     * 新增角色
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/create")
    public ApiResponse<Boolean> create(@Valid @RequestBody CreateSysRoleForm form) {
        return ApiResponse.success(sysRoleService.create(form));
    }

    /**
     * 查看角色列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/list")
    public ApiResponse<PageInfo<ListRoleVo>> list(@Valid @RequestBody ListRoleForm form) {
        return ApiResponse.success(sysRoleService.list(form));
    }

    /**
     * 删除角色
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/del")
    public ApiResponse<Boolean> del(@Valid @RequestBody DelSysRoleForm form) {
        return ApiResponse.success(sysRoleService.del(form.getId()));
    }

    /**
     * 禁用或启用角色
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/updateDisable")
    public ApiResponse<Boolean> updateDisable(@Valid @RequestBody UpdateRoleDisableForm form) throws JsonProcessingException {
        return ApiResponse.success(sysRoleService.updateDisable(form));
    }

    /**
     * 修改角色
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/update")
    public ApiResponse<Boolean> update(@Valid @RequestBody UpdateRoleForm form) {
        return ApiResponse.success(sysRoleService.update(form));
    }

    /**
     * 查询角色明细
     *
     * @param id
     * @return
     */
    @GetMapping(value = "getById")
    public ApiResponse<GetRoleDetailVo> getById(@Valid @NotNull @RequestParam Integer id) {
        return ApiResponse.success(sysRoleService.getById(id));
    }

    /**
     * 查询当前登录用户角色绑定的菜单列表
     *
     * @return
     */
    @GetMapping(value = "/listRoleBindMenu")
    public ApiResponse<List<MenuDataItemVo>> listRoleBindMenu() {
        return ApiResponse.success(sysRoleService.listRoleBindMenu());
    }

    /**
     * 角色绑定菜单
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/roleBindMenu")
    public ApiResponse<Boolean> roleBindResource(@Valid @RequestBody RoleBindMenuForm form) {
        return ApiResponse.success(sysRoleService.roleBindMenu(form));
    }

    /**
     * 角色绑定资源
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/roleBindResource")
    public ApiResponse<Boolean> roleBindResource(@Valid @RequestBody RoleBindResourceForm form) {
        return ApiResponse.success(sysRoleService.roleBindResource(form));
    }
}
