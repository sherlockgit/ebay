package com.winit.service.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.winit.dataobject.OrderError;
import com.winit.repository.OrderErrorRepository;
import com.winit.service.OrderErrorService;
import com.winit.utils.BeanUtilEx;

@Service
public class OrderErrorServiceImpl implements OrderErrorService {
	
	@Autowired
	private OrderErrorRepository repository;
	
	@Override
	public OrderError save(OrderError orderError) {
		
		if (orderError != null && orderError.getId()!=null) {
			
			OrderError orderErrorTemp = repository.findByOrderNo(orderError.getOrderNo());
			Long id = orderErrorTemp.getId();
			if(orderErrorTemp!=null){
				BeanUtilEx.copyPropertiesIgnoreNull(orderError, orderErrorTemp);
				orderError = orderErrorTemp;
				orderError.setId(id);
			}
		}
		
		return repository.save(orderError);
	}

	@Override
	public OrderError findOne(Long id) {
		return repository.findOne(id);
	}

	@Override
	public Page<OrderError> findAll(OrderError orderError, Pageable pageable) {

    	Specification<OrderError> spec = new Specification<OrderError>() {  
            public Predicate toPredicate(Root<OrderError> root,  
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
          	  List<Predicate> predicate = Lists.newArrayList();
          	  //默认查询有效，未删除的
          	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
              		  "Y"));
          	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
          			  "N"));
          	  if (StringUtils.isNoneBlank(orderError.getErrorNo())) { 
          		  predicate.add(cb.equal(root.get("errorNo").as(String.class), 
          		  orderError.getErrorNo())); 
          	  }
          	  if (StringUtils.isNoneBlank(orderError.getOrderNo())) { 
          		predicate.add(cb.like(root.get("orderNo").as(String.class),
                		  "%"+orderError.getOrderNo()+"%"));
          	  }
          	  if (StringUtils.isNoneBlank(orderError.getErrorType())) { 
          		  predicate.add(cb.equal(root.get("errorType").as(String.class), 
          				  orderError.getErrorType())); 
          	  }
          	  if (StringUtils.isNoneBlank(orderError.getErrorStatus())) {
          		  predicate.add(cb.equal(root.get("errorStatus").as(String.class), 
          				  orderError.getErrorStatus())); 
          	  }
          	  if (StringUtils.isNoneBlank(orderError.getErrorMemo())) {
        		  predicate.add(cb.like(root.get("errorMemo").as(String.class), 
                		  "%" + orderError.getErrorMemo() + "%")); 
        	  }
          	  if (StringUtils.isNoneBlank(orderError.getSloveMemo())) {
          		 predicate.add(cb.like(root.get("sloveMemo").as(String.class), 
               		  "%" + orderError.getSloveMemo() + "%"));
          	  }
          	  if (StringUtils.isNoneBlank(orderError.getHanderby())) {
          		  predicate.add(cb.like(root.get("handerby").as(String.class),
      				  "%"+orderError.getHanderby()+"%"));
          	  }
          	  if (orderError.getOrganizationId()!=null) {
        		  predicate.add(cb.equal(root.get("organizationId").as(String.class), 
    				  orderError.getOrganizationId())); 
        	  }
      		  Predicate[] pre = new Predicate[predicate.size()];
          	  return query.where(predicate.toArray(pre)).getRestriction(); 
            }  
        };  
        return repository.findAll(spec,pageable);
  
	}
	@Override
	public void delete(OrderError  orderError) {
		 repository.delete(orderError);
	}

}
