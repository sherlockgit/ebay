package com.winit.enums;

import lombok.Getter;

/**
 * 商品状态
 * Created by liyou
 * 2017-05-09 17:33
 */
@Getter
public enum UserTypeEnum{

    DISTRIBUTOR("1", "分销商"),
    ORIDINARY("2", "普通用户");
    private String code;

    private String message;

    UserTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
