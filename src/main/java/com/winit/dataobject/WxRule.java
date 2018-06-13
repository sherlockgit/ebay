package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/*
* 微信回复表
 */

@Data
@Entity
public class WxRule {
    @Id
    private Long id;

    /**
     *规则名称
     */
    private String ruleName;

    /**
     *规则类型（1.关键字回复；2.关注回复；3.消息回复）
     */
    private String ruleType;

    /**
     *关键字，不同关键字用中文“，”分割
     */
    private String keyWords;

    /**
     *回复消息类型，1：文本，2：图片，3：语音，4：视频，5：音乐，6：图文
     */
    private String replyType;

    /**
     *YBL_WX_SOURCE表MEDIA_ID
     */
    private String sourceId;

    /**
     *匹配类型，1：完全匹配，2：模糊匹配
     */
    private String comType;

    /**
     *是否有效，1：有效，2：无效
     */
    private String status;

    /**
     *排序，按照数字从小到大来
     */
    private String sort;

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