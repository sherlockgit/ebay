package com.winit.enums;

import lombok.Getter;

/**
 * 商品状态
 * Created by liyou
 * 2017-05-09 17:33
 */
@Getter
public enum ItemTypeEnum implements CodeEnum {
    SINGLE_ITEM(2, "单个商品"),
    GROUP_ITEM(1, "组合商品");

    private Integer code;

    private String message;

    ItemTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


}
