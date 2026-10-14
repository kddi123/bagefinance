package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.CreateMenuForm;
import com.bage.finance.biz.dto.form.DelMenuForm;
import com.bage.finance.biz.dto.form.UpdateMenuForm;
import com.bage.finance.biz.dto.vo.GetMenuByIdVo;
import com.bage.finance.biz.dto.vo.ListTreeMenuVo;
import com.bage.finance.biz.dto.vo.ListTreeSelectMenuVo;
import com.bage.finance.biz.service.SysMenuService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 系统菜单管理
 */
@RestController
@RequestMapping(value = "/sysMenu")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysMenuController {
    final SysMenuService sysMenuService;

    /**
     * 树形菜单列表
     *
     * @param title
     * @return
     */
    @GetMapping(value = "/listTreeMenu")
    public ApiResponse<List<ListTreeMenuVo>> listTreeMenu(@RequestParam(required = false) String title) {
        return ApiResponse.success(sysMenuService.listTreeMenu(title));
    }

    /**
     * 树形选择菜单列表
     *
     * @return
     */
    @GetMapping(value = "/listTreeSelectMenu")
    public ApiResponse<List<ListTreeSelectMenuVo>> listTreeSelectMenu() {
        return ApiResponse.success(sysMenuService.listTreeSelectMenu());
    }

    /**
     * 获取菜单明细
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getMenuById")
    public ApiResponse<GetMenuByIdVo> getMenuById(@Validated @RequestParam @NotNull Integer id) {
        return ApiResponse.success(sysMenuService.getMenuById(id));
    }

    /**
     * 创建菜单
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/create")
    public ApiResponse<Boolean> create(@Validated @RequestBody CreateMenuForm form) {
        return ApiResponse.success(sysMenuService.create(form));
    }

    /**
     * 修改菜单
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/update")
    public ApiResponse<Boolean> update(@Validated @RequestBody UpdateMenuForm form) {
        return ApiResponse.success(sysMenuService.update(form));
    }

    /**
     * 删除菜单
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/del")
    public ApiResponse<Boolean> del(@Validated @RequestBody DelMenuForm form) {
        return ApiResponse.success(sysMenuService.del(form));
    }
}
