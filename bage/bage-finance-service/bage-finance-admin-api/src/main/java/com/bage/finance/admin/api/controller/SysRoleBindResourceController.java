package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.service.SysRoleBindResourceService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统角色绑定资源
 */
@RestController
@RequestMapping(value = "/sysRoleBindResource")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysRoleBindResourceController {
    final SysRoleBindResourceService sysRoleBindResourceService;

    /**
     * 查询绑定的资源列表
     *
     * @param roleId
     * @return
     */
    @GetMapping(value = "/listBindResourceIdByRoleId")
    public ApiResponse<List<Integer>> listBindResourceIdByRoleId(@RequestParam int roleId) {
        return ApiResponse.success(sysRoleBindResourceService.listBindResourceIdByRoleId(roleId));
    }
}
