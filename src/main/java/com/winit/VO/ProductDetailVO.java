package com.winit.VO;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.winit.dataobject.ProductAttr;

import lombok.Data;

/**
 * 商品详情包含商品属性
 * Created by liyou
 * 2017-05-12 14:20
 */
@Data
public class ProductDetailVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("productNo")
    private String productNo;

	@JsonProperty("name")
    private String productNane;

	@JsonProperty("price")
    private BigDecimal productPrice;
	
	@JsonProperty("type")
    private String productType;

	@JsonProperty("pic")
    private List<String> pics;
	
	@JsonProperty("icon")
    private String productIcon;
	
	@JsonProperty("productCountry")
    private String productCountry;

    @JsonProperty("productAttr")
    private List<ProductAttr> productAttrList;

    @JsonProperty("productMemo")
    private String productMemo;
    
    @JsonProperty("carriageFee")
    private BigDecimal carriageFee;
    
    @JsonProperty("taxFee")
    private BigDecimal taxFee;
    
    @JsonProperty("auditStatus")
    private String auditStatus;
    
    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("productSales")
    private String productSales;

    @JsonProperty("usdRate")
    private BigDecimal usdRate;

    @JsonProperty("addPrice")
    private BigDecimal addPrice;


}
