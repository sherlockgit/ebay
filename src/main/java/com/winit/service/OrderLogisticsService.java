package com.winit.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.winit.dataobject.OrderLogistics;

/**
 * Created by liyou
 * 2017-06-11 18:23
 */
public interface OrderLogisticsService {

    /** 保存订单物流信息. */
    OrderLogistics save(OrderLogistics orderLogistics);
    
    /** 删除订单物流信息. */
    void delete(OrderLogistics orderLogistics);

    /** 查询单个订单物流信息. */
    OrderLogistics findOne(Long id);

    /** 查询订单物流信息列表带分页. */
    Page<OrderLogistics> findListPage(OrderLogistics orderLogistics, Pageable pageable);

    /** 查询订单物流信息列表. */
    List<OrderLogistics> findList(OrderLogistics orderLogistics);
    
    /** 根据orderNo查询订单物流信息. */
    List<OrderLogistics> findByOrderNoList(List<String> orderNos);

}
