package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.Subject;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.GetSubjectDetailVo;
import com.bage.finance.biz.dto.vo.GetSubjectVo;
import com.bage.finance.biz.dto.vo.ListSubjectByCateAndCodeAndNameVo;
import com.bage.finance.biz.dto.vo.ListSubjectVo;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SubjectService {

    /**
     * 创建科目
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    boolean create(CreateSubjectForm form) throws JsonProcessingException;

    /**
     * 删除科目
     *
     * @param form
     * @return
     */
    boolean del(DelForm form) throws JsonProcessingException;

    /**
     * 禁用或启用科目
     *
     * @param form
     * @return
     */
    boolean disable(DisableSubjectForm form);

    /**
     * 修改币别
     *
     * @param form
     * @return
     */
    boolean update(UpdateSubjectForm form) throws JsonProcessingException;

    /**
     * 查询科目列表
     *
     * @return
     */
    List<ListSubjectVo> list(ListSubjectForm form);

    /**
     * 查询科目列表
     *
     * @param form
     * @return
     */
    List<ListSubjectByCateAndCodeAndNameVo> list(ListSubjectByCateAndCodeAndNameForm form);

    /**
     * 查询科目
     *
     * @return
     */
    GetSubjectVo get(long id) throws JsonProcessingException;

    /**
     * 查询科目列表
     *
     * @return
     */
    GetSubjectDetailVo getDetail(long id) throws JsonProcessingException;

    /**
     * 查询科目列表
     *
     * @return
     */
    List<GetSubjectVo> list(Set<Long> ids, long tenantId);

    /**
     * 查询id，编码，名称
     *
     * @param ids
     * @return
     */
    List<Subject> listIdAndCodeAndName(Set<Long> ids);

    /**
     * 查询科目列表
     *
     * @return
     */
    GetSubjectVo getByPid(long pid) throws JsonProcessingException;

    /**
     * 增加科目使用计数
     *
     * @param id
     * @return
     */
    boolean addUseCount(long id);

    /**
     * 增加科目使用计数
     *
     * @param id
     * @param count
     * @return
     */
    boolean addUseCount(long id, int count);

    /**
     * 增加科目使用计数
     *
     * @param ids
     * @return
     */
    boolean addUseCount(Set<Long> ids);

    /**
     * 减少科目使用计数
     *
     * @param id
     * @param count
     * @return
     */
    boolean deductUseCount(long id, int count);

    /**
     * 导出科目
     *
     * @param response
     */
    void download(HttpServletResponse response) throws IOException;

    /**
     * 获取科目全名称（含父级名称）
     *
     * @param subjects
     * @return
     */
    Map<Long, String> getSubjectFullName(List<Subject> subjects);

    /**
     * 获取科目全名称（含父级名称）
     *
     * @param subjects
     * @return
     */
    Map<Long, List<Subject>> getParentSubjectList(List<Subject> subjects);
}
