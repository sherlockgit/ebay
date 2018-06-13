package com.winit.service;

import com.winit.dataobject.BannerInfo;
import com.winit.dataobject.ProductCategory;

import java.util.List;

/**
 * 类目
 * Created by liyou
 * 2017-05-09 10:12
 */
public interface BannerInfoService {

    BannerInfo findOne(Integer categoryId);

    List<BannerInfo> findAll();

    List<BannerInfo> findByIsValid(String isValid);

    BannerInfo save(BannerInfo bannerInfo);
}
