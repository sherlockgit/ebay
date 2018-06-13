package com.winit.repository;

import com.winit.dataobject.OrderMaster;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by liyou
 * 2017-06-11 17:24
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster, Long> {

    Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);
    
    Page<OrderMaster> findByOrderNo(String orderNo, Pageable pageable);
    
    List<OrderMaster>  findByOrderNo(String orderNo);
    
    Page<OrderMaster> findAll(Specification<OrderMaster> orderMaster, Pageable pageable);

    List<OrderMaster> findAll(Specification<OrderMaster> orderMaster);
    
    /**
     *通过买家微信号查询订单
     * @param buyerOpenid
     * @return
     */
    List<OrderMaster> findByBuyerOpenid(String buyerOpenid);
    
}
