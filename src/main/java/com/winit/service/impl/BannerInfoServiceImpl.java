package com.winit.service.impl;

import com.winit.dataobject.BannerInfo;
import com.winit.dataobject.ProductCategory;
import com.winit.repository.BannerInfoRepository;
import com.winit.repository.ProductCategoryRepository;
import com.winit.service.BannerInfoService;
import com.winit.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  Banner
 * Created by liyou
 * 2017-05-09 10:16
 */
@Service
public class BannerInfoServiceImpl implements BannerInfoService {

    @Autowired
    private BannerInfoRepository repository;

    @Override
    public BannerInfo findOne(Integer id) {
        return repository.findOne(id);
    }

    @Override
    public List<BannerInfo> findAll() {
        return repository.findAll();
    }

    @Override
    public List<BannerInfo> findByIsValid(String isValid) {
        return repository.findByIsValid(isValid);
    }

    @Override
    public BannerInfo save(BannerInfo bannerInfo) {
        return repository.save(bannerInfo);
    }
}
