package com.winit.service.impl;

import javax.persistence.criteria.Predicate;

import com.google.common.collect.Lists;
import com.winit.dataobject.Account;
import com.winit.dataobject.User;
import com.winit.service.AccountService;
import com.winit.service.UserService;
import com.winit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
 * 2017-10-21 18:43
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
	
    @Autowired
    private UserRepository UserRepository;
    
    @Autowired
    private AccountService accountService;
    
    
    @Override
    @Transactional
    public User create(User user) {
    	if("null".equals(user.getUserPhone())){
    		user.setUserPhone("");
    	}
    	//添加账户  -- 会员称为分销商
    	if(StringUtils.isNotBlank(user.getId()) && "1".equals(user.getUserCtype())){
    		Account account_db = accountService.findByUserId(user.getId());
    		if(null == account_db){
    			Account account = new Account();
    			account.setUserId(user.getId());
    			account.setUserBalance(new BigDecimal(0));
    			account.setUserDrawable(new BigDecimal(0));
    			accountService.create(account);
    		}
    	}
    	UserRepository.save(user);
        return user;
    }
    
    @Override
    public User selectOne(String id) {
    	return UserRepository.findOne(id);
    }
    
    @Override
    public User selectByUserWxOpenid(String userWxOpenid) {
    	return UserRepository.findByUserWxOpenid(userWxOpenid);
    }
    
    @Override
    public Page<User> findPage(final User user,Pageable pageable) {
          Specification<User> spec = new Specification<User>() {  
              public Predicate toPredicate(Root<User> root,  
                      CriteriaQuery<?> query, CriteriaBuilder cb) {
            	  List<Predicate> predicate = Lists.newArrayList();
            	  //默认查询有效，未删除的
//            	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
//                		  "Y"));
            	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
            			  "N"));
            	  if (StringUtils.isNoneBlank(user.getUserName())) { 
            		  predicate.add(cb.like(root.get("userName").as(String.class), 
            		  "%" + user.getUserName() + "%")); 
            	  }
            	  if (StringUtils.isNoneBlank(user.getUserPhone())) { 
            		  predicate.add(cb.like(root.get("userPhone").as(String.class), 
            		  "%" + user.getUserPhone() + "%")); 
            	  }
            	  if (StringUtils.isNoneBlank(user.getUserCtype())) { 
            		  predicate.add(cb.equal(root.get("userCtype").as(String.class), user.getUserCtype() )); 
            	  }
            	  if (StringUtils.isNoneBlank(user.getUserAddr())) { 
            		  predicate.add(cb.like(root.get("userAddr").as(String.class), 
            				  "%" + user.getUserAddr() + "%")); 
            	  }
            	  if (StringUtils.isNoneBlank(user.getUserWxName())) { 
            		  predicate.add(cb.like(root.get("userWxName").as(String.class),
            				  "%" + user.getUserWxName() + "%"));
            	  }
            	  if (StringUtils.isNoneBlank(user.getUserWxOpenid())) { 
            		  predicate.add(cb.like(root.get("userWxOpenid").as(String.class),
            				  "%"+user.getUserWxOpenid()+"%"));
            	  }
        		  Predicate[] pre = new Predicate[predicate.size()];
            	  return query.where(predicate.toArray(pre)).getRestriction(); 
//                  return query.where(
//                		  cb.like(root.get("userName").as(String.class), "%"+user.getUserName()+"%")
//                		  )  
//                          .getRestriction();  
              }  
          };  
          return UserRepository.findAll(spec, pageable);  
    }

    @Override
    @Transactional
    public void batch(List<User> users) {
    	UserRepository.save(users);
    }
    
    @Override
    @Transactional
    public List<User> selectList(List<String> ids) {
    	return (List<User>) UserRepository.findAll(ids);
    }
    
    @Override
    @Transactional
    public List<User> selectList(final User user,List<String> wxOpenIds) {
    	 Specification<User> spec = new Specification<User>() {  
             public Predicate toPredicate(Root<User> root,  
                     CriteriaQuery<?> query, CriteriaBuilder cb) {
           	  List<Predicate> predicate = Lists.newArrayList();
           	  //默认查询有效，未删除的
//           	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
//               		  "Y"));
           	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
           			  "N"));
           	  if (StringUtils.isNoneBlank(user.getUserName())) { 
       		     predicate.add(cb.like(root.get("userName").as(String.class), 
       		     "%" + user.getUserName() + "%")); 
	       	  }
	       	  if (StringUtils.isNoneBlank(user.getUserPhone())) { 
	       		  predicate.add(cb.like(root.get("userPhone").as(String.class), 
	       		  "%" + user.getUserPhone() + "%")); 
	       	  }
	       	  if (StringUtils.isNoneBlank(user.getUserCtype())) { 
	       		  predicate.add(cb.equal(root.get("userCtype").as(String.class), user.getUserCtype() )); 
	       	  }
	       	  if (StringUtils.isNoneBlank(user.getUserAddr())) { 
	         	  predicate.add(cb.like(root.get("userAddr").as(String.class), 
	       				  "%" + user.getUserAddr() + "%")); 
	       	  }
	       	  if (StringUtils.isNoneBlank(user.getUserWxName())) { 
	       		  predicate.add(cb.like(root.get("userWxName").as(String.class), 
	       				  "%" + user.getUserWxName() + "%")); 
	       	  }
	       	  if (StringUtils.isNoneBlank(user.getUserWxOpenid())) { 
	       		  predicate.add(cb.equal(root.get("userWxOpenid").as(String.class), 
	       				  user.getUserWxOpenid())); 
	       	  }
           	  if(null != wxOpenIds && !wxOpenIds.isEmpty()){
                   In<String> in = cb.in(root.get("userWxOpenid"));
                   for (String userWxOpenid : wxOpenIds) {
                       in.value(userWxOpenid);
                   }
                   predicate.add(in);
           	  }
       		  Predicate[] pre = new Predicate[predicate.size()];
           	  return query.where(predicate.toArray(pre)).getRestriction(); 
             }  
         };  
    	return UserRepository.findAll(spec);
    }


}
