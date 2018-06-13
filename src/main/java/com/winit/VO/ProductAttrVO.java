package com.winit.VO;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/*
* 商品属性扩展表
 */

@Data
public class ProductAttrVO {

	private Long id;
    /**
     * 商品编号
     */
    private String productId;

    /**
     * 属性英文名称
     */
    private String attrEname;

    /**
     * 属性英文值
     */
    private String attrEvalue;

    /**
     * 属性中文名称
     */
    private String attrCname;

    /**
     * 属性中文值
     */
    private String attrCvalue;
    
    /**
     * ebay商品ID
     */
    private String itemId ;

    /**
     * 属性类型[单值 0,  多值 1] -颜色
     */
    private String attrType;

    /**
     * 组织
     */
    private Long organizationId;



}