package com.bage.finance.biz.dto.vo;


import lombok.Data;

/**
 * 科目（表：subject）
 */
@Data
public class GetSubjectVo {
    /**
     * id
     */
    private Long id;

    /**
     * 父类id
     */
    private Long pid;

    /**
     * 父类名称
     */
    private String parentName;

    /**
     * 顺序，序号
     */
    private Integer sort;

    /**
     * 科目编码
     */
    private String code;

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
     * 是否禁用，true禁用，false启用
     */
    private Boolean disable;

    /**
     * 1：资产，2：负债，3：权益，4：成本，5：损益
     */
    private Integer subjectCate;

    /**
     * 科目类别名称
     */
    private String subjectCateName;

    /**
     * 科目核算配置
     */
    private SubjectCalculateConfigVo subjectCalculateConfigVo;

    /**
     * 科目类型[0：系统科目，不能删除，1：自定义科目，可自行删除]
     */
    private Integer subjectType;

    /**
     * 被使用的次数
     */
    private Integer useCount;
}