package com.winit.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.winit.dataobject.Distribution;

/**
 * Created by yhy
 * 2017-10-22 18:23
 */
public interface DistributionService {

    /** 创建 */
	String createNew(Distribution distribution,Distribution dtemp);
	
	/** 创建 */
	Distribution create(Distribution distribution);
	
	/** 查询单条信息 */
	Distribution selectOne(Long id);
	
//	/**账户收入**/
//	List<Distribution> findAll(Distribution distribution,List<String> userIds,Date startDate,Date endDate);
	
	/**分销列表**/
	Page<Distribution> findPage(final Distribution distribution,Pageable pageable);
	
	/**根据订单号查询分销数据**/
	List<Distribution> findByOrderNo(String orderNo);
}