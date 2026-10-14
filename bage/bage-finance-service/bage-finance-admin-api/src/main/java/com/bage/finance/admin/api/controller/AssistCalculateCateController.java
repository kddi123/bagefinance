package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.CreateAssistCalculateCateForm;
import com.bage.finance.biz.dto.form.DelAssistCalculateCateForm;
import com.bage.finance.biz.dto.form.UpdateAssistCalculateCateForm;
import com.bage.finance.biz.dto.vo.GetAssistCalculateCateVo;
import com.bage.finance.biz.dto.vo.ListCalculateCateVo;
import com.bage.finance.biz.service.AssistCalculateCateService;
import com.fasterxml.jackson.core.JsonProcessingException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 辅助核算类别管理
 */
@RestController
@RequestMapping(value = "/assistCalculateCate")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AssistCalculateCateController {
    final AssistCalculateCateService assistCalculateCateService;

    /**
     * 创建辅助核算类别
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/create")
    public ApiResponse<Boolean> create(@Valid @RequestBody CreateAssistCalculateCateForm form) throws JsonProcessingException {
        return ApiResponse.success(assistCalculateCateService.create(form));
    }

    /**
     * 删除辅助核算类别
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/del")
    public ApiResponse<Boolean> del(@Valid @RequestBody DelAssistCalculateCateForm form) {
        return ApiResponse.success(assistCalculateCateService.del(form));
    }

    /**
     * 修改辅助核算类别
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/update")
    public ApiResponse<Boolean> update(@Valid @RequestBody UpdateAssistCalculateCateForm form) throws JsonProcessingException {
        return ApiResponse.success(assistCalculateCateService.update(form));
    }

    /**
     * 查询辅助核算类别列表
     *
     * @return
     */
    @GetMapping(value = "/list")
    public ApiResponse<List<ListCalculateCateVo>> list() {
        return ApiResponse.success(assistCalculateCateService.list());
    }

    /**
     * 获取辅助核算类别详情
     *
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/getById")
    public ApiResponse<GetAssistCalculateCateVo> getById(@RequestParam
                                                         @NotNull
                                                         @Range(min = 1)
                                                         Long id) throws JsonProcessingException {
        return ApiResponse.success(assistCalculateCateService.getById(id));
    }
}
