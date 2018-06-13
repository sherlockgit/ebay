package com.winit.service;

import java.util.List;

import com.winit.dataobject.GoodCarVO;

/**
 * 购物车
 * Created by liyou
 * 2017-06-22 00:11
 */
public interface GoodsCarService {

    //保存购物车信息
    void save(GoodCarVO orderDetail,String openId);

    //查询购物车信息，分页
    List <GoodCarVO> findByOpenId(String openid, long start, long end);
    
    void delete(String productId,String openId);
}
