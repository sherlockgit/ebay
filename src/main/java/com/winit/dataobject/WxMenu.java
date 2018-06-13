package com.winit.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/*
* 微信菜单表
 */

@Data
@Entity
@DynamicUpdate
public class WxMenu {
    @Id
    @GeneratedValue
    private Long id;

    /**
     *父菜单
     */
    private Integer wxMenuParent;

    /**
     *菜单级别
     */
    private Integer wxMenuLevel;

    /**
     *菜单名称
     */
    private String wxMenuName;

    /**
     *菜单类型(1:链接 2:消息)
     */
    private String wxMenuType;

    /**
     *菜单顺序
     */
    private Integer wxMenuSeriNo;

    /**
     *菜单内容
     */
    private String wxMenuContent;

    /**
     *有效位(0:无效 1:有效)
     */
    private String wxMenuFlag;

    /**
     *创建人
     */
    private String crtUserId;

    /**
     *创建时间
     */
    private Date crtTime;

    /**
     *修改人
     */
    private String uptUserId;

    /**
     *修改时间
     */
    private Date uptTime = new Date();

    /**
     *同步时间
     */
    private Date syncTime = new Date();

}