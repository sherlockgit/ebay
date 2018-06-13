package com.winit.dataobject;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Component;

import lombok.Data;

/*
* 商品信息表
 */

@Data
@Entity
@Component
@DynamicUpdate
public class ProductInfo {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 商品编号
     */
    private String productNo;

    /**
     * 商品名称
     */
    private String productNane;

    /**
     * 商品产地
     */
    private String productCountry;

    /**
     * 商品美元价格
     */
    private BigDecimal productUsd;

    /**
     * 品牌
     */
    private String productBrand;

    /**
     * 商品特色
     */
    private String productFeatures;

    /**
     * 单价
     */
    private BigDecimal productPrice;
    
    /**
     * 库存
     */
    private Integer productStock = 0;

    /**
     * 修商品分类
     */
    private String productType;

    /**
     * 商品图片
     */
    private String productPic;

    /**
     * 商品小图
     */
    private String productIcon;
    
    /**
     * 商品描述
     */
    private String productMemo;

    /**
     * 商品状态,0正常1下架
     */
    private String productStatus;

    /**
     * 类目编号
     */
    private Integer categoryType;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 译者微信OPEN_ID
     */
    private String userWxOpenid;

    /**
     * EBAY ITEMID
     */
    private String ebayItemid;

    /**
     * 组织
     */
    private Long organizationId;
    
    /**
     * 运费
     */
    private BigDecimal carriageFee;
    
    /**
     * 税费
     */
    private BigDecimal taxFee;
    /**
     * 商品组ID
     */
    private String groupId;

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

    /**
     * 审核时间
     */
    private Date audited;

    /**
     * 审核人
     */
    private String auditedby;

    /**
     * 累计销售个数,默认值
     */
    private String productSales;

    /**
     * 汇率
     */
    private BigDecimal usdRate;

    /**
     * 加价
     */
    private BigDecimal addPrice;


}