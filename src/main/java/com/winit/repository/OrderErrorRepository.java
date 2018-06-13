package com.winit.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.winit.dataobject.OrderError;


/**
 * Created by liyou
 * 2017-05-09 11:39
 */
public interface OrderErrorRepository extends JpaRepository<OrderError, Long> {

    
    Page<OrderError> findAll(Specification<OrderError> orderError,Pageable pageable);

    OrderError findByOrderNo(String orderNo);
}
