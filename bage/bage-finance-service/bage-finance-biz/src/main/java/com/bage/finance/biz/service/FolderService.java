package com.bage.finance.biz.service;


import com.bage.finance.biz.dto.form.CreateFolderForm;
import com.bage.finance.biz.dto.form.DelForm;
import com.bage.finance.biz.dto.form.ListFolderForm;
import com.bage.finance.biz.dto.form.UpdateFolderForm;
import com.bage.finance.biz.dto.vo.GetFolderVo;
import com.bage.finance.biz.dto.vo.ListFolderVo;

import java.util.List;

public interface FolderService {
    /**
     * 创建文件夹
     * @param form
     * @return
     */
    long create(CreateFolderForm form);

    /**
     * 修改文件夹
     * @param form
     * @return
     */
    boolean update(UpdateFolderForm form);

    /**
     * 修改文件夹
     *
     * @param form
     * @return
     */
    boolean del(DelForm form);

    /**
     * 查询文件夹列表
     * @param form
     * @return
     */
    List<ListFolderVo> list(ListFolderForm form);

    /**
     * 查询文件夹
     *
     * @param id
     * @return
     */
    GetFolderVo get(long id);
}
