package com.winit.dataobject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import lombok.Data;

/*
*   购物车对象
*/
@Data
public class GoodCarVO {
    @Id
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
    
    /** 运费. */
    private BigDecimal carriage;
    
    
    /** 税费  */
    private BigDecimal taxFee;
    /**
     * ebay商品ID
     */
    private String itemId ;

    /**
     * 动态属性
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