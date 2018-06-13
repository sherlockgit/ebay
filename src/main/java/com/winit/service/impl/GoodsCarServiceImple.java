package com.winit.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.lly835.bestpay.utils.JsonUtil;
import com.winit.constant.RedisConstant;
import com.winit.dataobject.GoodCarVO;
import com.winit.dataobject.OrderDetail;
import com.winit.service.GoodsCarService;

/**
 * 购物车 Created by yyl 2017-11-01 02:11
 */
@Service
public class GoodsCarServiceImple implements GoodsCarService {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public void save(GoodCarVO orderDetail, String openId) {
		ZSetOperations<String, String> redisList = stringRedisTemplate.opsForZSet();

		String key = "userid:" + openId;
		String itemId = orderDetail.getItemId();
		if(StringUtils.isEmpty(itemId)){
			itemId="group0";
		}
		String productId = orderDetail.getProductId().toString();
		productId += ":"+itemId ;
		Set<TypedTuple<String>> productIds = redisList.rangeWithScores(key, 0, -1);

		boolean flag = true;
		if (!CollectionUtils.isEmpty(productIds)) {
			for (TypedTuple<String> id : productIds) {
				
				if(productId.equals(id.getValue())){
					
					flag = false;
					
					break;
				}
			}


		}
		if(flag){
			
			redisList.add(key, productId, System.currentTimeMillis());
			
			stringRedisTemplate.expire(key, RedisConstant.GOOD_CAR_EXPIRE, TimeUnit.SECONDS);
		}

		String goodsCars = stringRedisTemplate.opsForValue().get(key + ":" + productId);

		if (!StringUtils.isEmpty(goodsCars)) {

			GoodCarVO orgOrderDetail = JsonUtil.toObject(goodsCars, GoodCarVO.class);
			orderDetail.setProductQuantity(orderDetail.getProductQuantity() + orgOrderDetail.getProductQuantity());
		}
		saveGoodsCar(key + ":" + productId, orderDetail);

	}

	/**
	 * 保存购物车到redis
	 * 
	 * @param key
	 * @param orderDetail
	 */
	private void saveGoodsCar(String key, GoodCarVO orderDetail) {
		Gson gson = new Gson();
		stringRedisTemplate.opsForValue().set(key, gson.toJson(orderDetail), RedisConstant.GOOD_CAR_EXPIRE,
				TimeUnit.SECONDS);

	}

	@Override
	public List<GoodCarVO> findByOpenId(String openId, long start, long end) {
		ZSetOperations<String, String> redisList = stringRedisTemplate.opsForZSet();
		List<GoodCarVO> orderDetails = new ArrayList<>();
		String key = "userid:" + openId;
		Set<TypedTuple<String>> productIds = redisList.rangeWithScores(key, start, end);
		if (CollectionUtils.isEmpty(productIds)) {
			return orderDetails;
		}

		for (TypedTuple<String> productId : productIds) {
			String goodsCars = stringRedisTemplate.opsForValue().get(key + ":" + productId.getValue());
			if (!StringUtils.isEmpty(goodsCars)) {
				GoodCarVO orderDetail = JsonUtil.toObject(goodsCars, GoodCarVO.class);
				orderDetails.add(orderDetail);
			}
		}

		return orderDetails;
	}

	@Override
	public void delete(String productId, String openId) {
		String key = "userid:" + openId;
		ZSetOperations<String, String> redisList = stringRedisTemplate.opsForZSet();
		redisList.remove(key, productId);
		stringRedisTemplate.delete(key + ":" + productId);
	}

}
