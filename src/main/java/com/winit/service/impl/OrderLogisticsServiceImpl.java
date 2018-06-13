package com.winit.service.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.winit.dataobject.OrderLogistics;
import com.winit.repository.OrderLogisticsRepository;
import com.winit.service.OrderLogisticsService;

@Service
public class OrderLogisticsServiceImpl implements OrderLogisticsService {
	
	@Autowired
	private OrderLogisticsRepository repository;
	
	@Override
	public OrderLogistics save(OrderLogistics orderLogistics) {
		return repository.save(orderLogistics);
	}

	@Override
	public void delete(OrderLogistics orderLogistics) {
		repository.delete(orderLogistics);
		
	}
	@Override
	public OrderLogistics findOne(Long id) {
		return repository.findOne(id);
	}

	@Override
	public Page<OrderLogistics> findListPage(OrderLogistics orderLogistics, Pageable pageable) {

    	Specification<OrderLogistics> spec = new Specification<OrderLogistics>() {  
            public Predicate toPredicate(Root<OrderLogistics> root,  
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
          	  List<Predicate> predicate = Lists.newArrayList();
          	  //默认查询有效，未删除的
          	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
              		  "Y"));
          	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
          			  "N"));
          	  if (StringUtils.isNoneBlank(orderLogistics.getOrderNo())) { 
          		  predicate.add(cb.equal(root.get("orderNo").as(String.class), 
          				orderLogistics.getOrderNo())); 
          	  }
          	  if (StringUtils.isNoneBlank(orderLogistics.getLogisticsInfo())) { 
          		predicate.add(cb.equal(root.get("logisticsInfo").as(String.class), 
          				orderLogistics.getLogisticsInfo())); 
          	  }
      		  Predicate[] pre = new Predicate[predicate.size()];
          	  return query.where(predicate.toArray(pre)).getRestriction(); 
            }  
        };
        
        return repository.findAll(spec,pageable);
  
	
	}
	public List<OrderLogistics> findList(OrderLogistics orderLogistics ){
		Specification<OrderLogistics> spec = new Specification<OrderLogistics>() {  
            public Predicate toPredicate(Root<OrderLogistics> root,  
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
          	  List<Predicate> predicate = Lists.newArrayList();
          	  //默认查询有效，未删除的
          	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
              		  "Y"));
          	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
          			  "N"));
          	  if (StringUtils.isNoneBlank(orderLogistics.getOrderNo())) { 
          		  predicate.add(cb.equal(root.get("orderNo").as(String.class), 
          				orderLogistics.getOrderNo())); 
          	  }
          	  if (StringUtils.isNoneBlank(orderLogistics.getLogisticsInfo())) { 
          		predicate.add(cb.equal(root.get("logisticsInfo").as(String.class), 
          				orderLogistics.getLogisticsInfo())); 
          	  }
      		  Predicate[] pre = new Predicate[predicate.size()];
          	  return query.where(predicate.toArray(pre)).getRestriction(); 
            }  
        };
		return repository.findAll(spec);
	}
	
	@Override
	public List<OrderLogistics> findByOrderNoList(List<String> orderNos) {
		
		Specification<OrderLogistics> spec = new Specification<OrderLogistics>() {  
            public Predicate toPredicate(Root<OrderLogistics> root,  
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
          	  List<Predicate> predicate = Lists.newArrayList();
				if (!CollectionUtils.isEmpty(orderNos)) {
					In<String> in = cb.in(root.get("orderNo"));
					for (String orderNo : orderNos) {
						in.value(orderNo);
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
