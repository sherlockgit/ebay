package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/*
* 分销轨迹表
*/
@Data
@Entity
public class DistributionRc {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 订单编号
     */
    private String productNo;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 分销者USERID
     */
    private String fromUserId;

    /**
     * 分销者姓名
     */
    private String fromUserName;

    /**
     * 被分销者USERID
     */
    private String userId;

    /**
     * 被分销者姓名
     */
    private String userName;

    /**
     * 被分销者微信ID
     */
    private BigDecimal userWxOpenid;

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
     * 修改者(经办人)
     */
    private String updatedby;

}