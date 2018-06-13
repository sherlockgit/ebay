package com.winit.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.param.ExplicitParameterSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.winit.dataobject.AccountItem;
import com.winit.dataobject.OrderDetail;
import com.winit.dataobject.ProductInfo;
import com.winit.dto.CartDTO;
import com.winit.enums.ProductStatusEnum;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.repository.ProductInfoRepository;
import com.winit.service.ProductService;
import com.winit.utils.BeanUtilEx;

/**
 * Created by liyou
 * 2017-05-09 17:31
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository repository;

    @Override
    public ProductInfo findOne(Long productId) {
        return repository.findOne(productId);
    }

    @Override
    public List<ProductInfo> findUpAll() {
        return repository.findByProductStatus(ProductStatusEnum.UP.getMessage());
    }

    @Override
    public Page<ProductInfo> findUpAll(Pageable pageable) {
        return repository.findByProductStatus(ProductStatusEnum.UP.getMessage(),pageable);
    }

    
    @Override
    public Page<ProductInfo> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public ProductInfo save(ProductInfo productInfo) {
    	
    	if(productInfo!=null && productInfo.getId()!=null){

    		ProductInfo productInfoTemp =repository.findOne(productInfo.getId());
    		if(productInfoTemp!=null){
                productInfo.setAudited(new Date());
    			BeanUtilEx.copyPropertiesIgnoreNull(productInfo, productInfoTemp);
    			productInfo=productInfoTemp;
    		}
    	}
        return repository.save(productInfo);
    }

    @Override
    @Transactional
    public void increaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO: cartDTOList) {
            ProductInfo productInfo = repository.findOne(cartDTO.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer result = productInfo.getProductStock() + cartDTO.getProductQuantity();
            productInfo.setProductStock(result);

            repository.save(productInfo);
        }

    }

    @Override
    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO: cartDTOList) {
            ProductInfo productInfo = repository.findOne(cartDTO.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer productStock = 0;
            Integer productQuantity = 0;
            if( null != productInfo.getProductStock()){
            	productStock=productInfo.getProductStock();
            }
            if( null != cartDTO.getProductQuantity()){
            	productQuantity=cartDTO.getProductQuantity();
            }
            Integer result = productStock - productQuantity;
            if (result < 0) {
                //throw new SellException(ResultEnum.PRODUCT_STOCK_ERROR);
            }

            productInfo.setProductStock(result);

            repository.save(productInfo);
        }
    }

    @Override
    public ProductInfo onSale(Long productId) {
        ProductInfo productInfo = repository.findOne(productId);
        if (productInfo == null) {
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductStatus() == ProductStatusEnum.UP.getMessage()) {
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        //更新
        productInfo.setProductStatus(ProductStatusEnum.UP.getMessage());
        return repository.save(productInfo);
    }

    @Override
    public ProductInfo offSale(Long productId) {
        ProductInfo productInfo = repository.findOne(productId);
        if (productInfo == null) {
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductStatus() == ProductStatusEnum.DOWN.getMessage()) {
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        //更新
        productInfo.setProductStatus(ProductStatusEnum.DOWN.getMessage());
        return repository.save(productInfo);
    }

	@Override
	public Page<ProductInfo> findByUserWxOpenid(String  userWxOpenid,Pageable pageable) {
		return repository.findByUserWxOpenid(userWxOpenid,pageable);
	}
    @Override
    public Page<ProductInfo> findByUserWxOpenidAndAuditStatus(Pageable pageable, String userWxOpenid,String auditStatus) {
        return repository.findByUserWxOpenidAndAuditStatus(pageable,userWxOpenid,auditStatus);
    }
    
    
    @Override
    public List<ProductInfo> findAll(final ProductInfo productInfo,Date startDate,Date endDate) {
          Specification<ProductInfo> spec = new Specification<ProductInfo>() {  
              public Predicate toPredicate(Root<ProductInfo> root,  
                      CriteriaQuery<?> query, CriteriaBuilder cb) {
            	  List<Predicate> predicate = Lists.newArrayList();
            	  //默认查询有效，未删除的
            	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
                		  "Y"));
            	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
            			  "N"));
            	  if (StringUtils.isNoneBlank(productInfo.getProductNo())) {
            		  predicate.add(cb.like(root.get("productNo").as(String.class), 
            		  productInfo.getProductNo())); 
            	  }
				  if (StringUtils.isNoneBlank(productInfo.getProductType())) {
					  predicate.add(cb.equal(root.get("productType").as(String.class),
							  productInfo.getProductType()));
				  }
            	  if (StringUtils.isNoneBlank(productInfo.getProductNane())) {
            		  predicate.add(cb.like(root.get("productNane").as(String.class), 
            		  "%" + productInfo.getProductNane() + "%")); 
            	  }
            	  if (StringUtils.isNoneBlank(productInfo.getUserWxOpenid())) {
            		  predicate.add(cb.equal(root.get("userWxOpenid").as(String.class), 
            				  productInfo.getUserWxOpenid())); 
            	  }
            	  if (StringUtils.isNoneBlank(productInfo.getAuditStatus())) {
            		  predicate.add(cb.like(root.get("auditStatus").as(String.class), 
            				  productInfo.getAuditStatus())); 
            	  }
            	  if (null != startDate && null != endDate) {
            		  predicate.add(cb.between(root.<Date>get("created"),startDate,endDate));
            	  }
//            	  if(null != ids && !ids.isEmpty()){
//      				In<Object> in = cb.in(root.get("id"));
//      				for (Long id : ids) {
//      					in.value(id);
//      				}
//      				predicate.add(in);
//      		  }
        		  Predicate[] pre = new Predicate[predicate.size()];
            	  return query.where(predicate.toArray(pre)).getRestriction(); 
              }  
          };  
          return repository.findAll(spec);
    }
    
    @Override
    public List<ProductInfo> findListByIds(List<Long> ids) {
    	return repository.findAll(ids);
    }
    /**
     * 查询商品--通用接口
     * @param 
     * @return
     */
    public Page<ProductInfo> findAllByProductInfo(ProductInfo productInfo,Pageable pageable){
    	Specification<ProductInfo> spec = new Specification<ProductInfo>() {  
            public Predicate toPredicate(Root<ProductInfo> root,  
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
          	  List<Predicate> predicate = Lists.newArrayList();
          	  //默认查询有效，未删除的
          	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
              		  "Y"));
          	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
          			  "N"));
          	  if (StringUtils.isNoneBlank(productInfo.getProductNo())) {
          		  predicate.add(cb.like(root.get("productNo").as(String.class), 
          		  productInfo.getProductNo())); 
          	  }
          	  if (StringUtils.isNoneBlank(productInfo.getProductNane())) {
          		  predicate.add(cb.like(root.get("productNane").as(String.class), 
          		  "%" + productInfo.getProductNane() + "%")); 
          	  }
          	  if (StringUtils.isNoneBlank(productInfo.getUserWxOpenid())) {
          		  predicate.add(cb.like(root.get("userWxOpenid").as(String.class),
          				  "%"+productInfo.getUserWxOpenid()+"%"));
          	  }
          	  if (StringUtils.isNoneBlank(productInfo.getAuditStatus())) {
          		  predicate.add(cb.equal(root.get("auditStatus").as(String.class), 
          				  productInfo.getAuditStatus())); 
          	  }
          	  if (StringUtils.isNoneBlank(productInfo.getProductCountry())) {
        		  predicate.add(cb.equal(root.get("productCountry").as(String.class), 
        				  productInfo.getProductCountry())); 
        	  }
          	  if (StringUtils.isNoneBlank(productInfo.getProductBrand())) {
          		  predicate.add(cb.equal(root.get("productBrand").as(String.class), 
      				  productInfo.getProductBrand())); 
          	  }
          	  if (StringUtils.isNoneBlank(productInfo.getProductFeatures())) {
          		  predicate.add(cb.equal(root.get("productFeatures").as(String.class), 
      				  productInfo.getProductFeatures())); 
          	  }
          	  if (productInfo.getCategoryType()!=null) {
        		  predicate.add(cb.equal(root.get("categoryType").as(String.class), 
    				  productInfo.getCategoryType())); 
        	  }
          	  if (StringUtils.isNoneBlank(productInfo.getProductType())) {
        		  predicate.add(cb.equal(root.get("productType").as(String.class), 
    				  productInfo.getProductType())); 
        	  }
          	  if (StringUtils.isNoneBlank(productInfo.getProductStatus())) {
	       		  predicate.add(cb.equal(root.get("productStatus").as(String.class), 
	   				  productInfo.getProductStatus())); 
	       	  }
          	  if (StringUtils.isNoneBlank(productInfo.getEbayItemid())) {
	       		  predicate.add(cb.like(root.get("ebayItemid").as(String.class),
	   				  "%"+productInfo.getEbayItemid()+"%"));
	       	  }
      		  Predicate[] pre = new Predicate[predicate.size()];
				query.orderBy(cb.desc(root.get("updated").as(Date.class)));
				//query.orderBy(cb.desc(root.get("productSales").as(String.class)));
				return query.where(predicate.toArray(pre)).getRestriction();
            }  
        };  
        return repository.findAll(spec,pageable);
  
    }
    @Override
	public List<ProductInfo> findByUserWxOpenid(String  userWxOpenid) {
		return repository.findByUserWxOpenid(userWxOpenid);
	}
	/**
	 * 通过译者微信号获取商品信息
	 * @param userWxOpenids
	 * @return
	 */
	@Override
	public List<ProductInfo> findByUserWxOpenids(List<String> userWxOpenids) {

		Specification<ProductInfo> spec = new Specification<ProductInfo>() {
			public Predicate toPredicate(Root<ProductInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				if (!CollectionUtils.isEmpty(userWxOpenids)) {
					In<String> in = cb.in(root.get("userWxOpenid"));
					for (String userWxOpenid : userWxOpenids) {
						in.value(userWxOpenid);
					}
					predicate.add(in);
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};

		return repository.findAll(spec);
	}
}
