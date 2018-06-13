package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;


/*
* 微信素材表
 */

@Data
@Entity
public class WxSource {
    @Id
    private Long id;

    /**
     *素材类型（1：图片，2：视频，3：语音，4：图文）
     */
    private String type;

    /**
     *名称
     */
    private String sourceName;

    /**
     *素材ID
     */
    private String mediaId;

    /**
     *链接
     */
    private String sourceUrl;

    /**
     *图文素材JSON
     */
    private String content;
}