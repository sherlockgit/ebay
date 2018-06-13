package com.winit.repository;

import com.winit.dataobject.BannerInfo;
import com.winit.dataobject.ProductCategory;
import org.springframework.boot.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by liyou
 * 2017-05-07 14:35
 */
public interface BannerInfoRepository extends JpaRepository<BannerInfo, Integer> {

    List<BannerInfo> findByIsValid(String isValid);

}
