package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.constant.RedisKeyConstant;
import com.bage.finance.biz.domain.SysResource;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.CreateSysResourceForm;
import com.bage.finance.biz.dto.form.DelSysResourceForm;
import com.bage.finance.biz.dto.form.ListSysResourceForm;
import com.bage.finance.biz.dto.form.UpdateSysResourceForm;
import com.bage.finance.biz.dto.vo.GetSysResourceVo;
import com.bage.finance.biz.dto.vo.ListSysResourceVo;
import com.bage.finance.biz.mapper.SysResourceMapper;
import com.bage.finance.biz.service.SysResourceService;
import com.bage.mybatis.help.Criteria;
import com.bage.mybatis.help.MyBatisWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.SysResourceField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SysResourceServiceImpl implements SysResourceService {
    final SysResourceMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final ObjectMapper jsonMapper;
    final RedisTemplate<String, Object> redisTemplate;
    final RedissonClient redissonClient;

    /**
     * 创建资源
     *
     * @param form
     * @return
     */
    @Override
    public boolean create(CreateSysResourceForm form) {
        SysResource saveSysResource = objectConvertor.toSysResource(form);
        saveSysResource.initDefault();
        saveSysResource.setMemberId(tokenService.getThreadLocalUserId());
        saveSysResource.setUpdateMemberId(tokenService.getThreadLocalUserId());
        RLock rLock = redissonClient.getLock(RedisKeyConstant.CHANGE_RESOURCE_LOCK);
        try {
            rLock.lock();
            SysResource sysResource = getByPath(form.getPath());
            if (sysResource != null) {
                throw new BizException("该资源路径已存在");
            }
            if (form.getPid() > 0) {
                SysResource parentSysResource = getById(form.getPid());
                if (parentSysResource == null) {
                    throw new BizException("上级资源不存在");
                }
                if (parentSysResource.getPid() > 0) {
                    throw new BizException("不能创建三级资源");
                }
                checkPath(parentSysResource.getPath(), form.getPath());
            } else {
                checkPath(form.getPath(), null);
            }
            return mapper.insert(saveSysResource) > 0;
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 修改资源
     *
     * @param form
     * @return
     */
    @Override
    public boolean update(UpdateSysResourceForm form) {
        RLock rLock = redissonClient.getLock(RedisKeyConstant.CHANGE_RESOURCE_LOCK);
        try {
            rLock.lock();
            SysResource sysResource = getById(form.getId());
            if (sysResource == null) {
                throw new BizException("资源不存在");
            }
            SysResource sysResourceByPath = getByPath(form.getPath());
            if (sysResourceByPath != null && !sysResourceByPath.getId().equals(form.getId())) {
                throw new BizException("该资源路径已存在");
            }
            if (sysResource.getPid() > 0) {
                SysResource parentSysResource = getById(sysResource.getPid());
                if (parentSysResource == null) {
                    throw new BizException("上级资源不存在");
                }
                if (parentSysResource.getPid() > 0) {
                    throw new BizException("不能创建三级资源");
                }
                checkPath(parentSysResource.getPath(), form.getPath());
            } else {
                checkPath(form.getPath(), null);
            }

            MyBatisWrapper<SysResource> wrapper = new MyBatisWrapper<>();
            wrapper.update(Name, form.getName())
                    .update(Path, form.getPath())
                    .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                    .whereBuilder()
                    .andEq(Id, form.getId());
            return mapper.updateField(wrapper) > 0;
        } catch (Exception ex) {
            throw ex;
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 删除资源
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelSysResourceForm form) {
        RLock rLock = redissonClient.getLock(RedisKeyConstant.CHANGE_RESOURCE_LOCK);
        try {
            rLock.lock();
            int count = countByPid(form.getId());
            if (count > 0) {
                throw new BizException("该资源下面还有子资源，不能删除");
            }
            MyBatisWrapper<SysResource> wrapper = new MyBatisWrapper<>();
            wrapper.updateConcat(Path, "@DEL_" + form.getId())
                    .update(DelFlag, true)
                    .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                    .whereBuilder()
                    .andEq(Id, form.getId());
            return mapper.updateField(wrapper) > 0;
        } catch (Exception ex) {
            throw ex;
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 获取资源
     *
     * @param id
     * @return
     */
    @Override
    public GetSysResourceVo get(int id) {
        SysResource sysResource = getById(id);
        if (sysResource == null) {
            throw new BizException("资源不存在");
        }
        GetSysResourceVo result = objectConvertor.toGetSysResourceVo(sysResource);
        result.setParentName("无上级资源");
        if (sysResource.getPid() > 0) {
            SysResource parentSysResource = getById(sysResource.getPid());
            if (parentSysResource == null) {
                throw new BizException("上级资源不存在");
            }
            result.setParentName(parentSysResource.getName());
        }
        return result;
    }

    /**
     * 获取资源列表
     *
     * @param form
     * @return
     */
    @Override
    public List<ListSysResourceVo> list(ListSysResourceForm form) {
        MyBatisWrapper<SysResource> wrapper = new MyBatisWrapper<>();
        Criteria<SysResource> criteria = wrapper.select(Id, Name, Path, Pid)
                .whereBuilder()
                .andEq(DelFlag, false);
        if (Strings.isNotBlank(form.getName())) {
            criteria.andLike(Name, "%" + form.getName() + "%");
        }
        if (Strings.isNotBlank(form.getPath())) {
            criteria.andLike(Path, "%" + form.getPath() + "%");
        }
        List<SysResource> sysResources = mapper.list(wrapper);
        List<ListSysResourceVo> listSysResourceVos = objectConvertor.toListSysResourceVo(sysResources);
        List<ListSysResourceVo> parentList = listSysResourceVos.stream()
                .filter(p -> p.getPid() == 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(parentList)) {
            return listSysResourceVos;
        }
        for (ListSysResourceVo parent : parentList) {
            List<ListSysResourceVo> childList = listSysResourceVos.stream()
                    .filter(p -> Integer.compare(parent.getId(), p.getPid()) == 0)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(childList)) {
                parent.setChildren(childList);
            }
        }

        return parentList;
    }


    /**
     * 根据id列表查询数量
     *
     * @param ids
     * @return
     */
    @Override
    public int countByIds(List<Integer> ids) {
        MyBatisWrapper<SysResource> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andIn(Id, ids)
                .andEq(DelFlag, false);
        return mapper.count(wrapper);
    }

    /**
     * 根据id查询资源列表
     *
     * @param ids
     * @return
     */
    @Override
    public List<SysResource> listByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        MyBatisWrapper<SysResource> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Path)
                .whereBuilder()
                .andIn(Id, ids)
                .andEq(DelFlag, false);
        return mapper.list(wrapper);
    }

    /**
     * 根据id获取资源
     *
     * @param id
     * @return
     */
    private SysResource getById(int id) {
        MyBatisWrapper<SysResource> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Path, NodePath)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false);
        return mapper.get(wrapper);
    }

    /**
     * 根据id获取资源
     *
     * @param path
     * @return
     */
    private SysResource getByPath(String path) {
        MyBatisWrapper<SysResource> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Path, NodePath)
                .whereBuilder()
                .andEq(Path, path);
        return mapper.get(wrapper);
    }

    /**
     * 根据pid统计子节点数量
     *
     * @param pid
     * @return
     */
    private int countByPid(int pid) {
        MyBatisWrapper<SysResource> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andEq(Pid, pid)
                .andEq(DelFlag, false);
        return mapper.count(wrapper);
    }

    /**
     * 检查path是否合法
     *
     * @param parentPath
     * @param childPath
     * @return
     */
    private boolean checkPath(String parentPath, String childPath) {
        if (parentPath == null) {
            throw new BizException("资源路径格式不正确");
        }
        // 检查路径格式
        String parentRegex = "^/[a-zA-Z0-9_*-]+(?:/[a-zA-Z0-9_*-]+)*/\\*+$";
        if (!parentPath.matches(parentRegex)) {
            throw new BizException("资源路径格式不正确");
        }
        if (childPath == null) {
            return true;
        }
        // 获取/*之前的内容
        String prefix = parentPath.substring(0, parentPath.lastIndexOf("/") + 1);
        if (!childPath.startsWith(prefix)) {
            throw new BizException("资源路径格式不正确");
        }
        String childRegex = "^/[a-zA-Z0-9_*-]+(?:/[a-zA-Z0-9_*-]+)*$";

        if (!childPath.matches(childRegex)) {
            throw new BizException("资源路径格式不正确");
        }
        return true;
    }
}
