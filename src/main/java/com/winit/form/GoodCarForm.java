package com.winit.form;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;



/**
 * Created by liyou
 * 2017-06-18 23:31
 */
@Data
public class GoodCarForm{

    /** 商品ID*/
    private Long productId;

    /**商品名称*/
    private String productName;

    /** 当前价格,单位分*/
    private BigDecimal productPrice;

    /**数量*/
    private Integer productQuantity;

    /**小图*/
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
    

}
