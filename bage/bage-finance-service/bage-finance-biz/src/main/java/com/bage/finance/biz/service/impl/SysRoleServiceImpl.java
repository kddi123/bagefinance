package com.bage.finance.biz.service.impl;

import com.bage.common.constant.CommonConstant;
import com.bage.common.dto.PageHelperRequest;
import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.SysMenu;
import com.bage.finance.biz.domain.SysResource;
import com.bage.finance.biz.domain.SysRole;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.GetRoleDetailVo;
import com.bage.finance.biz.dto.vo.ListRoleVo;
import com.bage.finance.biz.dto.vo.MenuDataItemVo;
import com.bage.finance.biz.mapper.SysRoleMapper;
import com.bage.finance.biz.service.*;
import com.bage.mybatis.help.Criteria;
import com.bage.mybatis.help.MyBatisWrapper;
import com.bage.mybatis.help.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.SysRoleField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {
    final SysRoleMapper sysRoleMapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final ObjectMapper jsonMapper;
    final RedisTemplate<String, Object> redisTemplate;
    final SysMenuService sysMenuService;
    final MemberService memberService;
    final SysResourceService sysResourceService;
    final SysRoleBindMenuService sysRoleBindMenuService;
    final SysRoleBindResourceService sysRoleBindResourceService;

    /**
     * 保存角色路由
     *
     * @param form
     * @return
     */
    @Override
    public boolean create(CreateSysRoleForm form) {
        SysRole sysRole = new SysRole();
        sysRole.initDefault();
        sysRole.setMemberId(tokenService.getThreadLocalUserId());
        sysRole.setUpdateMemberId(sysRole.getMemberId());
        sysRole.setRoleName(form.getRoleName());
        sysRole.setDisable(form.getDisable());

        boolean result = sysRoleMapper.insert(sysRole) > 0;
        if (!result) {
            throw new BizException("保存角色路由失败");
        }
        return true;
    }

    /**
     * 查看角色列表
     *
     * @param form
     * @return
     */
    @Override
    public PageInfo<ListRoleVo> list(ListRoleForm form) {
        MyBatisWrapper<SysRole> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.select(Id, RoleName, Disable)
                .page(form.getPageNum(), form.getPageSize());
        Criteria where = myBatisWrapper.whereBuilder()
                .andEq(setDelFlag(false));
        if (Strings.isNotBlank(form.getRoleName())) {
            where.andLike(RoleName, "%" + form.getRoleName() + "%");
        }
        if (form.getDisable() != null) {
            where.andEq(setDisable(form.getDisable()));
        }
        myBatisWrapper.orderByDesc(CreateTime);
        PageInfo<SysRole> sysRolePageInfo = myBatisWrapper.listPage(sysRoleMapper);
        return objectConvertor.toListRoleVoPage(sysRolePageInfo);
    }

    /**
     * 查看角色列表
     *
     * @param form
     * @return
     */
    @Override
    public List<SysRole> list(PageHelperRequest form) {
        MyBatisWrapper<SysRole> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, RoleName, Disable, DelFlag)
                .page(form.getPageNum(), form.getPageSize())
                .whereBuilder();

        wrapper.orderByDesc(CreateTime);
        return sysRoleMapper.list(wrapper);
    }

    /**
     * 删除角色
     *
     * @param id
     * @return
     */
    @Override
    public boolean del(int id) {
        MyBatisWrapper<SysRole> wrapper = new MyBatisWrapper<>();
        wrapper.update(setDelFlag(true))
                .update(setUpdateTime(DateUtil.getSystemTime()))
                .update(setUpdateMemberId(tokenService.getThreadLocalUserId()))
                .whereBuilder()
                .andEq(setId(id))
                .andEq(setDelFlag(false));
        if (sysRoleMapper.updateField(wrapper) == 0) {
            return false;
        }
        //删除角色绑定的菜单缓存
        deleteSysRoleMenuCache(id);
        //删除角色绑定的资源缓存
        deleteSysRoleResourceCache(id);
        //删除会员角色id
        //todo 后续通过mq发送消息调用删除
        //memberService.delRoleIds(id);
        return true;
    }

    /**
     * 禁用角色
     *
     * @param form
     * @return
     */
    @Override
    public boolean updateDisable(UpdateRoleDisableForm form) throws JsonProcessingException {
        MyBatisWrapper<SysRole> wrapper = new MyBatisWrapper<>();
        wrapper.update(setDisable(form.getDisable()))
                .update(setUpdateTime(DateUtil.getSystemTime()))
                .update(setUpdateMemberId(tokenService.getThreadLocalUserId()))
                .whereBuilder()
                .andEq(setId(form.getId()))
                .andEq(setDelFlag(false))
                .andEq(setDisable(!form.getDisable()));
        if (sysRoleMapper.updateField(wrapper) == 0) {
            throw new BizException("禁用或启用失败");
        }
        if (form.getDisable()) {
            deleteSysRoleMenuCache(form.getId());
            deleteSysRoleResourceCache(form.getId());
        } else {
            setSysRoleMenuCache(form.getId());
            setSysRoleResourceCache(form.getId());
        }
        return true;
    }

    /**
     * 修改角色
     *
     * @param form
     * @return
     */
    @Override
    public boolean update(UpdateRoleForm form) {
        MyBatisWrapper<SysRole> wrapper = new MyBatisWrapper<>();
        wrapper.update(setRoleName(form.getRoleName()))
                .whereBuilder()
                .andEq(setId(form.getId()))
                .andEq(setDelFlag(false));

        return sysRoleMapper.updateField(wrapper) > 0;
    }

    /**
     * 获取角色明细
     *
     * @param id
     * @return
     */
    @Override
    public GetRoleDetailVo getById(int id) {
        MyBatisWrapper<SysRole> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, RoleName)
                .whereBuilder()
                .andEq(setId(id))
                .andEq(setDelFlag(false));
        SysRole sysRole = sysRoleMapper.get(wrapper);
        return objectConvertor.toGetRoleDetailVo(sysRole);
    }

    /**
     * 角色绑定资源列表
     *
     * @param form
     * @return
     */
    @Override
    public boolean roleBindMenu(RoleBindMenuForm form) {
        if (!CollectionUtils.isEmpty(form.getBindMenuIds())) {
            List<SysMenu> sysMenus = sysMenuService.listByIds(form.getBindMenuIds());
            if (form.getBindMenuIds().size() != sysMenus.size()) {
                throw new BizException("资源非法");
            }
        }
        if (sysRoleBindMenuService.roleBindMenu(form)) {
            setSysRoleMenuCache(form.getRoleId());
        }
        return true;
    }

    /**
     * 将所有角色绑定的资源设置到缓存中(通过定时任务触发)
     */
    @Override
    public void setSysRoleMenuCache() {
        PageHelperRequest form = new PageHelperRequest();
        form.setPageNum(1);
        form.setPageSize(100);
        List<SysRole> sysRoles = null;
        List<SysMenu> sysMenus = sysMenuService.list();
        while (!CollectionUtils.isEmpty(sysRoles = list(form))) {
            for (SysRole sysRole : sysRoles) {
                if (sysRole.getDisable() || sysRole.getDelFlag()) {
                    deleteSysRoleMenuCache(sysRole.getId());
                    continue;
                }
                //查询绑定的资源id列表
                List<Integer> menuIds = sysRoleBindMenuService.listBindMenuIdByRoleId(sysRole.getId());
                List<MenuDataItemVo> menuDataItemVos = listRoleBindMenu(sysRole.getId(), menuIds, sysMenus);
                updateSysRoleMenuCache(sysRole.getId(), menuDataItemVos);
            }
            form.setPageNum(form.getPageNum() + 1);
        }
    }

    /**
     * 查询当前登录用户角色绑定的菜单列表
     *
     * @return
     */
    @Override
    public List<MenuDataItemVo> listRoleBindMenu() {
        Set<Long> roleIds = tokenService.getThreadLocalUser().getSysRoleIds();
        return listRoleMenuIdByCache(roleIds);
    }

    /**
     * 角色绑定资源列表
     *
     * @param form
     * @return
     */
    @Override
    public boolean roleBindResource(RoleBindResourceForm form) {
        if (!CollectionUtils.isEmpty(form.getBindResourceIds())) {
            List<SysResource> sysResources = sysResourceService.listByIds(form.getBindResourceIds());
            if (form.getBindResourceIds().size() != sysResources.size()) {
                throw new BizException("资源非法");
            }
        }
        if (sysRoleBindResourceService.roleBindResource(form)) {
            setSysRoleResourceCache(form.getRoleId());
        }
        return true;
    }

    /**
     * 将所有角色绑定的资源设置到缓存中(通过定时任务触发)
     */
    @Override
    public void setSysRoleResourceCache() {
        PageHelperRequest form = new PageHelperRequest();
        form.setPageNum(1);
        List<SysRole> sysRoles = null;
        while (!CollectionUtils.isEmpty(sysRoles = list(form))) {
            for (SysRole sysRole : sysRoles) {
                if (sysRole.getDisable() || sysRole.getDelFlag()) {
                    deleteSysRoleResourceCache(sysRole.getId());
                    continue;
                }
                //查询绑定的资源id列表
                List<Integer> resourceIds = sysRoleBindResourceService.listBindResourceIdByRoleId(sysRole.getId());
                List<SysResource> sysResources = sysResourceService.listByIds(resourceIds);
                updateSysRoleResourceCache(sysRole.getId(),
                        sysResources.stream().map(SysResource::getPath).collect(Collectors.toSet()));
            }
            form.setPageNum(form.getPageNum() + 1);
        }
    }

    /**
     * 将某个角色绑定的菜单设置到缓存中
     *
     * @param roleId
     */
    private void setSysRoleMenuCache(int roleId) {
        List<SysMenu> sysMenus = sysMenuService.list();
        //查询绑定的资源id列表
        List<Integer> menuIds = sysRoleBindMenuService.listBindMenuIdByRoleId(roleId);
        List<MenuDataItemVo> menuDataItemVos = listRoleBindMenu(roleId, menuIds, sysMenus);
        updateSysRoleMenuCache(roleId, menuDataItemVos);
    }

    /**
     * 更新角色资源路由缓存
     *
     * @param roleId          角色id
     * @param menuDataItemVos 资源路由
     */
    private void updateSysRoleMenuCache(int roleId, List<MenuDataItemVo> menuDataItemVos) {
        String cacheKey = CommonConstant.ROLE_MENU_PERMISSIONS;
        HashOperations<String, String, List<MenuDataItemVo>> hashOps = redisTemplate.opsForHash();
        hashOps.put(cacheKey, String.valueOf(roleId), menuDataItemVos);
    }

    /**
     * 删除角色资源路由缓存
     *
     * @param roleId 角色id
     */
    private void deleteSysRoleMenuCache(int roleId) {
        String cacheKey = CommonConstant.ROLE_MENU_PERMISSIONS;
        HashOperations<String, String, List<MenuDataItemVo>> hashOps = redisTemplate.opsForHash();
        hashOps.delete(cacheKey, String.valueOf(roleId));
    }

    /**
     * 从缓存中获取角色绑定的菜单
     *
     * @param roleIds
     * @return
     */
    private List<MenuDataItemVo> listRoleMenuIdByCache(Set<Long> roleIds) {
        HashOperations<String, String, List<MenuDataItemVo>> hashOps = redisTemplate.opsForHash();
        List<List<MenuDataItemVo>> roleMenuIds = hashOps.multiGet(CommonConstant.ROLE_MENU_PERMISSIONS, roleIds.stream().map(String::valueOf).collect(Collectors.toSet()));
        // 对结果进行处理，将 List<List<Integer>> 转为 List<Integer>
        return roleMenuIds.stream()
                .filter(p -> !CollectionUtils.isEmpty(p))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 查询角色绑定的菜单列表
     *
     * @return
     */
    private List<MenuDataItemVo> listRoleBindMenu(int roleId, List<Integer> menuIds, List<SysMenu> sysMenus) {
        Set<Integer> parentIds = sysMenus.stream().filter(p -> (CommonConstant.ROLE_ADMIN == roleId || menuIds.contains(p.getId()))
                && p.getPid() > 0)
                .map(SysMenu::getPid).collect(Collectors.toSet());
        //查询所有子节点
        List<SysMenu> parentMenus = sysMenus.stream().filter(p -> parentIds.contains(p.getId())).collect(Collectors.toList());
        List<MenuDataItemVo> parentMenuDataItemVos = objectConvertor.toMenuDataItemVo(parentMenus);
        for (MenuDataItemVo parentMenuDataItemVo : parentMenuDataItemVos) {
            if (parentMenuDataItemVo.getLayout()) {
                parentMenuDataItemVo.setLayout(null);
            }
            List<SysMenu> childMenus = sysMenus.stream().filter(p -> p.getPid().equals(parentMenuDataItemVo.getId())
                    && (CommonConstant.ROLE_ADMIN == roleId || menuIds.contains(p.getId())))
                    .collect(Collectors.toList());
            parentMenuDataItemVo.setRoutes(objectConvertor.toMenuDataItemVo(childMenus).stream().peek(p -> {
                if (p.getLayout()) {
                    p.setLayout(null);
                }
            }).collect(Collectors.toList()));
        }
        return parentMenuDataItemVos;
    }


    /**
     * 将某个角色绑定的资源设置到缓存中
     *
     * @param roleId
     */
    private void setSysRoleResourceCache(int roleId) {
        //查询绑定的资源id列表
        List<Integer> resourceIds = sysRoleBindResourceService.listBindResourceIdByRoleId(roleId);
        List<SysResource> sysResources = sysResourceService.listByIds(resourceIds);
        if (CollectionUtils.isEmpty(sysResources)) {
            updateSysRoleResourceCache(roleId, new HashSet<>());
        } else {
            updateSysRoleResourceCache(roleId,
                    sysResources.stream().map(SysResource::getPath).collect(Collectors.toSet()));
        }
    }

    /**
     * 更新角色资源路由缓存
     *
     * @param roleId       角色id
     * @param resourcePath 资源路由
     */
    private void updateSysRoleResourceCache(int roleId, Set<String> resourcePath) {
        String cacheKey = CommonConstant.ROLE_RESOURCE_PERMISSIONS;
        HashOperations<String, String, Set<String>> hashOps = redisTemplate.opsForHash();
        hashOps.put(cacheKey, String.valueOf(roleId), resourcePath);
    }

    /**
     * 删除角色资源路由缓存
     *
     * @param roleId 角色id
     */
    private void deleteSysRoleResourceCache(int roleId) {
        String cacheKey = CommonConstant.ROLE_RESOURCE_PERMISSIONS;
        HashOperations<String, String, Set<String>> hashOps = redisTemplate.opsForHash();
        hashOps.delete(cacheKey, String.valueOf(roleId));
    }
}
