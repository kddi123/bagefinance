package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.SysMenu;
import com.bage.finance.biz.dto.form.CreateMenuForm;
import com.bage.finance.biz.dto.form.DelMenuForm;
import com.bage.finance.biz.dto.form.UpdateMenuForm;
import com.bage.finance.biz.dto.vo.GetMenuByIdVo;
import com.bage.finance.biz.dto.vo.ListTreeMenuVo;
import com.bage.finance.biz.dto.vo.ListTreeSelectMenuVo;

import java.util.List;
import java.util.Set;

public interface SysMenuService {

    /**
     * 获取树形菜单
     *
     * @return
     */
    List<ListTreeMenuVo> listTreeMenu(String name);

    /**
     * 获取树形菜单
     *
     * @return
     */
    List<ListTreeMenuVo> listTreeMenuByMenuIds();

    /**
     * 获取树形选择菜单
     *
     * @return
     */
    List<ListTreeSelectMenuVo> listTreeSelectMenu();

    /**
     * 获取菜单明细
     *
     * @param id
     * @return
     */
    GetMenuByIdVo getMenuById(int id);

    /**
     * 创建菜单
     *
     * @param form
     * @return
     */
    boolean create(CreateMenuForm form);

    /**
     * 修改菜单
     *
     * @param form
     * @return
     */
    boolean update(UpdateMenuForm form);

    /**
     * 删除菜单
     *
     * @param form
     * @return
     */
    boolean del(DelMenuForm form);

    /**
     * 根据id查询菜单列表
     *
     * @param ids
     * @return
     */
    List<SysMenu> listByIds(List<Integer> ids);

    /**
     * 查询菜单列表
     *
     * @return
     */
    List<SysMenu> list();
}
