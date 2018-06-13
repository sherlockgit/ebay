package com.winit.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.winit.dataobject.ProductAttr;
import com.winit.dataobject.ProductInfo;
import com.winit.repository.ProductAttrRepository;
import com.winit.service.ProductAttrService;
import com.winit.utils.BeanUtilEx;

/**
 * Created by yeyalin 2017-10-28 15:43
 */
@Service
public class ProductAttrServiceImpl implements ProductAttrService {
	@Autowired
	private ProductAttrRepository repository;

	@Override
	public ProductAttr findOne(Long productAttrId) {
		return repository.findOne(productAttrId);
	}

	@Override
	public List<ProductAttr> findByProductId(Long productId) {
		return repository.findByProductId(productId);
	}

	@Override
	public void  save(List<ProductAttr> productAttrList) {
		
		if (!CollectionUtils.isEmpty(productAttrList)) {
			for (ProductAttr productAttr : productAttrList) {
				
				if (productAttr != null && productAttr.getId()!=null) {
					
					ProductAttr productAttrTemp = repository.findOne(productAttr.getId());
					if(productAttrTemp != null){
						
						BeanUtilEx.copyPropertiesIgnoreNull(productAttr, productAttrTemp);
						productAttr = productAttrTemp;
					}
				}
				 repository.save(productAttr);

			}
		}
	}

	@Override
	public List<ProductAttr> findAll(final ProductAttr productAttr) {

        Specification<ProductAttr> spec = new Specification<ProductAttr>() {  
            public Predicate toPredicate(Root<ProductAttr> root,  
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
          	  List<Predicate> predicate = Lists.newArrayList();
          	  if (productAttr.getProductId()!=null && productAttr.getProductId()>=0) { 
          		  predicate.add(cb.equal(root.get("productId").as(Long.class),
          		  productAttr.getProductId())); 
          	  }
			  if (StringUtils.isNoneBlank(productAttr.getItemId())) {
				  predicate.add(cb.equal(root.get("itemId").as(String.class), productAttr.getItemId()));
			  }
			  if (StringUtils.isNoneBlank(productAttr.getAttrEname())) {
				  predicate.add(cb.equal(root.get("attrEname").as(String.class), productAttr.getAttrEname()));
			  }
			  if (StringUtils.isNoneBlank(productAttr.getAttrEvalue())) {
				  predicate.add(cb.equal(root.get("attrEvalue").as(String.class), productAttr.getAttrEvalue()));
			  }
			  if (StringUtils.isNoneBlank(productAttr.getAttrCname())) {
				  predicate.add(cb.equal(root.get("attrCname").as(String.class), productAttr.getAttrCname()));
			  }
			  if (StringUtils.isNoneBlank(productAttr.getAttrCvalue())) {
				  predicate.add(cb.equal(root.get("attrCvalue").as(String.class), productAttr.getAttrCvalue()));
			  }
			  if (StringUtils.isNoneBlank(productAttr.getAttrType())) {
				  predicate.add(cb.equal(root.get("attrType").as(String.class), productAttr.getAttrType()));
			  }
      		  Predicate[] pre = new Predicate[predicate.size()];
          	  return query.where(predicate.toArray(pre)).getRestriction(); 
            }  
        };  
        return repository.findAll(spec);
	}
	/**按照productId删除ProductAttr
	 * @param productId
	 * @return
	 */
	@Transactional
	public void  deleteByProductId(Long productId){
		repository.deleteByProductId(productId);
	}
}
