package com.winit.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.winit.VO.ResultVO;
import com.winit.dataobject.GoodCarVO;
import com.winit.dataobject.OrderDetail;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.form.GoodCarForm;
import com.winit.service.GoodsCarService;
import com.winit.utils.ResultVOUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 购物车 Created by liyou 2017-06-18 23:27
 */
@RestController
@RequestMapping("/buyer/goodcar")
@Slf4j
@SuppressWarnings("unchecked")
public class GoodsCarController {

	@Autowired
	private GoodsCarService goodsCarService;

	// 创建订单
	@ApiOperation(value = "添加购物车信息")
	@PostMapping("/add/{userId}")
	public ResultVO<Map<String, String>> create(@RequestBody GoodCarForm goodCarForm,
			@PathVariable("userId") String userId, BindingResult bindingResult, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		if (bindingResult.hasErrors()) {
			log.error("【创建购物车】参数不正确, orderForm={}", goodCarForm);
			throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
					bindingResult.getFieldError().getDefaultMessage());
		}
		GoodCarVO orderDetail = new GoodCarVO();
		BeanUtils.copyProperties(goodCarForm, orderDetail);
		orderDetail.setUpdateTime(null);
		orderDetail.setCreateTime(null);
		goodsCarService.save(orderDetail, userId);
		return ResultVOUtil.success();
	}

	// 订单列表
	@ApiOperation(value = "查询购物车信息")
	@GetMapping("/list/{userId}/{page}/{size}")
	public ResultVO<List<OrderDetail>> list(
			@PathVariable("userId") String userId,
			@PathVariable("page") Integer page,
			@PathVariable("size") Integer size, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");

		if (StringUtils.isEmpty(userId)) {
			log.error("【查询订单列表】userId为空");
			throw new SellException(ResultEnum.PARAM_ERROR);
		}

		Long startPage = Long.parseLong(page.toString());
		Long pageSize = Long.parseLong(size.toString());
		Long start = startPage * pageSize;
		Long end = (startPage + 1) * pageSize;
		List<GoodCarVO> orderDetailList = goodsCarService.findByOpenId(userId, start, end);

		return ResultVOUtil.success(orderDetailList, orderDetailList.size());
	}


	// 删除购物车
	@DeleteMapping("/delete/{productId}/{userId}")
	@ApiOperation(value = "删除购物车信息")
	public ResultVO<Map<String, String>> delete(@PathVariable("productId") String productId,
												@PathVariable("userId") String userId,
												@ApiParam(value = "eaby商品id") @RequestParam(required = false, value = "itemId", defaultValue = "") String itemId,
												HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		if(StringUtils.isEmpty(itemId)){
			itemId="group0";
		}
		productId += ":"+itemId ;
		
		goodsCarService.delete(productId, userId);
		return ResultVOUtil.success();
	}
}
