package com.winit.service.impl;

import com.google.common.collect.Lists;
import com.winit.dataobject.Account;
import com.winit.dataobject.AccountItem;
import com.winit.dataobject.Distribution;
import com.winit.dataobject.User;
import com.winit.repository.DistributionRepository;
import com.winit.service.AccountItemService;
import com.winit.service.AccountService;
import com.winit.service.DistributionService;
import com.winit.service.UserService;
import com.winit.utils.BeanUtilEx;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
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
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by yhy
 * 2017-10-22 18:43
 */
@Service
@Slf4j
public class DistributionServiceImpl implements DistributionService {

    @Autowired
    private DistributionRepository distributionRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountItemService accountItemService;
    
    @Override
    @Transactional
    public String createNew(Distribution distribution,Distribution dtemp) {
    	//审核通过  账户
		if("2".equals(distribution.getAuditStatus()) && "0".equals(dtemp.getAuditStatus())){
			if(null  != dtemp.getFirstCommission()){
				User first_user = userService.selectByUserWxOpenid(dtemp.getFirstWxName());
				if(null == first_user){
					return "一级分销户微信名称不存在。";
				}
				Account first_account = accountService.findByUserId(first_user.getId());
				if(null == first_account){
					return "一级分销户账户不存在";
				}
				BigDecimal userDr = (null==first_account.getUserBalance())?(new BigDecimal(0)):first_account.getUserBalance();
				userDr = userDr.add(dtemp.getFirstCommission());
				first_account.setUserBalance(userDr);
				first_account.setUpdatedby(distribution.getUpdatedby());
				first_account.setUpdated(new Date());
				accountService.create(first_account);
				
				AccountItem accountItem_first = new AccountItem();
				accountItem_first.setUpdatedby(distribution.getUpdatedby());
				accountItem_first.setUserId(first_user.getId());
				accountItem_first.setTradeNo(dtemp.getOrderNo());
				accountItem_first.setTradeType("1");
				accountItem_first.setTradeStatus("1");
				accountItem_first.setTradeInAmount(dtemp.getFirstCommission().toString());
				accountItemService.create(accountItem_first);
			}
			
			if(null != dtemp.getSecondCommission() && dtemp.getSecondCommission().compareTo(BigDecimal.ZERO) == 1){
				User second_user = userService.selectByUserWxOpenid(dtemp.getSecondWxName());
				if(null == second_user){
					return "二级分销户微信名称不存在";
				}
				Account second_account = accountService.findByUserId(second_user.getId());
				if(null == second_account){
					return "二级分销户账户不存在";
				}
				BigDecimal userDr = (null==second_account.getUserBalance())?(new BigDecimal(0)):second_account.getUserBalance();
				userDr = userDr.add(dtemp.getSecondCommission());
				second_account.setUserBalance(userDr);
				second_account.setUpdatedby(distribution.getUpdatedby());
				second_account.setUpdated(new Date());
				accountService.create(second_account);
				//添加账户收入记录
				AccountItem accountItem_second = new AccountItem();
				accountItem_second.setUpdatedby(distribution.getUpdatedby());
				accountItem_second.setUserId(second_user.getId());
				accountItem_second.setTradeNo(dtemp.getOrderNo());
				accountItem_second.setTradeType("1");
				accountItem_second.setTradeStatus("1");
				accountItem_second.setTradeInAmount(dtemp.getSecondCommission().toString());
				accountItemService.create(accountItem_second);
			}
		}
    	
    	BeanUtilEx.copyPropertiesIgnoreNull(distribution,dtemp);
    	distributionRepository.save(dtemp);
        return null;
    }
    
    @Override
    @Transactional
    public Distribution create(Distribution distribution) {
    	distribution.setAuditStatus("0");
    	distributionRepository.save(distribution);
        return distribution;
    }
    
    @Override
    public Distribution selectOne(Long id) {
    	return distributionRepository.findOne(id);
    }
    
  
    @Override
    public Page<Distribution> findPage(final Distribution distributio,Pageable pageable) {
          Specification<Distribution> spec = new Specification<Distribution>() {  
              public Predicate toPredicate(Root<Distribution> root,  
                      CriteriaQuery<?> query, CriteriaBuilder cb) {
            	  List<Predicate> predicate = Lists.newArrayList();
            	  //默认查询有效，未删除的
            	  predicate.add(cb.equal(root.get("isDelete").as(String.class), 
            			  "N"));
            	  if (StringUtils.isNoneBlank(distributio.getOrderNo())) { 
            		  predicate.add(cb.equal(root.get("orderNo").as(String.class), 
            				  distributio.getOrderNo())); 
            	  }
            	  //审核状态
            	  if (StringUtils.isNoneBlank(distributio.getAuditStatus())) { 
            		  predicate.add(cb.equal(root.get("auditStatus").as(String.class), 
            				  distributio.getAuditStatus())); 
            	  }
            	  if (StringUtils.isNoneBlank(distributio.getProductName())) { 
            		  predicate.add(cb.like(root.get("productName").as(String.class), 
            				  "%" + distributio.getProductName() + "%")
            				  );
    	       	  }
            	  if (StringUtils.isNoneBlank(distributio.getBuyUserName())) {
            		  Predicate p = cb.or(cb.like(root.get("buyUserName").as(String.class), 
  				  			"%" + distributio.getBuyUserName() + "%"),
     				  cb.like(root.get("firstDistName").as(String.class), 
         	       		  "%" + distributio.getBuyUserName() + "%"),
     				   cb.like(root.get("secondDistName").as(String.class), 
	           	       		  "%" + distributio.getBuyUserName() + "%"));
            		  p = cb.and(p);
            		  predicate.add(p);
            	  }
        		  Predicate[] pre = new Predicate[predicate.size()];
            	  return query.where(predicate.toArray(pre)).getRestriction(); 
              }  
          };  
          return distributionRepository.findAll(spec, pageable);  
    }
    /**根据订单号查询分销数据**/
	public List<Distribution> findByOrderNo(String orderNo){
		
		return distributionRepository.findByOrderNo(orderNo);
	}
}
