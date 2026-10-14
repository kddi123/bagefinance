package com.bage.finance.biz.domain;

import java.util.Date;

/**
 * 数据字典（表：data_dictionary）
 *
 * @author bage
 */
public class DataDictionary {
    /**
     * 
     */
    private Long id;

    /**
     * 数据类型分类编码
     */
    private String dataCodeCate;

    /**
     * 数据编码
     */
    private String dataCode;

    /**
     * 数据值
     */
    private String dataValue;

    /**
     * 数据字典顺序
     */
    private Integer dataSort;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataCodeCate() {
        return dataCodeCate;
    }

    public void setDataCodeCate(String dataCodeCate) {
        this.dataCodeCate = dataCodeCate;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public Integer getDataSort() {
        return dataSort;
    }

    public void setDataSort(Integer dataSort) {
        this.dataSort = dataSort;
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

    public void initDefault() {
        if (this.getDataCodeCate() == null) {
            this.setDataCodeCate("");
        }
        if (this.getDataCode() == null) {
            this.setDataCode("");
        }
        if (this.getDataValue() == null) {
            this.setDataValue("");
        }
        if (this.getDataSort() == null) {
            this.setDataSort(0);
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
    }

    public void initUpdate() {
    }
}