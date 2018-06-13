package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/*
* 分销结算表
*/
@Data
@Entity
public class Distribution {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品总价
     */
    private BigDecimal productTotalPrice;
    
    /**
     *支付订单Ebay商品总价(不含运费)
     */
    private BigDecimal ebayAmount;
    
    /**
     * 订单差价
     */
    private BigDecimal productGapAmount;

    /**
     * 购买用户
     */
    private String buyUserName;

    /**
     * 一级分销姓名
     */
    private String firstDistName;

    /**
     * 一级分销比例
     */
    private String firstDistRatio;

    /**
     * 一级提成金额
     */
    private BigDecimal firstCommission;

    /**
     * 一级微信号
     */
    private String firstWxName;

    /**
     * 二级分销姓名
     */
    private String secondDistName;

    /**
     * 二级分销比例
     */
    private String secondDistRatio;

    /**
     * 二级提成金额
     */
    private BigDecimal secondCommission;

    /**
     * 总提成(元)
     */
    private BigDecimal totalCommission;

    /**
     * 二级微信号
     */
    private String secondWxName;

    /**
     * 审核状态[0-待审核 1-未通过 2-已通过 3-作废]
     */
    private String auditStatus;

    /**
     * 审核意见
     */
    private String auditOpinion;

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