package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.Valid;
 import jakarta.validation.constraints.NotBlank;
 import jakarta.validation.constraints.NotNull;
 import jakarta.validation.constraints.Pattern;
 import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 辅助核算类别
 *
 */
@Data
public class CreateAssistCalculateCateForm {
    /**
     *辅助核算类别名称
     */
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    /**
     *自定义列配置
     */
    @Valid
    @NotNull
    private List<CustomerColumnConfig> customerColumnConfigList;

    @Data
    public static class CustomerColumnConfig {
        /**
         *字段名称，c1,c2,c3,c4,c5,c6,c7,c8,c9,c10
         */
        @Pattern(regexp = "^(c[1-9]|c10|code|name|notes|mnemonicCode)$")
        @NotBlank
        private String columnName;

        /**
         * 字段别名
         */
        @NotBlank
        @Size(max = 50)
        private String columnAlias;
    }
}