package com.winit.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.winit.dataobject.OrderErrorRc;



/**
 * Created by liyou
 * 2017-05-09 11:39
 */
public interface OrderErrorRcRepository extends JpaRepository<OrderErrorRc, Long> {

    
    Page<OrderErrorRc> findAll(Specification<OrderErrorRc> orderErrorRc,Pageable pageable);
}
