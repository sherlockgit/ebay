package com.winit.enums;

import lombok.Getter;

/**
 * Created by liyou
 * 2017-06-11 17:16
 * 支付状态, 默认未支付
 */
@Getter
public enum LogisticsStatusEnum implements CodeEnum {

    WAIT(0, "未支付"),
    SUCCESS(1, "支付成功");

    private Integer code;

    private String message;

    LogisticsStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
