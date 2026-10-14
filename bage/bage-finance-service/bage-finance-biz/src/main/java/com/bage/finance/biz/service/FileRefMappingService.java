package com.bage.finance.biz.service;

import com.bage.finance.biz.dto.form.ListFileRefMappingFileIdsForm;
import com.bage.finance.biz.enums.FileRefTypeEnum;

import java.util.Set;

public interface FileRefMappingService {
    /**
     * 保存文件映射
     *
     * @param fileRefTypeEnum 引用类型
     * @param fileRefId       引用id
     * @param fileIds         映射的文件id列表
     * @return
     */
    boolean save(FileRefTypeEnum fileRefTypeEnum, long fileRefId, Set<Long> fileIds,
                 long tenantId,
                 long memberId);

    /**
     * 获取文件id列表
     *
     * @param form
     * @return
     */
    Set<Long> listFileIds(ListFileRefMappingFileIdsForm form);
}