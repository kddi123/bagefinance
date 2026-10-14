package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.CreateCurrencyConfigForm;
import com.bage.finance.biz.dto.form.DelCurrencyConfigForm;
import com.bage.finance.biz.dto.form.UpdateCurrencyConfigForm;
import com.bage.finance.biz.dto.vo.GetCurrencyConfigVo;
import com.bage.finance.biz.dto.vo.ListCurrencyConfigVo;
import com.bage.finance.biz.service.CurrencyConfigService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 币别设置
 */
@RestController
@RequestMapping(value = "/currencyConfig")
@RequiredArgsConstructor
@Slf4j
public class CurrencyConfigController {
    final CurrencyConfigService currencyConfigService;

    /**
     * 添加币别
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/create")
    public ApiResponse<Boolean> create(@Valid @RequestBody CreateCurrencyConfigForm form) {
        return ApiResponse.success(currencyConfigService.create(form));
    }

    /**
     * 删除币别
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/del")
    public ApiResponse<Boolean> del(@Valid @RequestBody DelCurrencyConfigForm form) {
        return ApiResponse.success(currencyConfigService.del(form));
    }

    /**
     * 修改币别
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/update")
    public ApiResponse<Boolean> update(@Valid @RequestBody UpdateCurrencyConfigForm form) {
        return ApiResponse.success(currencyConfigService.update(form));
    }

    /**
     * 获取币别详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/get")
    public ApiResponse<GetCurrencyConfigVo> getById(
            @Validated
            @RequestParam
            @NotNull
            @Range(min = 1)
            Long id) {
        return ApiResponse.success(currencyConfigService.getById(id));
    }

    /**
     * 查询币别列表
     *
     * @return
     */
    @GetMapping(value = "/list")
    public ApiResponse<List<ListCurrencyConfigVo>> list() {
        return ApiResponse.success(currencyConfigService.list());
    }
}
