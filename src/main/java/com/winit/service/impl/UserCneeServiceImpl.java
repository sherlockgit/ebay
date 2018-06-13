package com.winit.service.impl;

import javax.persistence.criteria.Predicate;
import com.google.common.collect.Lists;
import com.winit.dataobject.UserCnee;
import com.winit.service.UserCneeService;
import com.winit.repository.UserCneeRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class UserCneeServiceImpl implements UserCneeService {

    @Autowired
    private UserCneeRepository userCneeRepository;
    
    @Override
    @Transactional
    public UserCnee create(UserCnee userCnee) {
    	userCneeRepository.save(userCnee);
        return userCnee;
    }
    
    @Override
    public UserCnee selectOne(String id) {
    	return userCneeRepository.findOne(id);
    }
    
    @Override
    public Page<UserCnee> findPage(final UserCnee userCnee,Pageable pageable) {
          Specification<UserCnee> spec = new Specification<UserCnee>() {  
              public Predicate toPredicate(Root<UserCnee> root,  
                      CriteriaQuery<?> query, CriteriaBuilder cb) {
            	  List<Predicate> predicate = Lists.newArrayList();
            	  //默认查询有效，未删除的
            	  predicate.add(cb.equal(root.get("isActive").as(String.class), 
                		  "Y"));
            	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
            			  "N"));
            	  
            	  if (StringUtils.isNoneBlank(userCnee.getUserId())) { 
            		  predicate.add(cb.equal(root.get("userId").as(String.class), 
            				  userCnee.getUserId())); 
            	  }
            	  if (StringUtils.isNoneBlank(userCnee.getIsDefaute()) 
            			  && "Y".equals(userCnee.getIsDefaute())) { 
            		  predicate.add(cb.equal(root.get("isDefaute").as(String.class), 
            				  userCnee.getIsDefaute())); 
            	  }
            	  if (StringUtils.isNoneBlank(userCnee.getCneePhone())) { 
            		  predicate.add(cb.equal(root.get("cneePhone").as(String.class), 
            				  userCnee.getCneePhone())); 
            	  }
            	  if (StringUtils.isNoneBlank(userCnee.getCneeIdcard())) { 
            		  predicate.add(cb.equal(root.get("cneeIdcard").as(String.class), 
            				  userCnee.getCneeIdcard())); 
            	  }
            	  if (StringUtils.isNoneBlank(userCnee.getCneeName())) { 
            		  predicate.add(cb.like(root.get("cneeName").as(String.class), 
            		  "%" + userCnee.getCneeName() + "%")); 
            	  }
            	  if (StringUtils.isNoneBlank(userCnee.getCneeAddress())) { 
            		  predicate.add(cb.like(root.get("cneeAddress").as(String.class), 
            				  "%" + userCnee.getCneeAddress() + "%")); 
            	  }
        		  Predicate[] pre = new Predicate[predicate.size()];
            	  return query.where(predicate.toArray(pre)).getRestriction(); 
              }  
          };  
          return userCneeRepository.findAll(spec, pageable);  
    }

    @Override
    @Transactional
    public void batch(List<UserCnee> userCnees) {
    	userCneeRepository.save(userCnees);
    }
    
    @Override
    @Transactional
    public List<UserCnee> selectList(List<String> ids) {
    	return (List<UserCnee>) userCneeRepository.findAll(ids);
    }


}
