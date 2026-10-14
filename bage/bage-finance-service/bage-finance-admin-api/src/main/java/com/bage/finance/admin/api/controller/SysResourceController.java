package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.CreateSysResourceForm;
import com.bage.finance.biz.dto.form.DelSysResourceForm;
import com.bage.finance.biz.dto.form.ListSysResourceForm;
import com.bage.finance.biz.dto.form.UpdateSysResourceForm;
import com.bage.finance.biz.dto.vo.GetSysResourceVo;
import com.bage.finance.biz.dto.vo.ListSysResourceVo;
import com.bage.finance.biz.service.SysResourceService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 系统资源
 */
@RestController
@RequestMapping(value = "/sysResource")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysResourceController {
    final SysResourceService sysResourceService;

    /**
     * 创建资源
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/create")
    public ApiResponse<Boolean> create(@Valid @RequestBody CreateSysResourceForm form) {
        return ApiResponse.success(sysResourceService.create(form));
    }

    /**
     * 修改资源
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/update")
    public ApiResponse<Boolean> update(@Valid @RequestBody UpdateSysResourceForm form) {
        return ApiResponse.success(sysResourceService.update(form));
    }

    /**
     * 查看资源列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/list")
    public ApiResponse<List<ListSysResourceVo>> list(@Valid @RequestBody ListSysResourceForm form) {
        return ApiResponse.success(sysResourceService.list(form));
    }

    /**
     * 删除资源
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/del")
    public ApiResponse<Boolean> del(@Valid @RequestBody DelSysResourceForm form) {
        return ApiResponse.success(sysResourceService.del(form));
    }

    /**
     * 查询资源明细
     *
     * @param id
     * @return
     */
    @GetMapping(value = "get")
    public ApiResponse<GetSysResourceVo> get(@Valid @NotNull @RequestParam Integer id) {
        return ApiResponse.success(sysResourceService.get(id));
    }
}
