package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.Folder;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.CreateFolderForm;
import com.bage.finance.biz.dto.form.DelForm;
import com.bage.finance.biz.dto.form.ListFolderForm;
import com.bage.finance.biz.dto.form.UpdateFolderForm;
import com.bage.finance.biz.dto.vo.GetFolderVo;
import com.bage.finance.biz.dto.vo.ListFolderVo;
import com.bage.finance.biz.mapper.FolderMapper;
import com.bage.finance.biz.service.FileService;
import com.bage.finance.biz.service.FolderService;
import com.bage.mybatis.help.MyBatisWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.FolderField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    final FolderMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final ObjectMapper objectMapper;
    final ApplicationContext applicationContext;
    final FileService fileService;

    /**
     * 创建文件夹
     *
     * @param form
     * @return
     */
    @Override
    public long create(CreateFolderForm form) {
        if (form.getPid() > 0 && !isExists(form.getPid())) {
            throw new BizException("上级目录不存在");
        }
        Folder folder = objectConvertor.toFolder(form);
        folder.initDefault();
        folder.setMemberId(tokenService.getThreadLocalUserId());
        folder.setTenantId(tokenService.getThreadLocalTenantId());
        mapper.insert(folder);
        return folder.getId();
    }

    /**
     * 修改文件夹
     *
     * @param form
     * @return
     */
    @Override
    public boolean update(UpdateFolderForm form) {
        MyBatisWrapper<Folder> wrapper = new MyBatisWrapper<>();
        wrapper.update(Name, form.getName())
                .update(Sort, form.getSort())
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);

        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 修改文件夹
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelForm form) {
        if (getChildCount(form.getId()) > 0) {
            throw new BizException("必须将当前目录的所有子目录删除才能删除该目录");
        }
        if (fileService.countByFolderId(form.getId()) > 0) {
            throw new BizException("该目录下还有文件不能删除");
        }
        MyBatisWrapper<Folder> wrapper = new MyBatisWrapper<>();
        wrapper.update(DelFlag, true)
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);

        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 查询文件夹列表
     *
     * @param form
     * @return
     */
    @Override
    public List<ListFolderVo> list(ListFolderForm form) {
        MyBatisWrapper<Folder> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Sort)
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andEq(Pid, form.getPid());
        wrapper.orderByAsc(Sort);
        List<Folder> list = mapper.list(wrapper);
        List<ListFolderVo> result = objectConvertor.toListFolderVo(list);

        //获取子节点的数量
        if (!CollectionUtils.isEmpty(list)) {
            List<Folder> childList = list(result.stream().map(ListFolderVo::getId).collect(Collectors.toSet()));
            result.forEach(p -> {
                p.setChildCount(childList.stream().filter(c -> Objects.equals(p.getId(), c.getPid())).count());
            });
        }
        return result;
    }

    /**
     * 查询文件夹
     *
     * @param id
     * @return
     */
    @Override
    public GetFolderVo get(long id) {
        MyBatisWrapper<Folder> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Sort)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);
        Folder folder = mapper.get(wrapper);
        if (folder == null) {
            throw new BizException("文件夹不存在");
        }
        GetFolderVo result = objectConvertor.toGetFolderVo(folder);
        result.setParentName("根目录");
        if (folder.getPid() > 0) {
            wrapper.clear();
            wrapper.select(Id, Pid, Name, Sort)
                    .whereBuilder()
                    .andEq(Id, folder.getPid())
                    .andEq(TenantId, tokenService.getThreadLocalTenantId())
                    .andEq(DelFlag, false);
            folder = mapper.get(wrapper);
            if (folder == null) {
                throw new BizException("上级目录不存在");
            }
            result.setParentName(folder.getName());
        }
        return result;
    }

    private List<Folder> list(Set<Long> pids) {
        MyBatisWrapper<Folder> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Name, Sort)
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andIn(Pid, pids);
        wrapper.orderByAsc(Sort);
        return mapper.list(wrapper);
    }

    private boolean isExists(long id) {
        MyBatisWrapper<Folder> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andEq(Id, id)
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);
        return mapper.count(wrapper) > 0;
    }

    /**
     * 获取子节点数量
     *
     * @param pid
     * @return
     */
    private int getChildCount(long pid) {
        MyBatisWrapper<Folder> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andEq(Pid, pid)
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);
        return mapper.count(wrapper);
    }

}
