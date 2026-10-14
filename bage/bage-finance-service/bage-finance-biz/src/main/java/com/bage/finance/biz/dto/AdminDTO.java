package com.bage.finance.biz.dto;

import com.bage.common.dto.BaseUserInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class AdminDTO extends BaseUserInfoDTO {
    private List<Integer> permissions;
}
