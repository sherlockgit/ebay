package com.winit.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.winit.dataobject.*;
import com.winit.form.OrderListFrom;
import com.winit.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.data.repository.support.PageableExecutionUtils.TotalSupplier;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.lly835.bestpay.enums.AlipayRefundStatusEnum;
import com.winit.VO.OrderListVO;
import com.winit.VO.ResultVO;
import com.winit.converter.OrderForm2OrderDTOConverter;
import com.winit.dto.OrderDTO;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.form.OrderForm;
import com.winit.service.OrderLogisticsService;
import com.winit.service.OrderService;
import com.winit.service.UserService;
import com.winit.utils.ResultVOUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 卖家端订单
 * Created by liyou
 * 2017-07-16 17:24
 */
@RestController
@RequestMapping("/seller/order")
@Slf4j
public class SellerOrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderLogisticsService orderLogisticsService;

	/**
	 * 订单列表
	 * @param page 第几页, 从1页开始
	 * @param size 一页有多少条数据
	 * @return1
	 */
	@GetMapping("/list")
	public  ResultVO list(
			@ApiParam(value = "订单状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成 6:已评价 7:退款中 8:已退款 9：已删除）")
			@RequestParam(required = false, value = "orderStatus", defaultValue = "") String orderStatus,
			@ApiParam(value = "订单编号") @RequestParam(required = false, value = "orderNo", defaultValue = "") String orderNo,
			@ApiParam(value = "买家名字") @RequestParam(required = false, value = "buyerName", defaultValue = "") String buyerName,
			@ApiParam(value = "买家电话") @RequestParam(required = false, value = "buyerPhone", defaultValue = "") String buyerPhone,
			@ApiParam(value = "买家地址") @RequestParam(required = false, value = "buyerAddress", defaultValue = "") String buyerAddress,
			@ApiParam(value = "微信号(OPENID)") @RequestParam(required = false, value = "userWxOpenid", defaultValue = "") String userWxOpenid,
			@ApiParam(value = "支付状态, 默认1:未支付") @RequestParam(required = false, value = "payStatus", defaultValue = "") Byte payStatus,
			@ApiParam(value = "EBAY订单号") @RequestParam(required = false, value = "ebayNo", defaultValue = "") String ebayNo,
			@ApiParam(value = "EBAY状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成-海外仓已签收）") @RequestParam(required = false, value = "ebayStatus", defaultValue = "") String ebayStatus,
			@ApiParam(value = "物流状态（0:海外已入库 1:海外已出仓 3:清关中 4:派送中 5:已签收）") @RequestParam(required = false, value = "logisticsStatus", defaultValue = "") String logisticsStatus,
			@ApiParam(value = "收货人姓名") @RequestParam(required = false, value = "cneeName", defaultValue = "") String cneeName,
			@ApiParam(value = "收货人号码") @RequestParam(required = false, value = "cneePhone", defaultValue = "") String cneePhone,
			@ApiParam(value = "收货人身份证") @RequestParam(required = false, value = "cneeIdcard", defaultValue = "") String cneeIdcard,
			@ApiParam(value = "收货人地址") @RequestParam(required = false, value = "cneeAddress", defaultValue = "") String cneeAddress,
			@ApiParam(value = "0-查询自己订单，1-查询分销商订单") @RequestParam(required = false, value = "isContainDistributor", defaultValue = "0") Integer isContainDistributor,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		PageRequest request = new PageRequest(page, size);
		OrderMaster orderMaster = new OrderMaster();
		orderMaster.setOrderStatus(orderStatus);
		orderMaster.setOrderNo(orderNo);
		orderMaster.setBuyerPhone(buyerPhone);
		orderMaster.setBuyerAddress(buyerAddress);
		orderMaster.setBuyerOpenid(userWxOpenid);
		orderMaster.setPayStatus(payStatus);
		orderMaster.setEbayNo(ebayNo);
		orderMaster.setEbayStatus(ebayStatus);
		orderMaster.setCneeName(cneeName);
		orderMaster.setCneePhone(cneePhone);
		orderMaster.setCneeIdcard(cneeIdcard);
		orderMaster.setCneeAddress(cneeAddress);
		orderMaster.setPayStatus(payStatus);
		
		List<String> orderNos  = Lists.newArrayList();
		List<String> openIds = Lists.newArrayList();
		if(!StringUtils.isEmpty(userWxOpenid)){
			openIds.add(userWxOpenid);
		}
		//1.查询出订单列表
		List<OrderMaster> orderMasters  =  orderService.findOrderMasterList(orderMaster);
		if(!CollectionUtils.isEmpty(orderMasters)){
			 for (OrderMaster orderMasterTemp:orderMasters){
				 orderNos.add(orderMasterTemp.getOrderNo());
			 }
		}
		// isContainDistributor 是否包含分销商订单：0-包含,1-不包含
		// 查询分销商订单信息
		List<String> distributedOrderNos = Lists.newArrayList();

		//2.根据openid查询分销商订单编号
		if (isContainDistributor == 1) {
			
			distributedOrderNos = orderService.findDistributedOderIdsByOpenIds(openIds);
			
			orderNos = distributedOrderNos;
		}
		if(CollectionUtils.isEmpty(orderNos)){
			return ResultVOUtil.success(Lists.newArrayList());
		}
		Page<OrderMaster> orderMasterPage =orderService.findOrderMasterByOrderNos(orderNos, orderStatus, request);
		//return ResultVOUtil.success(orderMasterPage);
		// 查询分销商订单信息
		List<OrderDTO> orderList = Lists.newArrayList();
		
		//组合返回结果
		if(orderMasterPage !=null && !CollectionUtils.isEmpty(orderMasterPage.getContent())){
			for (OrderMaster orderMasterTemp : orderMasterPage.getContent()){
				OrderDTO orderDTOTemp  = new OrderDTO();
				BeanUtils.copyProperties(orderMasterTemp, orderDTOTemp);
				orderDTOTemp.setOrderDetailList(orderService.findByOrderId(orderMasterTemp.getOrderNo()));
				orderList.add(orderDTOTemp);
			}
		}
		
		
		return ResultVOUtil.success(new PageImpl<OrderDTO>(orderList, request, orderMasterPage.getTotalElements()));
	}
	/**
	 * 后台管理：订单列表查询
	 * @param userCtype
	 * @param orderNo
	 * @param productName
	 * @param buyerName
	 * @param orderStatus
	 * @param ebayStatus
	 * @param page
	 * @param size
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "后台管理：订单列表查询")
	@GetMapping("/orderlistShow")
	public ResultVO<List<?>>  orderlistShow(
			@ApiParam(value = "用户类型【1:分销商、2:普通用户】") @RequestParam(required = false, value = "userCtype", defaultValue = "") String userCtype,
			@ApiParam(value = "订单编号") @RequestParam(required = false, value = "orderNo", defaultValue = "") String orderNo,
			@ApiParam(value="商品名称")@RequestParam(required = false, value = "productName", defaultValue = "") String productName,
			@ApiParam(value = "买家名字") @RequestParam(required = false, value = "buyerName", defaultValue = "") String buyerName,
			@ApiParam(value = "手机号码") @RequestParam(required = false, value = "buyerPhone", defaultValue = "") String buyerPhone,
			@ApiParam(value = "订单状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成 6:已评价 7:退款中 8:已退款 9：已删除）") @RequestParam(required = false, value = "orderStatus", defaultValue = "") String orderStatus,
			@ApiParam(value = "EBAY状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成-海外仓已签收）") @RequestParam(required = false, value = "ebayStatus", defaultValue = "") String ebayStatus,
			@ApiParam(value = "微信号(OPENID)") @RequestParam(required = false, value = "userWxOpenid", defaultValue = "") String userWxOpenid,
			@ApiParam(value = "0-查询自己订单，1-查询分销商订单") @RequestParam(required = false, value = "isContainDistributor", defaultValue = "0") Integer isContainDistributor,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size, HttpServletResponse response){

		response.setHeader("Access-Control-Allow-Origin","*");
		Sort sort = new Sort(Sort.Direction.DESC,"createTime");
		PageRequest request = new PageRequest(page, size, sort);
		OrderMaster orderMaster = new OrderMaster();
		orderMaster.setOrderNo(orderNo);
		orderMaster.setProductName(productName);
		orderMaster.setBuyerName(buyerName);
		orderMaster.setOrderStatus(orderStatus);
		orderMaster.setEbayStatus(ebayStatus);
		orderMaster.setBuyerPhone(buyerPhone);
		orderMaster.setBuyerOpenid(userWxOpenid);
		System.out.println(orderStatus);
		User user = new User();
		user.setUserCtype(userCtype);
		boolean isTrue = false;
		List<User> userList = Lists.newArrayList();
		if(StringUtils.isNoneBlank(userCtype)){
			isTrue=true;
			user.setUserCtype(userCtype);
		}
		if(StringUtils.isNoneBlank(buyerName)){
			isTrue=true;
			user.setUserName(buyerName);
		}
		//获取userId集合
		List<String> buyerNames = Lists.newArrayList();
		if(isTrue){
			userList =  userService.selectList(user, null);
			for (User user2 : userList) {
				buyerNames.add(user2.getUserName());
			}
		}

		//查询订单信息
		List<OrderMaster> orderMasterList = orderService.findOrderMasterByNameList(orderMaster,buyerNames);
		
		//获取orderId集合
		List<String> orderIds = Lists.newArrayList();
		List<String> openIds = Lists.newArrayList();
		
		if(!StringUtils.isEmpty(userWxOpenid)){
			openIds.add(userWxOpenid);
		}
		if(!StringUtils.isEmpty(userWxOpenid)){
			openIds.add(userWxOpenid);
		}
		if(!CollectionUtils.isEmpty(orderMasterList)){
			for(OrderMaster orderMasterVO:orderMasterList){
				orderIds.add(orderMasterVO.getOrderNo());
			}
		}
		
		//isContainDistributor 0-查询自己订单，1-查询分销商订单
		//查询分销商订单信息
		List<String>  distributedOrderNos  = Lists.newArrayList();
		
		if(isContainDistributor==1){
			
			distributedOrderNos  = orderService.findDistributedOderIdsByOpenIds(openIds);
			orderIds = distributedOrderNos ;
		}
		if(CollectionUtils.isEmpty(orderIds)){
			Page<OrderListVO> orderListVOPage  =PageableExecutionUtils.getPage(Lists.newArrayList(),request,new TotalSupplier(){
				@Override
				public long get() {
					return 0L;
				}
			});
			return ResultVOUtil.success(orderListVOPage);
		}
		//查询详情
		Page<OrderDetail> orderDetailList =orderService.findOrderDetailByOrderNoList(orderIds, request);
		
		//获取分页之后的orderNo
		List<String> orderNoPages = Lists.newArrayList();
		//页面对象数据
		List<OrderListVO> orderList = Lists.newArrayList();
		if(orderDetailList!=null){
			if(!CollectionUtils.isEmpty(orderDetailList.getContent())){
				for(OrderDetail orderDetailVO:orderDetailList.getContent()){
					orderNoPages.add(orderDetailVO.getOrderId());
					OrderListVO orderListVO = new OrderListVO();
					BeanUtils.copyProperties(orderDetailVO, orderListVO);
					orderListVO.setOrderDetailId(orderDetailVO.getId());
					orderListVO.setCreated(orderDetailVO.getCreateTime());
					orderListVO.setOrderNo(orderDetailVO.getOrderId());
					orderList.add(orderListVO);
				}
			}
		}
		//查询物流订单号
		List<OrderLogistics> orderLogisticsList = orderLogisticsService.findByOrderNoList(orderNoPages);
		// 组装页面数据
		if (!CollectionUtils.isEmpty(orderList)) {
			for (OrderListVO orderListVO : orderList) {
				// 计算订单总价
				BigDecimal orderAmount = orderListVO.getProductPrice()
						.multiply(new BigDecimal(orderListVO.getProductQuantity()));
				orderListVO.setOrderAmount(orderAmount);
				// 组装主表数据
				if (!CollectionUtils.isEmpty(orderMasterList)) {
					for (OrderMaster orderMasterVO : orderMasterList) {
						if (orderMasterVO.getOrderNo().equals(orderListVO.getOrderNo())) {
							orderListVO.setId(orderMasterVO.getId());
							orderListVO.setOrderStatus(orderMasterVO.getOrderStatus());
							orderListVO.setEbayNo(orderMasterVO.getEbayNo());
							orderListVO.setEbayStatus(orderMasterVO.getEbayStatus());
							orderListVO.setBuyerName(orderMasterVO.getBuyerName());
							orderListVO.setBuyerPhone(orderMasterVO.getBuyerPhone());
							orderListVO.setOrderGroupNo(orderMasterVO.getOrderGroupNo());
						}
					}
				}
				// 组装物流信息数据
				if (!CollectionUtils.isEmpty(orderLogisticsList)) {
					for (OrderLogistics orderLogisticsVo : orderLogisticsList) {
						if (orderLogisticsVo.getOrderNo().equals(orderListVO.getOrderNo())) {
						}
					}
				}
				//组装订单详情信息
				orderListVO.setOrderDetailList(orderService.findByOrderId(orderListVO.getOrderNo()));
			}
		}
		Page<OrderListVO> orderListVOPage  =PageableExecutionUtils.getPage(orderList,request,new TotalSupplier(){
			@Override
			public long get() {
				return orderDetailList.getTotalElements();
			}
		});
		return ResultVOUtil.success(orderListVOPage);
	}

	/**
	 * 取消订单
	 * @param orderId
	 * @return
	 */
	@PutMapping("/cancel/{openid}/{orderId}")
	public ResultVO cancel(@PathVariable("openid") String openid,
						   @PathVariable("orderId") String orderId,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setHeader("Access-Control-Allow-Origin","*");
		try {
			orderService.cancel(openid, orderId);
			OrderMaster orderMaster = new OrderMaster();
	        orderMaster.setOrderNo(orderId);
	        orderMaster.setBuyerOpenid(openid);
	        List<OrderMaster> orderMasterList = orderService.findOrderMasterByNameList(orderMaster, null);
	        if(!CollectionUtils.isEmpty(orderMasterList)){
	        	orderMaster =orderMasterList.get(0) ;
	        }
			return ResultVOUtil.success(orderMaster);
		}catch (SellException e){
			return ResultVOUtil.error(ResultEnum.ORDER_STATUS_ERROR.getCode(),ResultEnum.ORDER_STATUS_ERROR.getMessage());
		}


	}

	/**
	 * 订单详情
	 * @param orderId
	 * @return
	 */
	@GetMapping("/detail/{orderId}")
	public ResultVO<OrderDTO>  detail(@PathVariable("orderId") String orderId,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		OrderDTO orderDTO = new OrderDTO();
		try {
			orderDTO = orderService.findOne(orderId);
		}catch (SellException e) {
			log.error("【卖家端查询订单详情】发生异常{}", e);
			return ResultVOUtil.error(ResultEnum.SELLER_ORDER_DETIAL_EXCEPTION.getCode(), e.getMessage());
		}
		return  ResultVOUtil.success(orderDTO);
	}

	/**
	 * 完结订单
	 * @param orderId
	 * @return
	 */
	@PutMapping("/finish/{orderId}")
	public ResultVO finished(@PathVariable("orderId") String orderId,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		try {
			OrderDTO orderDTO = orderService.findOne(orderId);
			orderService.finish(orderDTO);
		} catch (SellException e) {
			log.error("【卖家端完结订单】发生异常{}", e);
			return ResultVOUtil.error(ResultEnum.SELLER_ORDER_FINAL_EXCEPTION.getCode(), e.getMessage());
		}
		return ResultVOUtil.success();
	}

	//修改订单
	@ApiOperation(value = "卖家编辑订单")
	@PutMapping("/update")
	public ResultVO<Map<String, String>> update(@RequestBody OrderListFrom orderListFrom,
											  BindingResult bindingResult,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		if (bindingResult.hasErrors()) {
			log.error("【创建订单】参数不正确, orderForm={}", orderListFrom);
			throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
					bindingResult.getFieldError().getDefaultMessage());
		}
		System.out.println(orderListFrom.toString());
		orderService.update(orderListFrom);
		return ResultVOUtil.success();
	}
	//创建订单
	@ApiOperation(value = "卖家新增订单")
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@RequestBody OrderForm orderForm,
                                                BindingResult bindingResult,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm={}", orderForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
		System.out.println(orderForm.toString());
		OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
		orderDTO.setArrivalTime(DateUtil.getDateByAddDay(15));
        OrderDTO createResult = orderService.create(orderDTO);
		System.out.println("asdasdasd"+createResult.getOrderDetailList().toString());
		Map<String, String> map = new HashMap<>();
        map.put("orderId", createResult.getOrderGroupNo());

        return ResultVOUtil.success(map);
    }
}
