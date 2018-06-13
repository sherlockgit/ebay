package com.winit.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.winit.dataobject.ProductAttr;

/**
 * Created by yeyalin
 * 2017-10-28 14:35
 */
public interface ProductAttrRepository extends JpaRepository<ProductAttr, Long> {

    List<ProductAttr> findByProductId(Long productId);
    List<ProductAttr> findAll(Specification<ProductAttr> productAttr);
    long  deleteByProductId(Long productId);
}
