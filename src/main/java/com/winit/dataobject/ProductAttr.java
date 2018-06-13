package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/*
* 商品属性扩展表
 */

@Data
@Entity
public class ProductAttr {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 商品编号
     */
    private Long productId;

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

    /**
     * 是否有效(Y 有效  N 无效)
     */
    private String isActive = "Y";

    /**
     * 是否删除(Y 删除 N 未删除)
     */
    private String isDelete ="N";

    /**
     * 创建时间
     */
    private Date created = new Date();

    /**
     * 修改时间（翻译时间)
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