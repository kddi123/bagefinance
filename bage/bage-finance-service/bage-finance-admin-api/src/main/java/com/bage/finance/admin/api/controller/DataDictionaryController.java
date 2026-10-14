package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.vo.DataDictionaryVo;
import com.bage.finance.biz.service.DataDictionaryService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典
 */
@RestController
@RequestMapping(value = "/DataDictionary")
@RequiredArgsConstructor
@Slf4j
@Validated
public class DataDictionaryController {
    final DataDictionaryService dataDictionaryService;

    /**
     * 查询行业数据列表
     *
     * @return
     */
    @GetMapping(value = "/listHangYe")
    public ApiResponse<List<DataDictionaryVo>> listHangYe() {
        return ApiResponse.success(dataDictionaryService.listHangYe());
    }

    /**
     * 查询会计准则数据列表
     *
     * @return
     */
    @GetMapping(value = "/listKuaiJiZhunZe")
    public ApiResponse<List<DataDictionaryVo>> listKuaiJiZhunZe() {
        return ApiResponse.success(dataDictionaryService.listKuaiJiZhunZe());
    }
}
