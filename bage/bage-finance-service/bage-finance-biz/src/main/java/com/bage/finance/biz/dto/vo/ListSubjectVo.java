package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 查询科目列表
 */
@Data
public class ListSubjectVo {
    /**
     * id
     */
    private Long id;

    /**
     * 父类id
     */
    private Integer pid;

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
     * 余额方向
     */
    private String balanceDirectionText;

    /**
     * 是否禁用，true禁用，false启用
     */
    private Boolean disable;

    /**
     * 1：资产，2：负债，3：权益，4：成本，5：损益
     */
    private String subjectCateText;

    /**
     * 计量单位
     */
    private String unitOfMeasurement;

    /**
     * 辅助核算列表
     */
    private String assistCalculateText;

    /**
     * 外币核算列表
     */
    private String currencyConfigText;

    /**
     * 是否启用期末调汇
     */
    private Boolean endOfYearCurrencyRevaluationFlag;

    /**
     * 科目层级
     */
    private Integer level;

    /**
     * 科目类型[0：系统科目，不能删除，1：自定义科目，可自行删除]
     */
    private Byte subjectType;

    /**
     * 节点深度
     */
    private Integer nodeDepth;

    /**
     * 使用计数
     */
    private Integer useCount;

    /**
     * 上级科目是否禁用
     */
    private Boolean parentSubjectDisable;
}