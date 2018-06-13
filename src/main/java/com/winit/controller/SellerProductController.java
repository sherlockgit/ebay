package com.winit.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.winit.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.winit.VO.ResultVO;
import com.winit.converter.ProductAttrVO2ProductAttr;
import com.winit.dataobject.ProductAttr;
import com.winit.dataobject.ProductInfo;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.form.ProductForm;
import com.winit.service.ProductAttrService;
import com.winit.service.ProductService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 卖家端商品
 * Created by liyou
 * 2017-07-23 15:12
 */
@RestController
@RequestMapping("/seller/product")
@Slf4j
@SuppressWarnings("unchecked")
public class SellerProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductAttrService productAttrService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 列表
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "商品列表")
    @GetMapping("/list")
    public ResultVO<List<ProductInfo>>  list(
    		 @ApiParam(value = "微信号(OPENID)") @RequestParam(required = false, value = "userWxOpenid", defaultValue = "") String userWxOpenid,
    		 @ApiParam(value = "商品编号") @RequestParam(required = false, value = "productNo", defaultValue = "") String productNo,
			 @ApiParam(value = "商品名称") @RequestParam(required = false, value = "productNane", defaultValue = "") String productName,
			 @ApiParam(value = "商品产地") @RequestParam(required = false, value = "productCountry", defaultValue = "") String productCountry,
			 @ApiParam(value = "品牌") @RequestParam(required = false, value = "productBrand", defaultValue = "") String productBrand,
			 @ApiParam(value = "商品特色") @RequestParam(required = false, value = "productFeatures", defaultValue = "") String productFeatures,
			 @ApiParam(value = "商品分类") @RequestParam(required = false, value = "productType", defaultValue = "") String productType,
			 @ApiParam(value = "商品状态:正常/下架") @RequestParam(required = false, value = "productStatus", defaultValue = "") String productStatus,
			 @ApiParam(value = "类目编号") @RequestParam(required = false, value = "categoryType", defaultValue = "") Integer categoryType,
			 @ApiParam(value = "审核状态") @RequestParam(required = false, value = "auditStatus", defaultValue = "") String auditStatus,
			 @ApiParam(value = "EBAY ITEMID") @RequestParam(required = false, value = "ebayItemid", defaultValue = "") String ebayItemid,
             @RequestParam(value = "page", defaultValue = "0") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size,HttpServletResponse response) {

    	response.setHeader("Access-Control-Allow-Origin","*");
        PageRequest request = new PageRequest(page, size);
        ProductInfo productInfo= new ProductInfo();
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
        Page<ProductInfo> productInfoPage = productService.findAllByProductInfo(productInfo, request);
        return ResultVOUtil.success(productInfoPage);

    }

    /**
     * 商品上架
     * @param productId
     * @return
     */
    @ApiOperation(value = "商品上架")
    @PutMapping("/on_sale/{productId}")
    public ResultVO onSale(@PathVariable("productId") String productId,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        try {
            productService.onSale(Long.parseLong(productId));
        } catch (SellException e) {
        	log.error("【商品上架】openid为出现异常，原因 :{}" +e.getMessage());
            throw new SellException(ResultEnum.SELLER_ONSALE_EXCEPTION);
        }

        return ResultVOUtil.success();
    }
    
    /**
     * 商品下架
     * @param productId
     * @return
     */
    @ApiOperation(value = "商品下架")
    @PutMapping("/off_sale/{productId}")
    public ResultVO offSale(@PathVariable("productId") String productId,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        try {
            productService.offSale(Long.parseLong(productId));
        } catch (SellException e) {
        	log.error("【商品上架】openid为出现异常，原因 :{}" +e.getMessage());
            throw new SellException(ResultEnum.SELLER_OFFSALE_EXCEPTION);
        }

        return ResultVOUtil.success();
    }


    /**
     * 微信端发布新的商品
     * @param form
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "商品编辑")
    @PutMapping("/save")
    public ResultVO save(@RequestBody ProductForm form,BindingResult bindingResult,HttpServletResponse response) {
    	
    	response.setHeader("Access-Control-Allow-Origin","*");
    	
        if (bindingResult.hasErrors()) {
        	return ResultVOUtil.error(0, "商品参数不对");
        }

        ProductInfo productInfo = new ProductInfo();
        try {
            //如果productId为空, 说明是新增
            if (!StringUtils.isEmpty(form.getProductId())) {
                productInfo = productService.findOne(Long.parseLong(form.getProductId()));
            } 
            if(null != productInfo){
                String type = productInfo.getProductType();
                String weixinopenId = productInfo.getUserWxOpenid();
            	BeanUtilEx.copyPropertiesIgnoreNull(form, productInfo);
            	if(StringUtils.isEmpty(form.getProductType())){
                    productInfo.setProductType(type);
                }
                if(StringUtils.isEmpty(weixinopenId)){
                    weixinopenId=form.getUserWxOpenid();
                    if(StringUtils.isEmpty(weixinopenId)){
                        weixinopenId="houtaiadd";
                    }
                }
                productInfo.setUserWxOpenid(weixinopenId);

                //商品价格含(用户追加的价格)
                BigDecimal productPrices = productInfo.getProductPrice();

                BigDecimal addPrice = productInfo.getProductPrice();
                if (productInfo.getTaxFee().intValue()>0){
                    addPrice=addPrice.subtract(productInfo.getTaxFee());
                }

                if (productInfo.getCarriageFee().intValue()>0){
                    addPrice=addPrice.subtract(productInfo.getCarriageFee());
                }
                //求汇率
                String exchangeRate = "6.99";
                if(stringRedisTemplate.hasKey("exchangeRate")){
                    exchangeRate =  (String)stringRedisTemplate.opsForValue().get("exchangeRate");
                }
                productInfo.setUsdRate(new BigDecimal(exchangeRate));

                addPrice =addPrice.subtract(new BigDecimal(productInfo.getProductUsd().doubleValue()*Double.valueOf(exchangeRate)));

                productInfo.setAddPrice(addPrice);
                log.info("商品价格含(用户追加的价格):"+productInfo.getProductPrice().doubleValue());

            	//确定商品价格： 比如包邮包税，那么页面显示价格就是全包的价格；包邮不包税，那么页面显示价格就是商品单价（加邮费）+税费；包税不包邮，页面显示就是商品单价（税费）+运费
                if(productInfo.getCarriageFee()!=null){
                    BigDecimal carriageFee = productInfo.getCarriageFee();
                    if(carriageFee.intValue()<=0 ){
                        BigDecimal carriageFeeproductPrices = productInfo.getProductPrice().add(new BigDecimal(50));
                        productInfo.setProductPrice(carriageFeeproductPrices);
                        log.info("商品价格包运费:"+productInfo.getProductPrice().doubleValue());
                        //处理productAttr商品价格
                        form.getItems().stream().forEach(productAttr -> {
                            if(productAttr.getAttrEname().equals("price")){
                                BigDecimal cvalue = new BigDecimal(productAttr.getAttrCvalue());
                                cvalue = cvalue.add(new BigDecimal(50));
                                productAttr.setAttrCvalue(String.valueOf(cvalue));
                            }
                        });
                    }
                }
                if(productInfo.getTaxFee()!=null) {
                    BigDecimal taxFee = productInfo.getTaxFee();
                    if(taxFee.intValue()<= 0){
                        DecimalFormat df   = new DecimalFormat("######0.00");
                        double txFeed = productInfo.getProductUsd().doubleValue()*Double.valueOf(exchangeRate).doubleValue()*0.119;

                        BigDecimal taxFeeproductPrices = productInfo.getProductPrice().add(new BigDecimal(df.format(txFeed)));
                        productInfo.setProductPrice(taxFeeproductPrices);
                        log.info("商品价格包税费:"+productInfo.getProductPrice().doubleValue());

                        //处理productAttr商品价格
                        form.getItems().stream().forEach(productAttr -> {
                            if(productAttr.getAttrEname().equals("price")){
                                BigDecimal cvalue = new BigDecimal(productAttr.getAttrCvalue());
                                cvalue = cvalue.add(new BigDecimal(df.format(txFeed)));
                                productAttr.setAttrCvalue(cvalue.toString());
                            }
                        });
                    }
                }
            }
            productInfo.setUpdated(new Date());
            productInfo =productService.save(productInfo);
        } catch (SellException e) {
        	return ResultVOUtil.error(0, "添加商品出现异常");
        }
        //先删除扩展表属性
        if (!StringUtils.isEmpty(form.getProductId())){
        	productAttrService.deleteByProductId(Long.parseLong(form.getProductId()));
        }
        //商品扩展属性
        form.setProductId(productInfo.getId().toString());
        List<ProductAttr>  productAttrList = ProductAttrVO2ProductAttr.convert(form);
        productAttrService.save(productAttrList);
        return ResultVOUtil.success();
    }


    /**
     * 创建商品
     * @param form
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "创建商品")
    @PostMapping("/create")
    public ResultVO create(@RequestBody ProductForm form,BindingResult bindingResult,HttpServletResponse response) {
    	
    	response.setHeader("Access-Control-Allow-Origin","*");
    	
        if (bindingResult.hasErrors()) {
        	return ResultVOUtil.error(0, "商品参数不对");
        }

        ProductInfo productInfo = new ProductInfo();
        try {
            //如果productId为空, 说明是新增
            if (!StringUtils.isEmpty(form.getProductId())) {
                productInfo = productService.findOne(Long.parseLong(form.getProductId()));
            } 
            if(null != productInfo){
            	BeanUtilEx.copyPropertiesIgnoreNull(form, productInfo);
            }
            productInfo.setProductSales(RandomUtil.generateDigitalString(2));
            productInfo =productService.save(productInfo);
        } catch (SellException e) {
        	return ResultVOUtil.error(0, "添加商品出现异常");
        }

        //商品扩展属性
        form.setProductId(productInfo.getId().toString());
        List<ProductAttr>  productAttrList = ProductAttrVO2ProductAttr.convert(form);
        productAttrService.save(productAttrList);
        
        Map<String, String> map = new HashMap<>();
        map.put("productId", productInfo.getId().toString());
        
        return ResultVOUtil.success(map);
    }

    /**
     * 保存多属性商品，其中自动翻译localizedAspects部分.
     * @return
     */
    public  boolean  saveGroupGood(){

        return  true;
    }

    /**
     * 商品审核
     * @return
     */
    @ApiOperation(value = "商品审核")
    @PostMapping("/audit")
    public ResultVO audit(@ApiParam(value = "商品id") @RequestParam(required = true, value = "productId", defaultValue = "") String productId,
    		@ApiParam(value = "商品审核状态") @RequestParam(required = true, value = "auditStatus") String auditStatus,
                          @ApiParam(value = "商品销售个数") @RequestParam(required = false, value = "productSales") String productSales,HttpServletResponse response) {
    	
    	response.setHeader("Access-Control-Allow-Origin","*");
        ProductInfo productInfo = new ProductInfo();
        try {
            //如果productId为空, 说明是新增
            if (!StringUtils.isEmpty(productId)) {
                productInfo = productService.findOne(Long.parseLong(productId));
            } 
            if(null == productInfo){
            	
            	return ResultVOUtil.error(0, "商品不存在");
            }
            productInfo.setAuditStatus(auditStatus);
            productInfo.setAudited(new Date());
            if(StringUtils.isEmpty(productSales)){
                productSales=RandomUtil.generateDigitalString(2);
            }
            productInfo.setProductSales(productSales);
            productInfo =productService.save(productInfo);
        } catch (SellException e) {
        	return ResultVOUtil.error(0, "商品审核出现异常");
        }
        return ResultVOUtil.success();
    }
}
