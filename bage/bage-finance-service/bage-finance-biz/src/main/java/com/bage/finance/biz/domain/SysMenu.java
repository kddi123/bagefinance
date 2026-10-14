package com.bage.finance.biz.domain;

import java.util.Date;

/**
 * 系统菜单（表：sys_menu）
 *
 * @author bage
 */
public class SysMenu {
    /**
     * 
     */
    private Integer id;

    /**
     * 父级菜单
     */
    private Integer pid;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单路由
     */
    private String path;

    /**
     * 菜单组件
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否使用布局
     */
    private Boolean layout;

    /**
     * 是否隐藏菜单
     */
    private Boolean hideInMenu;

    /**
     * 重定向地址
     */
    private String redirect;

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
     * 节点深度，0表示一级节点
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

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getLayout() {
        return layout;
    }

    public void setLayout(Boolean layout) {
        this.layout = layout;
    }

    public Boolean getHideInMenu() {
        return hideInMenu;
    }

    public void setHideInMenu(Boolean hideInMenu) {
        this.hideInMenu = hideInMenu;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
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
        if (this.getComponent() == null) {
            this.setComponent("");
        }
        if (this.getIcon() == null) {
            this.setIcon("");
        }
        if (this.getLayout() == null) {
            this.setLayout(false);
        }
        if (this.getHideInMenu() == null) {
            this.setHideInMenu(false);
        }
        if (this.getRedirect() == null) {
            this.setRedirect("");
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