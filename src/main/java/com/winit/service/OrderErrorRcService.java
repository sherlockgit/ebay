package com.winit.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.winit.dataobject.OrderErrorRc;

/**
 * Created by liyou
 * 2017-06-11 18:23
 */
public interface OrderErrorRcService {

    /** 订单异常轨迹保存. */
	OrderErrorRc save(OrderErrorRc orderErrorRc);

    /** 查询单个异常轨迹订单. */
	OrderErrorRc findOne(Long id);
    
    
    /**查询异常轨迹订单详细信息列表分页    yyl*/
    Page<OrderErrorRc> findAllPage(final OrderErrorRc orderErrorRc,Pageable pageable);
    
    /** 订单异常轨迹删除. */
    public void delete(OrderErrorRc OrderErrorRc);

}
