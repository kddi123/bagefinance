package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.*;
import com.bage.finance.biz.service.AssistCalculateSummaryService;
import com.bage.mybatis.help.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 辅助核算管理
 */
@RestController
@RequestMapping(value = "/assistCalculateSummary")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AssistCalculateSummaryController {
    final AssistCalculateSummaryService assistCalculateSummaryService;

    /**
     * 创建自定义辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/createCustom")
    public ApiResponse<Boolean> createCustom(@Valid @RequestBody CreateAssistCalculateCustomForm form) {
        return ApiResponse.success(assistCalculateSummaryService.create(form));
    }

    /**
     * 创建客户辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/createCustomer")
    public ApiResponse<Boolean> createCustomer(@Valid @RequestBody CreateAssistCalculateCustomerForm form) {
        return ApiResponse.success(assistCalculateSummaryService.create(form));
    }

    /**
     * 创建部门辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/createDepartment")
    public ApiResponse<Boolean> createDepartment(@Valid @RequestBody CreateAssistCalculateDepartmentForm form) {
        return ApiResponse.success(assistCalculateSummaryService.create(form));
    }

    /**
     * 创建职员辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/createEmployee")
    public ApiResponse<Boolean> createEmployee(@Valid @RequestBody CreateAssistCalculateEmployeeForm form) {
        return ApiResponse.success(assistCalculateSummaryService.create(form));
    }

    /**
     * 创建存货辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/createInventory")
    public ApiResponse<Boolean> createInventory(@Valid @RequestBody CreateAssistCalculateInventoryForm form) {
        return ApiResponse.success(assistCalculateSummaryService.create(form));
    }

    /**
     * 创建项目辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/createProject")
    public ApiResponse<Boolean> createProject(@Valid @RequestBody CreateAssistCalculateProjectForm form) {
        return ApiResponse.success(assistCalculateSummaryService.create(form));
    }

    /**
     * 创建供应商辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/createSupplier")
    public ApiResponse<Boolean> createSupplier(@Valid @RequestBody CreateAssistCalculateSupplierForm form) {
        return ApiResponse.success(assistCalculateSummaryService.create(form));
    }

    /**
     * 修改自定义辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/updateCustom")
    public ApiResponse<Boolean> updateCustom(@Valid @RequestBody UpdateAssistCalculateCustomForm form) {
        return ApiResponse.success(assistCalculateSummaryService.update(form));
    }

    /**
     * 修改客户辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/updateCustomer")
    public ApiResponse<Boolean> updateCustomer(@Valid @RequestBody UpdateAssistCalculateCustomerForm form) {
        return ApiResponse.success(assistCalculateSummaryService.update(form));
    }

    /**
     * 修改部门辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/updateDepartment")
    public ApiResponse<Boolean> updateDepartment(@Valid @RequestBody UpdateAssistCalculateDepartmentForm form) {
        return ApiResponse.success(assistCalculateSummaryService.update(form));
    }

    /**
     * 修改职员辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/updateEmployee")
    public ApiResponse<Boolean> updateEmployee(@Valid @RequestBody UpdateAssistCalculateEmployeeForm form) {
        return ApiResponse.success(assistCalculateSummaryService.update(form));
    }

    /**
     * 修改存货辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/updateInventory")
    public ApiResponse<Boolean> updateInventory(@Valid @RequestBody UpdateAssistCalculateInventoryForm form) {
        return ApiResponse.success(assistCalculateSummaryService.update(form));
    }

    /**
     * 修改项目辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/updateProject")
    public ApiResponse<Boolean> updateProject(@Valid @RequestBody UpdateAssistCalculateProjectForm form) {
        return ApiResponse.success(assistCalculateSummaryService.update(form));
    }

    /**
     * 修改供应商辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/updateSupplier")
    public ApiResponse<Boolean> update(@Valid @RequestBody UpdateAssistCalculateSupplierForm form) {
        return ApiResponse.success(assistCalculateSummaryService.update(form));
    }

    /**
     * 查询辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/list")
    public ApiResponse<PageInfo<ListAssistCalculateSummaryVo>> list(@Valid @RequestBody ListAssistCalculateSummaryForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form));
    }

    /**
     * 查询自定义辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listCustom")
    public ApiResponse<PageInfo<ListAssistCalculateCustomVo>> listCustom(@Valid @RequestBody ListAssistCalculateForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form, ListAssistCalculateCustomVo.class));
    }

    /**
     * 查询客户辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listCustomer")
    public ApiResponse<PageInfo<ListAssistCalculateCustomerVo>> listCustomer(@Valid @RequestBody ListAssistCalculateForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form, ListAssistCalculateCustomerVo.class));
    }

    /**
     * 查询职员辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listDepartment")
    public ApiResponse<PageInfo<ListAssistCalculateDepartmentVo>> listDepartment(@Valid @RequestBody ListAssistCalculateForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form, ListAssistCalculateDepartmentVo.class));
    }

    /**
     * 查询职员辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listEmployee")
    public ApiResponse<PageInfo<ListAssistCalculateEmployeeVo>> listEmployee(@Valid @RequestBody ListAssistCalculateForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form, ListAssistCalculateEmployeeVo.class));
    }

    /**
     * 查询存货辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listInventory")
    public ApiResponse<PageInfo<ListAssistCalculateInventoryVo>> listInventory(@Valid @RequestBody ListAssistCalculateForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form, ListAssistCalculateInventoryVo.class));
    }

    /**
     * 查询项目辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listProject")
    public ApiResponse<PageInfo<ListAssistCalculateProjectVo>> listProject(@Valid @RequestBody ListAssistCalculateForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form, ListAssistCalculateProjectVo.class));
    }

    /**
     * 查询现金流辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listCashFlow")
    public ApiResponse<PageInfo<ListAssistCalculateCashFlowVo>> listCashFlow(@Valid @RequestBody ListAssistCalculateForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form, ListAssistCalculateCashFlowVo.class));
    }

    /**
     * 查询供应商辅助核算列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listSupplier")
    public ApiResponse<PageInfo<ListAssistCalculateSupplierVo>> listSupplier(@Valid @RequestBody ListAssistCalculateForm form) {
        return ApiResponse.success(assistCalculateSummaryService.list(form, ListAssistCalculateSupplierVo.class));
    }

    /**
     * 获取自定义辅助核算明细
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getCustom")
    public ApiResponse<GetAssistCalculateCustomVo> getCustom(@Valid @RequestParam Long id) {
        return ApiResponse.success(assistCalculateSummaryService.get(id, GetAssistCalculateCustomVo.class));
    }

    /**
     * 获取客户辅助核算详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getCustomer")
    public ApiResponse<GetAssistCalculateCustomerVo> getCustomer(@RequestParam("id")
                                                                 @NotNull
                                                                 @Range(min = 1)
                                                                 Long id) {
        return ApiResponse.success(assistCalculateSummaryService.get(id, GetAssistCalculateCustomerVo.class));
    }

    /**
     * 获取部门辅助核算详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getDepartment")
    public ApiResponse<GetAssistCalculateDepartmentVo> getDepartment(@RequestParam("id")
                                                                     @NotNull
                                                                     @Range(min = 1)
                                                                     Long id) {
        return ApiResponse.success(assistCalculateSummaryService.get(id, GetAssistCalculateDepartmentVo.class));
    }

    /**
     * 获取职员辅助核算详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getEmployee")
    public ApiResponse<GetAssistCalculateEmployeeVo> getEmployee(@RequestParam("id")
                                                                 @NotNull
                                                                 @Range(min = 1)
                                                                 Long id) {
        return ApiResponse.success(assistCalculateSummaryService.get(id, GetAssistCalculateEmployeeVo.class));
    }

    /**
     * 获取存货辅助核算详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getInventory")
    public ApiResponse<GetAssistCalculateInventoryVo> getInventory(@RequestParam("id")
                                                                   @NotNull
                                                                   @Range(min = 1)
                                                                   Long id) {
        return ApiResponse.success(assistCalculateSummaryService.get(id, GetAssistCalculateInventoryVo.class));
    }

    /**
     * 获取项目辅助核算详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getProject")
    public ApiResponse<GetAssistCalculateProjectVo> getProject(@RequestParam("id")
                                                               @NotNull
                                                               @Range(min = 1)
                                                               Long id) {
        return ApiResponse.success(assistCalculateSummaryService.get(id, GetAssistCalculateProjectVo.class));
    }

    /**
     * 获取供应商辅助核算详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getSupplier")
    public ApiResponse<GetAssistCalculateSupplierVo> getSupplier(@RequestParam("id")
                                                                 @NotNull
                                                                 @Range(min = 1)
                                                                 Long id) {
        return ApiResponse.success(assistCalculateSummaryService.get(id, GetAssistCalculateSupplierVo.class));
    }

    /**
     * 禁用或启用辅助核算
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/updateDisable")
    public ApiResponse<Boolean> updateDisable(@Valid @RequestBody UpdateDisableForm form) throws JsonProcessingException {
        return ApiResponse.success(assistCalculateSummaryService.updateDisable(form));
    }

    /**
     * 删除客户辅助核算
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/del")
    public ApiResponse<Boolean> del(@Valid @RequestBody DelForm form) {
        return ApiResponse.success(assistCalculateSummaryService.del(form));
    }
}
