package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class ListSubjectByCateAndCodeAndNameVo {
    /**
     * 科目id
     */
    private Long id;

    /**
     * 科目父id
     */
    private Long pid;

    /**
     * 科目类别
     */
    private String subjectCate;

    /**
     * 科目编码
     */
    private String code;

    /**
     * 科目名称
     */
    private String name;

    /**
     * 科目全名称（含分类）
     */
    private String fullName;
}
