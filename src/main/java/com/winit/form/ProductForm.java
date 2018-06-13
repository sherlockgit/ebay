package com.winit.form;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.winit.VO.ProductAttrVO;

import lombok.Data;

/**
 * Created by liyou 2017-07-23 17:20
 */
@Data
public class ProductForm {
	
	/** 商品id. */
	private String productId;

	/** 商品编号. */
	private String productNo;

	/** 名字. */
	private String productNane;
	
	/** 译者微信OPEN_ID*/
    private String userWxOpenid;


	/** 商品产地*/
	private String productCountry;

	/** 品牌 */
	private String productBrand;
	/** 修商品分类*/
	private String productType;

	/** 单价. */
	private BigDecimal productPrice;

	/** 美元单价. */
	private BigDecimal productUsd;

	/** 库存. */
	private Integer productStock;

	/** 商品特色. */
	private String productFeatures;

	/** 小图. */
	private String productIcon;

	/** 商品图片 */
	private String productPic;
	
	 /**
     * 商品状态,0正常1下架
     */
    private String productStatus;
    /**
     * 审核状态
     */
    private String auditStatus;
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
	 * 汇率
	 */
	private BigDecimal usdRate;

	/**
	 * 加价
	 */
	private BigDecimal addPrice;

    
    /**
     * 审核时间
     */
    private Date audited;
    
    /**
     * 商品组ID
     */
    private String groupId;

    /**
     * 创建者
     */
    private String createdby;

    /**
     * 修改者
     */
    private String updatedby;

	/** 类目编号. */
	private Integer categoryType;
	/**
	 * 商品描述
	 */
	private String productMemo;

	private List<ProductAttrVO> items;

}
