package com.winit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.winit.dataobject.OrderLogistics;

/**
 * Created by liyou
 * 2017-06-11 17:28
 */
public interface OrderLogisticsRepository extends JpaRepository<OrderLogistics, Long> {

    List<OrderLogistics> findByOrderNo(String orderNo);
    
    List<OrderLogistics> findAll(Specification<OrderLogistics> orderLogistics);
    
    Page<OrderLogistics> findAll(Specification<OrderLogistics> orderLogistics,Pageable pageable);
}
