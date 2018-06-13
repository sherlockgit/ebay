package com.winit.service;

import com.winit.dataobject.ProductCategory;

import java.util.List;

/**
 * 类目
 * Created by liyou
 * 2017-05-09 10:12
 */
public interface CategoryService {

    ProductCategory findOne(Integer categoryId);

    List<ProductCategory> findAll();

    List<ProductCategory> findByPid(String pid);

    ProductCategory save(ProductCategory productCategory);
}
