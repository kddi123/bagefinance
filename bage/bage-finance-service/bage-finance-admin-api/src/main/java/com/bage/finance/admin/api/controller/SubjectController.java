package com.bage.finance.admin.api.controller;

import com.bage.common.dto.ApiResponse;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.GetSubjectDetailVo;
import com.bage.finance.biz.dto.vo.GetSubjectVo;
import com.bage.finance.biz.dto.vo.ListSubjectByCateAndCodeAndNameVo;
import com.bage.finance.biz.dto.vo.ListSubjectVo;
import com.bage.finance.biz.service.SubjectService;
import com.fasterxml.jackson.core.JsonProcessingException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * 科目管理
 */
@RestController
@RequestMapping(value = "/subject")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SubjectController {
    final SubjectService subjectService;

    /**
     * 创建科目
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/create")
    public ApiResponse<Boolean> create(@Valid @RequestBody CreateSubjectForm form) throws JsonProcessingException {
        return ApiResponse.success(subjectService.create(form));
    }

    /**
     * 创建科目
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/del")
    public ApiResponse<Boolean> del(@Valid @RequestBody DelForm form) throws JsonProcessingException {
        return ApiResponse.success(subjectService.del(form));
    }

    /**
     * 修改科目
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/update")
    public ApiResponse<Boolean> update(@Valid @RequestBody UpdateSubjectForm form) throws JsonProcessingException {
        return ApiResponse.success(subjectService.update(form));
    }

    /**
     * 查询科目列表
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/list")
    public ApiResponse<List<ListSubjectVo>> list(@Valid @RequestBody ListSubjectForm form) {
        return ApiResponse.success(subjectService.list(form));
    }

    /**
     * 查询科目列表(根据类别编码和名称查询)
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/listByCateAndCodeAndName")
    public ApiResponse<List<ListSubjectByCateAndCodeAndNameVo>> listByCateAndCodeAndName(@Valid @RequestBody ListSubjectByCateAndCodeAndNameForm form) {
        return ApiResponse.success(subjectService.list(form));
    }

    /**
     * 获取科目详情
     *
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/get")
    public ApiResponse<GetSubjectVo> get(@RequestParam("id")
                                         @NotNull
                                         @Range(min = 1)
                                         Long id) throws JsonProcessingException {
        return ApiResponse.success(subjectService.get(id));
    }

    /**
     * 获取科目详情（凭证选择会计科目使用）
     *
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/getDetail")
    public ApiResponse<GetSubjectDetailVo> getDetail(@RequestParam("id")
                                                     @NotNull
                                                     @Range(min = 1)
                                                     Long id) throws JsonProcessingException {
        return ApiResponse.success(subjectService.getDetail(id));
    }

    /**
     * 获取父科目详情
     *
     * @param pid
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/getByPid")
    public ApiResponse<GetSubjectVo> getByPid(@RequestParam("pid")
                                              @NotNull
                                              @Range(min = 1)
                                              Long pid) throws JsonProcessingException {
        return ApiResponse.success(subjectService.getByPid(pid));
    }

    /**
     * 禁用或启用科目
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/disable")
    public ApiResponse<Boolean> disable(@Valid @RequestBody DisableSubjectForm form) {
        return ApiResponse.success(subjectService.disable(form));
    }

    /**
     * 导出
     *
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response) throws IOException {
        subjectService.download(response);
    }
}
