package com.bage.common.sofabolt;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SofaboltMsgResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 5264923797659012348L;

    private String msg;

    public SofaboltMsgResponse(String msg) {
        this.msg = msg;
    }
}
