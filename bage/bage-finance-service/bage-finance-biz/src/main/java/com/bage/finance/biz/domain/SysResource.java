package com.bage.finance.biz.domain;

import java.util.Date;

/**
 * 系统资源（表：sys_resource）
 *
 * @author bage
 */
public class SysResource {
    /**
     * 
     */
    private Integer id;

    /**
     * 父id
     */
    private Integer pid;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源路径
     */
    private String path;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 修改用户id
     */
    private Long updateMemberId;

    /**
     * 是否删除，0：删除，1：未删除
     */
    private Boolean delFlag;

    /**
     * 是否禁用
     */
    private Boolean disable;

    /**
     * 节点深度
     */
    private Integer nodePath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getUpdateMemberId() {
        return updateMemberId;
    }

    public void setUpdateMemberId(Long updateMemberId) {
        this.updateMemberId = updateMemberId;
    }

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Integer getNodePath() {
        return nodePath;
    }

    public void setNodePath(Integer nodePath) {
        this.nodePath = nodePath;
    }

    public void initDefault() {
        if (this.getPid() == null) {
            this.setPid(0);
        }
        if (this.getName() == null) {
            this.setName("");
        }
        if (this.getPath() == null) {
            this.setPath("");
        }
        if (this.getSort() == null) {
            this.setSort(0);
        }
        if (this.getCreateTime() == null) {
            this.setCreateTime(new Date());
        }
        if (this.getUpdateTime() == null) {
            this.setUpdateTime(new Date());
        }
        if (this.getMemberId() == null) {
            this.setMemberId(0L);
        }
        if (this.getUpdateMemberId() == null) {
            this.setUpdateMemberId(0L);
        }
        if (this.getDelFlag() == null) {
            this.setDelFlag(false);
        }
        if (this.getDisable() == null) {
            this.setDisable(false);
        }
        if (this.getNodePath() == null) {
            this.setNodePath(0);
        }
    }

    public void initUpdate() {
    }
}