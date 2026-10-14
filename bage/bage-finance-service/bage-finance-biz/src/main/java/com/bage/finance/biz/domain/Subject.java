package com.bage.finance.biz.domain;

import java.util.Date;

/**
 * 科目（表：subject）
 *
 * @author bage
 */
public class Subject {
    /**
     * 
     */
    private Long id;

    /**
     * 父类id
     */
    private Long pid;

    /**
     * 科目编码
     */
    private String code;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 科目名称
     */
    private String name;

    /**
     * 助记码
     */
    private String mnemonicCode;

    /**
     * 余额方向[1：借，2：贷]
     */
    private Integer balanceDirection;

    /**
     * 科目层级
     */
    private Integer level;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 是否禁用
     */
    private Boolean disable;

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
     * 科目类别：1：资产，2：负债，3：权益，4：成本，5：损益
     */
    private Integer subjectCate;

    /**
     * 节点深度
     */
    private Integer nodeDepth;

    /**
     * 一级节点编码长度
     */
    private Integer depthCodeLengthOne;

    /**
     * 二级节点编码长度
     */
    private Integer depthCodeLengthTwo;

    /**
     * 三级编码长度
     */
    private Integer depthCodeLengthThree;

    /**
     * 四级编码长度
     */
    private Integer depthCodeLengthFour;

    /**
     * 科目类型[0：系统科目，不能删除，1：自定义科目，可自行删除]
     */
    private Integer subjectType;

    /**
     * 使用计数
     */
    private Integer useCount;

    /**
     * 上级科目禁用启用状态
     */
    private Boolean parentSubjectDisable;

    /**
     * 父节点全路径，比如/1/2/3
     */
    private String pidPath;

    /**
     * 核算配置[数量核算，辅助核算，外币核算]
     */
    private String calculateConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMnemonicCode() {
        return mnemonicCode;
    }

    public void setMnemonicCode(String mnemonicCode) {
        this.mnemonicCode = mnemonicCode;
    }

    public Integer getBalanceDirection() {
        return balanceDirection;
    }

    public void setBalanceDirection(Integer balanceDirection) {
        this.balanceDirection = balanceDirection;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
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

    public Integer getSubjectCate() {
        return subjectCate;
    }

    public void setSubjectCate(Integer subjectCate) {
        this.subjectCate = subjectCate;
    }

    public Integer getNodeDepth() {
        return nodeDepth;
    }

    public void setNodeDepth(Integer nodeDepth) {
        this.nodeDepth = nodeDepth;
    }

    public Integer getDepthCodeLengthOne() {
        return depthCodeLengthOne;
    }

    public void setDepthCodeLengthOne(Integer depthCodeLengthOne) {
        this.depthCodeLengthOne = depthCodeLengthOne;
    }

    public Integer getDepthCodeLengthTwo() {
        return depthCodeLengthTwo;
    }

    public void setDepthCodeLengthTwo(Integer depthCodeLengthTwo) {
        this.depthCodeLengthTwo = depthCodeLengthTwo;
    }

    public Integer getDepthCodeLengthThree() {
        return depthCodeLengthThree;
    }

    public void setDepthCodeLengthThree(Integer depthCodeLengthThree) {
        this.depthCodeLengthThree = depthCodeLengthThree;
    }

    public Integer getDepthCodeLengthFour() {
        return depthCodeLengthFour;
    }

    public void setDepthCodeLengthFour(Integer depthCodeLengthFour) {
        this.depthCodeLengthFour = depthCodeLengthFour;
    }

    public Integer getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(Integer subjectType) {
        this.subjectType = subjectType;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Boolean getParentSubjectDisable() {
        return parentSubjectDisable;
    }

    public void setParentSubjectDisable(Boolean parentSubjectDisable) {
        this.parentSubjectDisable = parentSubjectDisable;
    }

    public String getPidPath() {
        return pidPath;
    }

    public void setPidPath(String pidPath) {
        this.pidPath = pidPath;
    }

    public String getCalculateConfig() {
        return calculateConfig;
    }

    public void setCalculateConfig(String calculateConfig) {
        this.calculateConfig = calculateConfig;
    }

    public void initDefault() {
        if (this.getPid() == null) {
            this.setPid(0L);
        }
        if (this.getCode() == null) {
            this.setCode("");
        }
        if (this.getSort() == null) {
            this.setSort(0);
        }
        if (this.getName() == null) {
            this.setName("");
        }
        if (this.getMnemonicCode() == null) {
            this.setMnemonicCode("");
        }
        if (this.getBalanceDirection() == null) {
            this.setBalanceDirection(0);
        }
        if (this.getLevel() == null) {
            this.setLevel(0);
        }
        if (this.getTenantId() == null) {
            this.setTenantId(0L);
        }
        if (this.getDisable() == null) {
            this.setDisable(false);
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
        if (this.getSubjectCate() == null) {
            this.setSubjectCate(0);
        }
        if (this.getNodeDepth() == null) {
            this.setNodeDepth(0);
        }
        if (this.getDepthCodeLengthOne() == null) {
            this.setDepthCodeLengthOne(0);
        }
        if (this.getDepthCodeLengthTwo() == null) {
            this.setDepthCodeLengthTwo(0);
        }
        if (this.getDepthCodeLengthThree() == null) {
            this.setDepthCodeLengthThree(0);
        }
        if (this.getDepthCodeLengthFour() == null) {
            this.setDepthCodeLengthFour(0);
        }
        if (this.getSubjectType() == null) {
            this.setSubjectType(0);
        }
        if (this.getUseCount() == null) {
            this.setUseCount(0);
        }
        if (this.getParentSubjectDisable() == null) {
            this.setParentSubjectDisable(false);
        }
        if (this.getPidPath() == null) {
            this.setPidPath("");
        }
    }

    public void initUpdate() {
    }
}