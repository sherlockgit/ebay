package com.winit.service.impl;

import com.google.common.collect.Lists;
import com.winit.dataobject.AccountCheck;
import com.winit.dataobject.AccountItem;
import com.winit.repository.AccountCheckRepository;
import com.winit.repository.AccountItemRepository;
import com.winit.service.AccountItemService;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
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
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by yhy
 * 2017-10-22 18:43
 */
@Service
@Slf4j
public class AccountItemServiceImpl implements AccountItemService {

    @Autowired
    private AccountItemRepository accountItemRepository;
    
    @Autowired
    private AccountCheckRepository accountCheckRepository;
    
    @Override
    @Transactional
    public AccountItem create(AccountItem accountItem) {
    	accountItemRepository.save(accountItem);
        return accountItem;
    }
    
    @Override
    public AccountItem selectOne(String id) {
    	return accountItemRepository.findOne(id);
    }
    
    @Override
    public List<AccountItem> findAll(final AccountItem accountItem,List<String> userIds,List<String> tradeNoList,Date startDate,Date endDate) {
          Specification<AccountItem> spec = new Specification<AccountItem>() {  
              public Predicate toPredicate(Root<AccountItem> root,  
                      CriteriaQuery<?> query, CriteriaBuilder cb) {
            	  List<Predicate> predicate = Lists.newArrayList();
            	  //默认查询有效，未删除的
            	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
                		  "Y"));
            	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
            			  "N"));
            	  if (StringUtils.isNoneBlank(accountItem.getTradeType())) { 
            		  predicate.add(cb.equal(root.get("tradeType").as(String.class), 
            				  accountItem.getTradeType()));
            	  }
            	  if (StringUtils.isNoneBlank(accountItem.getTradeStatus())) { 
            		  predicate.add(cb.equal(root.get("tradeStatus").as(String.class), 
            				  accountItem.getTradeStatus()));
            	  }
            	  if (StringUtils.isNoneBlank(accountItem.getTradeInAmount())) { 
            		  predicate.add(cb.equal(root.get("tradeInAmount").as(String.class), 
            				  accountItem.getTradeInAmount()));
            	  }
            	  if (StringUtils.isNoneBlank(accountItem.getUserId())) {
            		  predicate.add(cb.equal(root.get("userId").as(String.class), 
            				  accountItem.getUserId()));
            	  }
            	  if(null != userIds && !userIds.isEmpty()){
            		  In<String> in = cb.in(root.get("userId"));
                      for (String userId : userIds) {
                          in.value(userId);
                      }
                      predicate.add(in);
            	  }
            	  if(null != tradeNoList && !tradeNoList.isEmpty()){
            		  In<String> in = cb.in(root.get("tradeNo"));
                      for (String tradeNo : tradeNoList) {
                          in.value(tradeNo);
                      }
                      predicate.add(in);
            	  }
            	  if (null != startDate && null != endDate) {
            		  predicate.add(cb.between(root.<Date>get("created"),startDate,endDate));
            	  }
            	  
        		  Predicate[] pre = new Predicate[predicate.size()];
            	  return query.where(predicate.toArray(pre)).getRestriction(); 
              }  
          };  
          return accountItemRepository.findAll(spec);  
    }
    
    @Override
    public Page<AccountItem> findPage(final AccountItem accountItem,Date startDate,Date endDate,Pageable pageable) {
          Specification<AccountItem> spec = new Specification<AccountItem>() {  
              public Predicate toPredicate(Root<AccountItem> root,  
                      CriteriaQuery<?> query, CriteriaBuilder cb) {
            	  List<Predicate> predicate = Lists.newArrayList();
            	  //默认查询有效，未删除的
            	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
                		  "Y"));
            	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
            			  "N"));
            	  if (StringUtils.isNoneBlank(accountItem.getUserId())) { 
            		  predicate.add(cb.equal(root.get("userId").as(String.class), 
            				  accountItem.getUserId())); 
            	  }
            	  if (StringUtils.isNoneBlank(accountItem.getTradeType())) { 
            		  predicate.add(cb.equal(root.get("tradeType").as(String.class), 
            				  accountItem.getTradeType()));
            	  }
            	  if (StringUtils.isNoneBlank(accountItem.getTradeStatus())) { 
            		  predicate.add(cb.equal(root.get("tradeStatus").as(String.class), 
            				  accountItem.getTradeStatus()));
            	  }
            	  if(null != accountItem.getCreated()){
            		  predicate.add(cb.equal(root.get("created").as(Date.class), 
            				  accountItem.getCreated())); 
            	  }
            	  if (null != startDate && null != endDate) {
            		  predicate.add(cb.between(root.<Date>get("created"),startDate,endDate));
            	  }
        		  Predicate[] pre = new Predicate[predicate.size()];
            	  return query.where(predicate.toArray(pre)).getRestriction(); 
              }  
          };  
          return accountItemRepository.findAll(spec, pageable);  
    }
    
    @Override
    public Page<AccountCheck> findCheckPage(final AccountCheck accountCheck,Date startDate,Date endDate,Pageable pageable) {
          Specification<AccountCheck> spec = new Specification<AccountCheck>() {  
              public Predicate toPredicate(Root<AccountCheck> root,  
                      CriteriaQuery<?> query, CriteriaBuilder cb) {
            	  List<Predicate> predicate = Lists.newArrayList();
            	  //默认查询有效，未删除的
            	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
            			  "N"));
            	  if(StringUtils.isNoneBlank(accountCheck.getIsActive())){
            		  predicate.add(cb.equal(root.get("isActive").as(String.class), 
            				  accountCheck.getIsActive()));
            	  }
            	  if(StringUtils.isNoneBlank(accountCheck.getCheckStatus())){
            		  predicate.add(cb.equal(root.get("checkStatus").as(String.class), 
            				  accountCheck.getCheckStatus()));
            	  }
            	  if (StringUtils.isNoneBlank(accountCheck.getTradeNo())) { 
            		  predicate.add(cb.equal(root.get("tradeNo").as(String.class), 
            				  accountCheck.getTradeNo())); 
            	  }
            	  if (null != startDate && null != endDate) {
            		  Predicate p = cb.or(cb.between(root.<Date>get("platformTime"),startDate,endDate), 
            				  cb.between(root.<Date>get("ebayTime"),startDate,endDate));
              		  p = cb.and(p);
              		  predicate.add(p);
            	  }
        		  Predicate[] pre = new Predicate[predicate.size()];
            	  return query.where(predicate.toArray(pre)).getRestriction(); 
              }  
          };  
          return accountCheckRepository.findAll(spec, pageable);  
    }
    
    @Override
    @Transactional
    public AccountCheck editCheck(AccountCheck accountCheck) {
    	accountCheckRepository.save(accountCheck);
        return accountCheck;
    }
    
    @Override
    public AccountCheck selectOneCheck(String id) {
    	return accountCheckRepository.findOne(id);
    }
}
