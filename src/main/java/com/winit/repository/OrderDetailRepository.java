package com.winit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.winit.dataobject.OrderDetail;

/**
 * Created by liyou
 * 2017-06-11 17:28
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(String orderId);
    
    List<OrderDetail> findAll(Specification<OrderDetail> orderDetail);


    Page<OrderDetail> findAll(Specification<OrderDetail> orderDetail,Pageable pageable);


}
