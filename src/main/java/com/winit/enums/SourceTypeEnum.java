package com.winit.enums;

import lombok.Getter;

/**
 * Created by liyou
 * 2017-06-11 17:12
 * 素材类型（1：图片，2：视频，3：语音，4：图文）
 */
@Getter
public enum SourceTypeEnum implements CodeEnum {
	PICTURE(1, "图片"),
    VEDIO(2, "视频"),
    AUDIO(3, "语音"),
    NEWS(4, "图文");

    private Integer code;

    private String message;

    SourceTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
