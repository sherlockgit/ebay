package com.winit.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.winit.dataobject.AccountCheck;
import com.winit.dataobject.AccountItem;

/**
 * Created by yhy
 * 2017-10-22 18:23
 */
public interface AccountItemService {

    /** 创建 */
	AccountItem create(AccountItem accountItem);
	
	/** 查询单条信息 */
	AccountItem selectOne(String id);
	
	/**账户收入**/
	List<AccountItem> findAll(AccountItem accountItem,List<String> userIds,List<String> tradeNoList,Date startDate,Date endDate);
	
	/**用户收入支出分页**/
	Page<AccountItem> findPage(final AccountItem accountItem,Date startDate,Date endDate,Pageable pageable);
	
	Page<AccountCheck> findCheckPage(final AccountCheck accountCheck,Date startDate,Date endDate,Pageable pageable);
	
	//***查询对账信息****/
	AccountCheck selectOneCheck(String id);
	
	/**处理对象信息***/
	AccountCheck editCheck(AccountCheck accountCheck);
}