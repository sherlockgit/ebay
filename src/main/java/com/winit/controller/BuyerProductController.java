package com.winit.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.winit.VO.ProductDetailVO;
import com.winit.VO.ResultVO;
import com.winit.dataobject.ProductAttr;
import com.winit.dataobject.ProductInfo;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.service.ProductAttrService;
import com.winit.service.ProductService;
import com.winit.utils.ResultVOUtil;

import io.swagger.annotations.ApiParam;
/**
 * 买家商品
 * Created by liyou
 * 2017-05-12 14:08
 */
@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {

    @Autowired
    private ProductService productService;

    
    @Autowired
    private ProductAttrService productAttrService;

    @GetMapping("/list")
    public ResultVO list(
    		 @ApiParam(value = "微信号(OPENID)") @RequestParam(required = false, value = "userWxOpenid", defaultValue = "") String userWxOpenid,
    		 @ApiParam(value = "商品编号") @RequestParam(required = false, value = "productNo", defaultValue = "") String productNo,
			 @ApiParam(value = "商品名称") @RequestParam(required = false, value = "productName", defaultValue = "") String productName,
			 @ApiParam(value = "商品产地") @RequestParam(required = false, value = "productCountry", defaultValue = "") String productCountry,
			 @ApiParam(value = "品牌") @RequestParam(required = false, value = "productBrand", defaultValue = "") String productBrand,
			 @ApiParam(value = "商品特色") @RequestParam(required = false, value = "productFeatures", defaultValue = "") String productFeatures,
			 @ApiParam(value = "商品分类") @RequestParam(required = false, value = "productType", defaultValue = "") String productType,
			 @ApiParam(value = "商品状态:正常/下架") @RequestParam(required = false, value = "productStatus", defaultValue = "正常") String productStatus,
			 @ApiParam(value = "类目编号") @RequestParam(required = false, value = "categoryType", defaultValue = "") Integer categoryType,
			 @ApiParam(value = "审核状态") @RequestParam(required = false, value = "auditStatus", defaultValue = "0") String auditStatus,
			 @ApiParam(value = "EBAY ITEMID") @RequestParam(required = false, value = "ebayItemid", defaultValue = "") String ebayItemid,@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size, HttpServletResponse response) {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*");
    	
		PageRequest request = new PageRequest(page, size);
		ProductInfo productInfo = new ProductInfo();
		
		productInfo.setUserWxOpenid(userWxOpenid);
		productInfo.setProductNo(productNo);
		productInfo.setProductNane(productName);
		productInfo.setProductCountry(productCountry);
		productInfo.setProductBrand(productBrand);
		productInfo.setProductFeatures(productFeatures);
		productInfo.setProductType(productType);
		productInfo.setProductStatus(productStatus);
		productInfo.setCategoryType(categoryType);
		productInfo.setAuditStatus(auditStatus);
		productInfo.setEbayItemid(ebayItemid);

		// 1. 查询所有的上架、审核通过商品
		Page<ProductInfo> productInfoPage = productService.findAllByProductInfo(productInfo, request);

		//摘出运费和税费
//		productInfoPage.getContent().stream().forEach(productInfos -> {
//			BigDecimal syfee = productInfos.getCarriageFee().add(productInfos.getTaxFee());
//			BigDecimal productPirce = productInfos.getProductPrice();
//			productPirce = productPirce.subtract(syfee);
//			productInfos.setProductPrice(productPirce);
//		});

        return ResultVOUtil.success(productInfoPage);
    }
    //商品详情
    @GetMapping("/detail/{productId}")
	public ResultVO<ProductDetailVO> detail(@ApiParam(value = "eaby商品id") @RequestParam(required = false, value = "itemId", defaultValue = "") String itemId,
			@PathVariable("productId") Long productId,
			HttpServletResponse response) {
    	
		response.setHeader("Access-Control-Allow-Origin", "*");
		ProductInfo productInfo = productService.findOne(productId);
		//摘出运费和税费
//		BigDecimal syfee = productInfo.getCarriageFee().add(productInfo.getTaxFee());
//		BigDecimal productPirce = productInfo.getProductPrice();
//		productInfo.setProductPrice(productPirce.subtract(syfee));

		ProductDetailVO productDetailVO = new ProductDetailVO();
		//审核状态[0- 待审核 1- 通过  2-不通过]
		if (productInfo == null) {
			throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
		}else {

			BeanUtils.copyProperties(productInfo, productDetailVO);
		}
		String pic = productInfo.getProductPic();
		List<String> pics =Lists.newArrayList();
		if(pic !=null && !"".equals(pic)){
			pics =Arrays.asList(pic.split("@"));  
			productDetailVO.setPics(pics);
		}
		
		List<ProductAttr> productAttrList = productAttrService.findByProductId(productId);
		
		//商品组id，如果有值则为多属性商品，商品价格及图片需从字表获取
		if (!StringUtils.isEmpty(itemId)) {
			ProductAttr productAttr = new ProductAttr();
			productAttr.setItemId(itemId);
			productAttrList = productAttrService.findAll(productAttr);
			if(!CollectionUtils.isEmpty(productAttrList)){
				for(ProductAttr tempVO : productAttrList){
					if("price".equalsIgnoreCase(tempVO.getAttrEname())){
						if(!StringUtils.isEmpty(tempVO.getAttrCvalue())){
							
							productDetailVO.setProductPrice(new BigDecimal(tempVO.getAttrCvalue()));
						}
					}
					if("itemGroupAdditionalImages".equalsIgnoreCase(tempVO.getAttrEname())){
						if(!StringUtils.isEmpty(tempVO.getAttrCvalue())){
							
							pics =Arrays.asList(tempVO.getAttrEvalue().split("@"));  
							productDetailVO.setPics(pics);
						}
					}
//					if ("carriageFee".equalsIgnoreCase(tempVO.getAttrEname())) {
//						if (!StringUtils.isEmpty(tempVO.getAttrCvalue())) {
//
//							productInfo.setCarriageFee(new BigDecimal(tempVO.getAttrCvalue()));
//						}
//					}
//					if ("taxFee".equalsIgnoreCase(tempVO.getAttrEname())) {
//						if (!StringUtils.isEmpty(tempVO.getAttrCvalue())) {
//
//							productInfo.setTaxFee(new BigDecimal(tempVO.getAttrCvalue()));
//						}
//					}
				}
			}
		}
				
		
		productDetailVO.setProductAttrList(productAttrList);
		return ResultVOUtil.success(productDetailVO);
	}
}
