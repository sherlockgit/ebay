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
import com.winit.dataobject.OrderErrorRc;
import com.winit.repository.OrderErrorRcRepository;
import com.winit.service.OrderErrorRcService;
import com.winit.utils.BeanUtilEx;

@Service
public class OrderErrorRcServiceImpl implements OrderErrorRcService {
	@Autowired
	private OrderErrorRcRepository repository;
	@Override
	public OrderErrorRc save(OrderErrorRc orderErrorRc) {
		
		if (orderErrorRc != null && orderErrorRc.getId() != null) {

			OrderErrorRc orderErrorRcTemp = repository.findOne(orderErrorRc.getId());
			
			if(orderErrorRcTemp!=null){
				
				orderErrorRc = orderErrorRcTemp;
				BeanUtilEx.copyPropertiesIgnoreNull(orderErrorRc, orderErrorRcTemp);
			}
		}
		return repository.save(orderErrorRc);
	}

	@Override
	public OrderErrorRc findOne(Long id) {
		return repository.findOne(id);
	}

	@Override
	public Page<OrderErrorRc> findAllPage(OrderErrorRc orderErrorRc, Pageable pageable) {

    	Specification<OrderErrorRc> spec = new Specification<OrderErrorRc>() {  
            public Predicate toPredicate(Root<OrderErrorRc> root,  
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
          	  List<Predicate> predicate = Lists.newArrayList();
          	  //默认查询有效，未删除的
          	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
              		  "Y"));
          	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
          			  "N"));
          	  if (StringUtils.isNoneBlank(orderErrorRc.getErrorNo())) { 
          		  predicate.add(cb.equal(root.get("errorNo").as(String.class), 
          				orderErrorRc.getErrorNo())); 
          	  }
          	  if (StringUtils.isNoneBlank(orderErrorRc.getErrorType())) { 
          		predicate.add(cb.equal(root.get("errorType").as(String.class), 
          				orderErrorRc.getErrorType())); 
          	  }
          	  if (StringUtils.isNoneBlank(orderErrorRc.getErrorStatus())) {
          		  predicate.add(cb.equal(root.get("errorStatus").as(String.class), 
          				orderErrorRc.getErrorStatus())); 
          	  }
          	  if (StringUtils.isNoneBlank(orderErrorRc.getErrorMemo())) {
        		  predicate.add(cb.like(root.get("errorMemo").as(String.class), 
                		  "%" + orderErrorRc.getErrorMemo() + "%")); 
        	  }
          	  if (StringUtils.isNoneBlank(orderErrorRc.getSloveMemo())) {
          		 predicate.add(cb.like(root.get("sloveMemo").as(String.class), 
               		  "%" + orderErrorRc.getSloveMemo() + "%"));
          	  }
          	  if (StringUtils.isNoneBlank(orderErrorRc.getHanderType())) {
          		  predicate.add(cb.equal(root.get("handerType").as(String.class), 
          				orderErrorRc.getHanderType())); 
          	  }
          	  if (orderErrorRc.getOrganizationId()!=null) {
        		  predicate.add(cb.equal(root.get("organizationId").as(String.class), 
        				  orderErrorRc.getOrganizationId())); 
        	  }
      		  Predicate[] pre = new Predicate[predicate.size()];
          	  return query.where(predicate.toArray(pre)).getRestriction(); 
            }  
        };
        
        return repository.findAll(spec,pageable);
  
	
	}

	@Override
	public void delete(OrderErrorRc orderErrorRc) {
		repository.delete(orderErrorRc);

	}

}
