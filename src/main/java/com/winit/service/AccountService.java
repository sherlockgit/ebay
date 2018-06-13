package com.winit.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.winit.dataobject.Account;
import com.winit.dataobject.AccountWithdraw;

/**
 * Created by yhy
 * 2017-10-22 18:23
 */
public interface AccountService {

    /** 创建 */
	Account create(Account account);
	
	/** 查询单条信息 */
	Account selectOne(String id);
	
	/**账户**/
	Account findByUserId(String userId);
	
	/**账户列表**/
    Page<Account> findPage(final Account account,List<String> userIds,Date startDate,Date endDate,Pageable pageable);
    
    /***体现列表*****/
    Page<AccountWithdraw> findAWPage(final AccountWithdraw accountWithdraw,List<String> userIds,Pageable pageable);
    
    /****体现审核****/
    AccountWithdraw editWithdraw(AccountWithdraw accountWithdraw);
    
    /****体现详细信息****/
    AccountWithdraw withdrawSelectOne(String id);
}