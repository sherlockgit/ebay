package com.winit.enums;

import lombok.Getter;

/**
 * 商品状态
 * Created by liyou
 * 2017-05-09 17:33
 */
@Getter
public enum AuditStatusEnum implements CodeEnum {
    PASS(0, "审核通过"),
    ;

    private Integer code;

    private String message;

    AuditStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


}
