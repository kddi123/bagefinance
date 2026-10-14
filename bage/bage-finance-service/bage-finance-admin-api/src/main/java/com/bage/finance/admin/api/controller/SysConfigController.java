package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.SaveSendSmsCodeTemplateConfigForm;
import com.bage.finance.biz.service.SysConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
 import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统配置
 */
@RestController
@RequestMapping(value = "/sysConfig")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysConfigController {
    final SysConfigService sysConfigService;

    /**
     * 保存短信验证码模板配置
      * @param form
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/saveSendSmsCodeTemplateConfig")
    public ApiResponse<Boolean> saveSendSmsCodeTemplateConfig(@Valid @RequestBody SaveSendSmsCodeTemplateConfigForm form) throws JsonProcessingException {
        return ApiResponse.success(sysConfigService.saveSendSmsCodeTemplateConfig(form));
    }
}
