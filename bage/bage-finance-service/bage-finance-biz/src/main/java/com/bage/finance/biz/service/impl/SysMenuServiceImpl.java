package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.constant.RedisKeyConstant;
import com.bage.finance.biz.domain.SysMenu;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.CreateMenuForm;
import com.bage.finance.biz.dto.form.DelMenuForm;
import com.bage.finance.biz.dto.form.UpdateMenuForm;
import com.bage.finance.biz.dto.vo.GetMenuByIdVo;
import com.bage.finance.biz.dto.vo.ListTreeMenuVo;
import com.bage.finance.biz.dto.vo.ListTreeSelectMenuVo;
import com.bage.finance.biz.mapper.SysMenuMapper;
import com.bage.finance.biz.service.SysMenuService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.SysMenuField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {
    final SysMenuMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final RedisTemplate<String, Object> redisTemplate;
    final RedissonClient redissonClient;

    /**
     * 获取树形菜单
     *
     * @return
     */
    @Override
    public List<ListTreeMenuVo> listTreeMenu(String name) {
        if (Strings.isNotBlank(name)) {
            List<SysMenu> sysMenus = list(name);
            return objectConvertor.toListTreeMenuVo(sysMenus);
        } else {
            return listTreeMenuByMenuIds();
        }
    }

    /**
     * 获取树形菜单
     *
     * @return
     */
    @Override
    public List<ListTreeMenuVo> listTreeMenuByMenuIds() {
        List<ListTreeMenuVo> result = null;
        List<SysMenu> list = list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<SysMenu> parentMenuList = list.stream().filter(p -> p.getPid() == 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(parentMenuList)) {
            return null;
        }
        result = objectConvertor.toListTreeMenuVo(parentMenuList);
        for (ListTreeMenuVo listTreeMenuVo : result) {
            List<SysMenu> childMenuList = list.stream().filter(p -> Objects.equals(p.getPid(), listTreeMenuVo.getId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(childMenuList)) {
                continue;
            }
            listTreeMenuVo.setChildren(objectConvertor.toListTreeMenuVo(childMenuList));
        }
        return result;
    }

    /**
     * 获取树形选择菜单
     *
     * @return
     */
    @Override
    public List<ListTreeSelectMenuVo> listTreeSelectMenu() {
        List<ListTreeSelectMenuVo> result = new ArrayList<>();
        ListTreeSelectMenuVo rootMenu = new ListTreeSelectMenuVo();
        rootMenu.setId(0);
        rootMenu.setValue("0");
        rootMenu.setTitle("根菜单");
        result.add(rootMenu);
        List<SysMenu> list = list();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }

        List<SysMenu> parentMenuList = list.stream().filter(p -> p.getPid() == 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(parentMenuList)) {
            return result;
        }
        rootMenu.setChildren(objectConvertor.toListTreeSelectMenuVo(parentMenuList));
        for (ListTreeSelectMenuVo listTreeSelectMenuVo : rootMenu.getChildren()) {
            List<SysMenu> childMenuList = list.stream().filter(p -> Objects.equals(p.getPid(), listTreeSelectMenuVo.getId()))
                    .collect(Collectors.toList());
            listTreeSelectMenuVo.setChildren(objectConvertor.toListTreeSelectMenuVo(childMenuList));
        }
        return result;
    }

    @Override
    public GetMenuByIdVo getMenuById(int id) {
        SysMenu sysMenu = get(id);
        return objectConvertor.toGetMenuByIdVo(sysMenu);
    }

    /**
     * 创建菜单
     *
     * @param form
     * @return
     */
    @Override
    public boolean create(CreateMenuForm form) {
        RLock rLock = redissonClient.getLock(RedisKeyConstant.CHANGE_MENU_LOCK);
        try {
            rLock.lock();
            //检测父级菜单
            int parentLevel = checkParent(form.getPid());
            //更新菜单顺序
            updateSort(form.getPid(), form.getSort());
            SysMenu sysMenu = objectConvertor.toSysMenu(form);
            sysMenu.initDefault();
            sysMenu.setNodePath(parentLevel + 1);
            sysMenu.setMemberId(tokenService.getThreadLocalUserId());
            sysMenu.setUpdateMemberId(tokenService.getThreadLocalUserId());
            mapper.insert(sysMenu);
            return true;
        } catch (Exception ex) {
            throw new BizException(ex.getMessage(), ex);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 修改菜单
     *
     * @param form
     * @return
     */
    @Override
    public boolean update(UpdateMenuForm form) {
        SysMenu sysMenu = get(form.getId());
        if (sysMenu == null) {
            throw new BizException("菜单不存在");
        }
        RLock rLock = redissonClient.getLock(RedisKeyConstant.CHANGE_MENU_LOCK);
        try {
            rLock.lock();
            //更新菜单顺序
            updateSort(sysMenu.getPid(), form.getSort());
            MyBatisWrapper<SysMenu> wrapper = new MyBatisWrapper<>();
            wrapper
                    .update(Name, form.getName())
                    .update(setPath(form.getPath()))
                    .update(setComponent(form.getComponent()))
                    .update(setIcon(form.getIcon()))
                    .update(setLayout(form.getLayout()))
                    .update(setHideInMenu(form.getHideInMenu()))
                    .update(setRedirect(form.getRedirect()))
                    .update(setSort(form.getSort()))
                    .whereBuilder()
                    .andEq(setId(form.getId()))
                    .andEq(setDelFlag(false));
            return mapper.updateField(wrapper) > 0;
        } catch (Exception ex) {
            throw new BizException(ex.getMessage(), ex);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 删除菜单
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelMenuForm form) {
        RLock rLock = redissonClient.getLock(RedisKeyConstant.CHANGE_MENU_LOCK);
        try {
            rLock.lock();
            if (countByPid(form.getId()) > 0) {
                throw new BizException("该菜单下有子菜单不能直接删除");
            }
            MyBatisWrapper<SysMenu> wrapper = new MyBatisWrapper<>();
            wrapper.update(setDelFlag(true))
                    .update(setUpdateMemberId(tokenService.getThreadLocalUserId()))
                    .update(setUpdateTime(DateUtil.getSystemTime()))
                    .whereBuilder()
                    .andEq(setId(form.getId()));
            return mapper.updateField(wrapper) > 0;
        } catch (Exception ex) {
            throw new BizException(ex.getMessage(), ex);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 根据id查询菜单列表
     *
     * @param ids
     * @return
     */
    @Override
    public List<SysMenu> listByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        MyBatisWrapper<SysMenu> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Path)
                .whereBuilder()
                .andIn(Id, ids)
                .andEq(DelFlag, false);
        return mapper.list(wrapper);
    }

    /**
     * 查询菜单列表
     *
     * @return
     */
    @Override
    public List<SysMenu> list() {
        MyBatisWrapper<SysMenu> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Path, Component, Icon, Layout, HideInMenu, Redirect, Sort, NodePath)
                .whereBuilder().andEq(setDelFlag(false))
                .andEq(setDisable(false));
        wrapper.orderByAsc(Sort);
        return mapper.list(wrapper);
    }

    /**
     * 查询菜单列表
     *
     * @return
     */
    private List<SysMenu> list(String name) {
        MyBatisWrapper<SysMenu> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Path, Component, Icon, Layout, HideInMenu, Redirect, Sort, NodePath)
                .whereBuilder().andEq(setDelFlag(false))
                .andEq(setDisable(false))
                .andLike(Name, "%" + name + "%");
        wrapper.orderByAsc(Sort);
        return mapper.list(wrapper);
    }

    /**
     * 获取菜单
     *
     * @param id
     * @return
     */
    private SysMenu get(int id) {
        MyBatisWrapper<SysMenu> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Path, Component, Icon, Layout, HideInMenu, Redirect, Sort, NodePath)
                .whereBuilder()
                .andEq(setId(id))
                .andEq(setDelFlag(false))
                .andEq(setDisable(false));
        return mapper.get(wrapper);
    }

    /**
     * 统计子菜单数量
     *
     * @param pid
     * @return
     */
    private int countByPid(int pid) {
        MyBatisWrapper<SysMenu> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Path, Component, Icon, Layout, HideInMenu, Redirect, Sort)
                .whereBuilder()
                .andEq(setPid(pid))
                .andEq(setDelFlag(false))
                .andEq(setDisable(false));
        return mapper.count(wrapper);
    }


    /**
     * 更新菜单顺序
     *
     * @param pid
     * @param sort
     * @return
     */
    private int updateSort(int pid, int sort) {
        MyBatisWrapper<SysMenu> wrapper = new MyBatisWrapper<>();
        wrapper
                .updateIncr(setSort(1))
                .whereBuilder()
                .andEq(setPid(pid))
                .andGTE(setSort(sort))
                .andEq(setDelFlag(false));
        return mapper.updateField(wrapper);
    }

    /**
     * 检测父级菜单 并返回level级别
     *
     * @param pid
     */
    private int checkParent(int pid) {
        if (pid == 0) {
            return 0;
        }
        SysMenu sysMenu = get(pid);
        if (sysMenu == null) {
            throw new BizException("父级菜单不存在");
        }
        if (sysMenu.getPid() > 0) {
            throw new BizException("不可以创建三级菜单");
        }
        return sysMenu.getNodePath();
    }
}
