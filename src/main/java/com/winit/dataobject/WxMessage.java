package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/*
* 微信消息回复表
 */

@Data
@Entity
public class WxMessage {
    @Id
    @GeneratedValue
    private Long id;

    /**
     *消息名称
     */
    private String messageName;

    /**
     *规则类型（1.关注回复；2.消息回复）
     */
    private String messageType;

    /**
     *关键字，不同关键字用中文“，”分割
     */
    private String keyWords;

    /**
     *回复消息类型，1：文本，2：图片，3：语音，4：视频，5：音乐，6：图文
     */
    private String replyType;

    /**
     *图片url
     */
    private String pictureUrl;

    /**
     *图文url
     */
    private String newsUrl;

    /**
     * 消息内容
     */
    private String content;

    /**
     *是否有效，1：有效，2：无效
     */
    private String status;


    /**
     *备注
     */
    private String memo;

    /**
     *新增时间
     */
    private Date crtTime = new Date();

    /**
     *修改时间
     */
    private Date uptTime = new Date();

    /**
     *创建人ID
     */
    private String crtId;

    /**
     *修改人ID
     */
    private String uptId;

}