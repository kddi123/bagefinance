package com.bage.finance.biz.dto.form.msg;

import com.bage.common.dto.TokenResponse;
import com.bage.common.sofabolt.SofaboltMsgRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScanSucMsgForm extends SofaboltMsgRequest {

    @Serial
    private static final long serialVersionUID = 6931433821922349158L;

    private TokenResponse msg;
}
