package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/*
* 对账信息表
*/
@Data
@Entity
public class AccountCheck {
    @Id
    @GeneratedValue
    private String id;

    /**
     * 交易编号
     */
    private String tradeNo;

    /**
     * 平台状态
     */
    private String platformStatus;

    /**
     * 平台金额
     */
    private String platformAmount;

    /**
     * 平台交易时间
     */
    private Date platformTime;

    /**
     * EBAY状态
     */
    private String ebayStatus;

    /**
     * EBAY金额
     */
    private String ebayAmount;

    /**
     * EBAY交易时间
     */
    private Date ebayTime;

    /**
     * 对账状态
     */
    private String checkStatus;

    /**
     * 处理过程
     */
    private String handleMemo;

    /**
     * 组织
     */
    private Long organizationId;

    /**
     * 是否有效(Y 有效  N 无效)
     */
    private String isActive = "Y";

    /**
     * 是否删除(Y 删除 N 未删除)
     */
    private String isDelete = "N";

    /**
     * 创建时间
     */
    private Date created = new Date();

    /**
     * 修改时间
     */
    private Date updated = new Date();

    /**
     * 创建者
     */
    private String createdby;

    /**
     * 修改者
     */
    private String updatedby;

}