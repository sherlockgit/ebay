package com.winit.enums;

import lombok.Getter;

/**
 * Created by liyou
 * 2017-06-11 17:12
 * 1：文本，2：图片，3：语音，4：视频，5：音乐，6：图文'
 */
@Getter
public enum MessageTypeEnum implements CodeEnum {
	TEXT(1, "文本"),
    PICTURE(2, "图片"),
    AUDIO(3, "语音"),
    VEDIO(4, "视频"),
    MUSIC(5, "音乐"),
    NEWS(6, "图文");

    private Integer code;

    private String message;

    MessageTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
