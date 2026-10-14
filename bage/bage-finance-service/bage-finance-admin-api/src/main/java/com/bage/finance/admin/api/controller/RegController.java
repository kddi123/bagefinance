package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.GenerateMpRegCodeForm;
import com.bage.finance.biz.dto.form.PhoneRegisterForm;
import com.bage.finance.biz.dto.vo.GenerateMpRegCodeVo;
import com.bage.finance.biz.service.MemberRegService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 注册模块
 */
@RestController
@RequestMapping(value = "/reg")
@RequiredArgsConstructor
@Slf4j
public class RegController {
    final MemberRegService memberRegService;

    /**
     * 手机号注册
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/phoneReg")
    public ApiResponse<Long> phoneReg(@Validated @RequestBody PhoneRegisterForm form) {
        return ApiResponse.success(memberRegService.phoneReg(form));
    }

    /**
     * 生成微信公众号二维码（关注注册）
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/generateMpRegCode")
    public ApiResponse<GenerateMpRegCodeVo> generateMpRegCode(@Validated @ModelAttribute GenerateMpRegCodeForm request) {
        GenerateMpRegCodeVo result = memberRegService.generateMpRegCode(request.getClientId());
        return ApiResponse.success(result);
    }
}
