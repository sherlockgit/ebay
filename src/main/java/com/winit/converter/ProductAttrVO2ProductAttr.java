package com.winit.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import com.winit.VO.ProductAttrVO;
import com.winit.dataobject.ProductAttr;
import com.winit.form.ProductForm;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by liyou
 * 2017-06-18 23:41
 */
@Slf4j
public class ProductAttrVO2ProductAttr {

    public static List<ProductAttr> convert(ProductForm productForm) {
    	List<ProductAttr> productAttrs = new ArrayList<>();
        List<ProductAttrVO> productAttrVOList = productForm.getItems();
        if(!CollectionUtils.isEmpty(productAttrVOList)){
        	
        	for(ProductAttrVO vo : productAttrVOList){
        		ProductAttr productAttr = new ProductAttr();
        		BeanUtils.copyProperties(vo, productAttr);
        		productAttr.setProductId(Long.parseLong(productForm.getProductId()));
        		productAttrs.add(productAttr);
        	}
        }

        return productAttrs;
    }
}
