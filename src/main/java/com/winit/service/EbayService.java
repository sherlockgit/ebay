package com.winit.service;

import com.alibaba.fastjson.JSONObject;

public interface EbayService {

    public JSONObject getCompactGoods(String itemId);

    public JSONObject getGoodsInfo(String itemId);

    /**
     * 获取商品信息
     * @param id
     * @return
     */
    public JSONObject getItemInfoById(String id);

    public JSONObject createEbayOrder(String orderNo);

}
