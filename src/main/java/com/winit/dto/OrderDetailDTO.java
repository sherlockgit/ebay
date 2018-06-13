package com.winit.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/*
*   订单详情表
*
*/
@Data
public class OrderDetailDTO {
    private Long id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 当前价格,单位分
     */
    private BigDecimal productPrice;

    /**
     * 数量
     */
    private Integer productQuantity;

    /**
     * 小图
     */
    private String productIcon;
    /**
     * ebay商品ID
     */
    private String itemId ;
    /**
     * 运费
     */
    private BigDecimal carriageFee;
    
    /**
     * 税费
     */
    private BigDecimal taxFee;
    /**
     * 订单总金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 商品选择属性
     */
    private String productAttr ;

    /**
     * 创建时间
     */
    private Date createTime = new Date();

    /**
     * 修改时间
     */
    private Date updateTime = new Date();

}