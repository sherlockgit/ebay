package com.winit.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.winit.dataobject.ProductAttr;

/**
 * 商品扩展属性查询
 * Created by yeyalin
 * 2017-10-28 15:09
 */
public interface ProductAttrService {

	ProductAttr findOne(Long productAttrId);
	List<ProductAttr> findByProductId(Long productId);
	List<ProductAttr> findAll(final ProductAttr productAttr);
	void  save(List<ProductAttr> productAttrs);
	/**按照productId删除ProductAttr
	 * @param productId
	 * @return
	 */
	void  deleteByProductId(Long productId);
}
