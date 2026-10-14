package com.bage.finance.biz.service;


import com.bage.finance.biz.domain.SysResource;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.*;

import java.util.List;

public interface SysResourceService {
    /**
     * 创建资源
     *
     * @param form
     * @return
     */
    boolean create(CreateSysResourceForm form);

    /**
     * 修改资源
     *
     * @param form
     * @return
     */
    boolean update(UpdateSysResourceForm form);

    /**
     * 删除资源
     *
     * @param form
     * @return
     */
    boolean del(DelSysResourceForm form);


    /**
     * 获取资源
     *
     * @param id
     * @return
     */
    GetSysResourceVo get(int id);

    /**
     * 获取资源列表
     *
     * @param form
     * @return
     */
    List<ListSysResourceVo> list(ListSysResourceForm form);

    /**
     * 根据id列表查询数量
     *
     * @param ids
     * @return
     */
    int countByIds(List<Integer> ids);

    /**
     * 根据id查询资源列表
     *
     * @param ids
     * @return
     */
    List<SysResource> listByIds(List<Integer> ids);
}
