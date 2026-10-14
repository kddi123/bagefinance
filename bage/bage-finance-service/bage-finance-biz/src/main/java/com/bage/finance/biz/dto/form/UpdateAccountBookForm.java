package com.bage.finance.biz.dto.form;


import lombok.Data;

 import jakarta.validation.constraints.NotBlank;
 import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 账套（表：account_book）
 */
@Data
public class UpdateAccountBookForm implements Serializable {
    /**
     * id
     */
    @NotNull
    private Long id;

    /**
     * 公司名称
     */
    @NotBlank
    private String companyName;

    /**
     * 行业代码id（取数据字典）
     */
    @NotNull
    private Integer industryId;

    /**
     * 增值税种类[0：小规模纳税人；1：一般纳税人]
     */
    @NotNull
    private Byte valueAddedTaxCate;

    /**
     * 凭证是否审核[0：不审核；1：审核]
     */
    @NotNull
    private Boolean enableVoucherVerify;

    /**
     * 账套启用年月
     */
//    @ApiModelProperty(value = "账套启用年月")
//    @NotNull
//    private Date startTime;

    /**
     * 会计准则[0：小企业会计准则；1：企业会计准则；2：民间非盈利组织会计制度；3：农民专业合作社财务会计制度]
     */
//    @ApiModelProperty(value = "会计准则[0：小企业会计准则；1：企业会计准则；2：民间非盈利组织会计制度；3：农民专业合作社财务会计制度]")
//    @NotNull
//    private Byte accountingStandard;

    /**
     * 是否启用固定资产模块[0：不启用；1：启用]
     */
    @NotNull
    private Boolean enableFixedAssets;

    /**
     * 是否启用资金模块[0：不启用；1：启用]
     */
    @NotNull
    private Boolean enableCapital;

    /**
     * 是否启用进销存[0：不启用；1：启用]
     */
    @NotNull
    private Boolean enablePsi = false;
}