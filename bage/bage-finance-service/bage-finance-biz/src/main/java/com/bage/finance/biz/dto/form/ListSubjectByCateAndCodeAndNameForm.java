package com.bage.finance.biz.dto.form;


import lombok.Data;

@Data
public class ListSubjectByCateAndCodeAndNameForm {

    /**
     * 科目类别
     */
    private Integer subjectCate;

    /**
     * 科目编码或科目名称
     */
    private String codeAndName;
}
