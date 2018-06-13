package com.winit.service.impl;

import com.google.common.collect.Lists;
import com.winit.dataobject.Account;
import com.winit.dataobject.AccountWithdraw;
import com.winit.dataobject.User;
import com.winit.repository.AccountRepository;
import com.winit.repository.AccountWithdrawRepository;
import com.winit.service.AccountService;
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
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AccountWithdrawRepository accountWithdrawRepository;
    
    @Override
    @Transactional
    public Account create(Account account) {
    	accountRepository.save(account);
        return account;
    }
    
    @Override
    public Account selectOne(String id) {
    	return accountRepository.findOne(id);
    }
    
    @Override
    public Account findByUserId(String userId) {
        return accountRepository.findByUserId(userId);
    }
    
    @Override
    public Page<Account> findPage(final Account account,List<String> userIds,Date startDate,Date endDate,Pageable pageable) {
          Specification<Account> spec = new Specification<Account>() {  
              public Predicate toPredicate(Root<Account> root,  
                      CriteriaQuery<?> query, CriteriaBuilder cb) {
            	  List<Predicate> predicate = Lists.newArrayList();
            	  //默认查询有效，未删除的
            	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
                		  "Y"));
            	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
            			  "N"));
            	  if (null != startDate && null != endDate) {
            		  predicate.add(cb.between(root.<Date>get("updated"),startDate,endDate));
            	  }
            	  if(null != userIds && !userIds.isEmpty()){
                      In<String> in = cb.in(root.get("userId"));
                      for (String userId : userIds) {
                          in.value(userId);
                      }
                      predicate.add(in);
              	  }
            	  
        		  Predicate[] pre = new Predicate[predicate.size()];
            	  return query.where(predicate.toArray(pre)).getRestriction(); 
              }  
          };  
          return accountRepository.findAll(spec, pageable);  
    }
    
    @Override
    public Page<AccountWithdraw> findAWPage(final AccountWithdraw accountWithdraw,List<String> userIds,Pageable pageable) {
    	Specification<AccountWithdraw> spec = new Specification<AccountWithdraw>() {  
    		public Predicate toPredicate(Root<AccountWithdraw> root,  
    				CriteriaQuery<?> query, CriteriaBuilder cb) {
    			List<Predicate> predicate = Lists.newArrayList();
    			//默认查询有效，未删除的
    			predicate.add(cb.equal(root.get("isDelete").as(String.class), 
    					"N"));
    			
    			if (StringUtils.isNoneBlank(accountWithdraw.getAuditStatus())) { 
  	       		     predicate.add(cb.equal(root.get("auditStatus").as(String.class), accountWithdraw.getAuditStatus() )); 
  	       	    }
    			if (StringUtils.isNoneBlank(accountWithdraw.getPerilRatio())) { 
    	       		 predicate.add(cb.equal(root.get("perilRatio").as(String.class), accountWithdraw.getPerilRatio() )); 
    	       	}
    			if(null != userIds && !userIds.isEmpty()){
                    In<String> in = cb.in(root.get("userId"));
                    for (String userId : userIds) {
                        in.value(userId);
                    }
                    predicate.add(in);
             	}
    			Predicate[] pre = new Predicate[predicate.size()];
    			return query.where(predicate.toArray(pre)).getRestriction(); 
    		}  
    	};  
    	return accountWithdrawRepository.findAll(spec, pageable);  
    }
    
    @Override
    @Transactional
    public AccountWithdraw editWithdraw(AccountWithdraw accountWithdraw) {
    	accountWithdrawRepository.save(accountWithdraw);
        return accountWithdraw;
    }
    
    @Override
    public AccountWithdraw withdrawSelectOne(String id) {
    	return accountWithdrawRepository.findOne(id);
    }
}
