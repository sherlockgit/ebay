package com.winit.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.winit.dataobject.OrderError;

/**
 * Created by liyou
 * 2017-06-11 18:23
 */
public interface OrderErrorService {

    /** 订单异常保存. */
	OrderError save(OrderError orderErrorRc);

    /** 查询单个异常订单. */
	OrderError findOne(Long id);
    
    
    /**查询异常订单详细信息列表分页    yyl*/
    Page<OrderError> findAll(final OrderError orderError,Pageable pageable);
    
    
    /** 订单异常删除. */
    public void delete(OrderError  orderError);

}
