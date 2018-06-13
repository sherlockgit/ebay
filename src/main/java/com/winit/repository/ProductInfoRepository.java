package com.winit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.winit.dataobject.ProductInfo;


/**
 * Created by liyou
 * 2017-05-09 11:39
 */
public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {

    List<ProductInfo> findByProductStatus(String productStatus);
    
    Page<ProductInfo> findByUserWxOpenid(String userWxOpenid,Pageable pageable);

    Page<ProductInfo> findByUserWxOpenidAndAuditStatus(Pageable pageable, String userWxOpenid,String auditStatus);

    List<ProductInfo> findAll(Specification<ProductInfo> productInfo);
    
    Page<ProductInfo> findByProductStatus(String productStatus,Pageable pageable);
    
    Page<ProductInfo> findAll(Specification<ProductInfo> productInfo,Pageable pageable);
    /* 通过译者微信号查找商品信息**/
    List<ProductInfo> findByUserWxOpenid(String userWxOpenid);
}
