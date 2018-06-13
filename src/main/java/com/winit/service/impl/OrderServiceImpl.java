package com.winit.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.data.repository.support.PageableExecutionUtils.TotalSupplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.winit.converter.OrderMaster2OrderDTOConverter;
import com.winit.dataobject.OrderDetail;
import com.winit.dataobject.OrderMaster;
import com.winit.dataobject.ProductAttr;
import com.winit.dataobject.ProductInfo;
import com.winit.dto.CartDTO;
import com.winit.dto.OrderDTO;
import com.winit.dto.OrderDetailDTO;
import com.winit.enums.OrderStatusEnum;
import com.winit.enums.PayStatusEnum;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.form.OrderListFrom;
import com.winit.repository.OrderDetailRepository;
import com.winit.repository.OrderMasterRepository;
import com.winit.service.OrderService;
import com.winit.service.PayService;
import com.winit.service.ProductAttrService;
import com.winit.service.ProductService;
import com.winit.service.PushMessageService;
import com.winit.service.WebSocket;
import com.winit.utils.BeanUtilEx;
import com.winit.utils.KeyUtil;
import com.winit.utils.NumberConvert;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by liyou 2017-06-11 18:43
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderDetailRepository orderDetailRepository;

	@Autowired
	private OrderMasterRepository orderMasterRepository;

	@Autowired
	private PayService payService;

	@Autowired
	private PushMessageService pushMessageService;

	@Autowired
	private WebSocket webSocket;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductAttrService productAttrService;

	@Override
	@Transactional
	public OrderDTO create(OrderDTO orderDTO) {

		String orderGroupNo = KeyUtil.genUniqueKey();


		List<OrderDetailDTO> orderDetalList  = setterOrderInfo(orderDTO);

		List<String> orderList =Lists.newArrayList();
		// 1. 查询商品（数量, 价格）
		for (OrderDetailDTO orderDetailDTO : orderDetalList) {
			
			String orderId = KeyUtil.genUniqueKey();
			BigDecimal orderAmount = orderDetailDTO.getOrderAmount();
			BigDecimal taxFee  =orderDetailDTO.getTaxFee();
			BigDecimal carriageFee  =orderDetailDTO.getCarriageFee();
			String itemId = orderDetailDTO.getItemId();
			
			OrderDetail orderDetail = new OrderDetail();
			
			BeanUtils.copyProperties(orderDetailDTO, orderDetail);
			orderDetail.setOrderId(orderId);
			
			orderDetail.setId(null);
			orderDetailRepository.save(orderDetail);

			//orderAmount = orderAmount.add(orderDTO.getCarriageFee());
			
			// 3. 写入订单数据库（orderMaster和orderDetail）
			OrderMaster orderMaster = new OrderMaster();
			orderDTO.setOrderId(orderId);
			BeanUtils.copyProperties(orderDTO, orderMaster);
			orderMaster.setItemId(itemId);
			orderMaster.setOrderNo(orderId);
			orderMaster.setOrderAmount(orderAmount);
			orderMaster.setOrderStatus(OrderStatusEnum.UNPAID.getCode().toString());
			orderMaster.setPayStatus(NumberConvert.intToByte(PayStatusEnum.WAIT.getCode()));
			orderMaster.setCarriageFee(carriageFee);
			orderMaster.setTaxFee(taxFee);
			orderMaster.setOrderGroupNo(orderGroupNo);
			orderMasterRepository.save(orderMaster);
			
			// 4. 扣库存
//			List<CartDTO> cartDTOList = new ArrayList<CartDTO>();
//			for (OrderDetail e : orderDTO.getOrderDetailList()) {
//				CartDTO cartDTO = new CartDTO(e.getProductId(), e.getProductQuantity());
//				cartDTOList.add(cartDTO);
//			}
//			productService.decreaseStock(cartDTOList);
			orderList.add(orderId);
		}

		// 发送websocket消息
		// webSocket.sendMessage(orderDTO.getOrderId());
		orderDTO.setOrderGroupNo(orderGroupNo);
		
		if(!CollectionUtils.isEmpty(orderList)){
			
			orderDTO.setOrderNo( String.join(",",orderList));
		}
		
		return orderDTO;
	}
	/**
	 * 一个商品一个订单，价格超过2000也要拆单
	 * @param orderDTO
	 * @return
	 */
	public List<OrderDetailDTO> setterOrderInfo(OrderDTO orderDTO) {

		List<OrderDetailDTO> orderDetalList = Lists.newArrayList();
		int  twoHusand =2000;
		// 1. 查询商品（数量, 价格）
		for (OrderDetail orderDetailTemp : orderDTO.getOrderDetailList()) {
			
			OrderDetailDTO orderDetail = new OrderDetailDTO();
			BeanUtils.copyProperties(orderDetailTemp, orderDetail);
			ProductInfo productInfo = productService.findOne(orderDetail.getProductId());
			if (productInfo == null) {
				throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
			}
			BigDecimal productPrice = productInfo.getProductPrice();
			
			//商品组id （itemId），如果有值则为多属性商品，商品价格子表获取
			String itemId = orderDetail.getItemId() ;
			List<ProductAttr> productAttrList = Lists.newArrayList();
			if (!StringUtils.isEmpty(itemId)){
				ProductAttr productAttr =new ProductAttr();
				productAttr.setProductId(orderDetail.getProductId());
				productAttr.setItemId(itemId);
				productAttrList = productAttrService.findAll(productAttr);
			}
			if (!CollectionUtils.isEmpty(productAttrList)) {
				if (!CollectionUtils.isEmpty(productAttrList)) {
					for (ProductAttr tempVO : productAttrList) {
						if ("price".equalsIgnoreCase(tempVO.getAttrEname())) {
							if (!StringUtils.isEmpty(tempVO.getAttrCvalue())) {
								productPrice = new BigDecimal(tempVO.getAttrCvalue());
							}
						}
					}
				}
			}
			BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
			BeanUtils.copyProperties(productInfo, orderDetail);
			orderDetail.setProductPrice(productPrice);
			orderDetail.setItemId(itemId);
			BigDecimal oneHundred = new BigDecimal(100);
			//运费
			BigDecimal carriageFee =new BigDecimal(BigInteger.ZERO);
			if(productInfo.getCarriageFee() !=null){
				carriageFee =productInfo.getCarriageFee();
			}
			//税费
			BigDecimal taxFee =new BigDecimal(BigInteger.ZERO);
			if(productInfo.getTaxFee() !=null){
				taxFee =productInfo.getTaxFee();
			}
			BigDecimal perPrice = productPrice.multiply(new BigDecimal(orderDetail.getProductQuantity()));
			orderAmount = perPrice.add(orderAmount);
					//.add(taxFee).add(carriageFee);
			
			Integer quantity = orderDetail.getProductQuantity();
			
			if(quantity<=1){
				orderDetail.setCarriageFee(carriageFee);
				orderDetail.setTaxFee(taxFee);
				orderDetail.setOrderAmount(orderAmount);
				orderDetail.setProductQuantity(quantity);
				orderDetalList.add(orderDetail);
			}else{
//				if ((productPrice.add(taxFee).add(carriageFee)).intValue() > twoHusand){ 2018-05-22 修订
				if ((productPrice).intValue() > twoHusand){
					
					BigDecimal everyTaxFee=taxFee.divide(new BigDecimal(quantity), 2, BigDecimal.ROUND_HALF_UP);
					BigDecimal everyCarriageFee = carriageFee.divide(new BigDecimal(quantity), 2, BigDecimal.ROUND_HALF_UP);
					BigDecimal everyAmout = orderAmount.divide(new BigDecimal(quantity), 2, BigDecimal.ROUND_HALF_UP);
					
					for (int i=1;i<=quantity;i++){
						OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
						BeanUtilEx.copyPropertiesIgnoreNull(orderDetail, orderDetailDTO);
						orderDetailDTO.setCarriageFee(everyCarriageFee);
						orderDetailDTO.setTaxFee(everyTaxFee);
						orderDetailDTO.setOrderAmount(everyAmout);
						orderDetailDTO.setProductQuantity(i);
						orderDetalList.add(orderDetailDTO);
					}
				}else {
					
					int maxAllowedOrders =  twoHusand * 100 / (productPrice.add(taxFee).add(carriageFee).multiply(oneHundred)).intValue();
					int num = quantity / maxAllowedOrders;
					int remainderNum  = quantity % maxAllowedOrders;
					
					BigDecimal everyTaxFee = taxFee;
					BigDecimal everyCarriageFee = carriageFee;
					BigDecimal everyAmout  = orderAmount ;
					
					if (num==0){
						OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
						BeanUtilEx.copyPropertiesIgnoreNull(orderDetail, orderDetailDTO);
						orderDetailDTO.setCarriageFee(carriageFee);
						orderDetailDTO.setTaxFee(taxFee);
						orderDetailDTO.setOrderAmount(orderAmount);
						orderDetailDTO.setProductQuantity(quantity);
						orderDetailDTO.setProductPrice(productPrice);
						orderDetalList.add(orderDetailDTO);
					}else{
						if(remainderNum >0){
							everyTaxFee=taxFee.divide(new BigDecimal(num+1), 2, BigDecimal.ROUND_HALF_UP);
							everyCarriageFee = carriageFee.divide(new BigDecimal(num+1), 2, BigDecimal.ROUND_HALF_UP);
							everyAmout = orderAmount.divide(new BigDecimal(num+1), 2, BigDecimal.ROUND_HALF_UP);
						}else{
							everyTaxFee=taxFee.divide(new BigDecimal(num+1), 2, BigDecimal.ROUND_HALF_UP);
							everyCarriageFee = carriageFee.divide(new BigDecimal(num+1), 2, BigDecimal.ROUND_HALF_UP);
							everyAmout = orderAmount.divide(new BigDecimal(num+1), 2, BigDecimal.ROUND_HALF_UP);
						}
						
						for (int i=0;i<num;i++){
							OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
							BeanUtilEx.copyPropertiesIgnoreNull(orderDetail, orderDetailDTO);
							orderDetailDTO.setCarriageFee(everyCarriageFee);
							orderDetailDTO.setTaxFee(everyTaxFee);
							orderDetailDTO.setOrderAmount(everyAmout);
							orderDetailDTO.setProductQuantity(maxAllowedOrders);
							orderDetailDTO.setProductPrice(productPrice);
							orderDetalList.add(orderDetailDTO);
						}
						if(remainderNum>0){
							OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
							BeanUtilEx.copyPropertiesIgnoreNull(orderDetail, orderDetailDTO);
							orderDetailDTO.setCarriageFee(everyCarriageFee);
							orderDetailDTO.setTaxFee(everyTaxFee);
							orderDetailDTO.setOrderAmount(everyAmout);
							orderDetailDTO.setProductQuantity(maxAllowedOrders);
							orderDetailDTO.setProductPrice(productPrice);
							orderDetalList.add(orderDetailDTO);
						}
					}
				}
			}
		}
		return orderDetalList;
	}

	@Override
	public OrderDTO findOne(String orderId) {

		List<OrderMaster> orderMasters = orderMasterRepository.findByOrderNo(orderId);
		if (orderMasters == null || orderMasters.size() <= 0) {
			throw new SellException(ResultEnum.ORDER_NOT_EXIST);
		}
		OrderMaster orderMaster = orderMasters.get(0);
		List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
		if (CollectionUtils.isEmpty(orderDetailList)) {
			throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);
		}

		OrderDTO orderDTO = new OrderDTO();
		BeanUtils.copyProperties(orderMaster, orderDTO);
		orderDTO.setOrderDetailList(orderDetailList);

		return orderDTO;
	}

	/**
	 * 通过openid查询自己及分销订单
	 * @param buyerOpenid
	 * @param isContainDistributor 0-查询自己订单，1-查询分销商订单
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<OrderDTO> findList(String buyerOpenid,String orderStatus, Integer isContainDistributor, Pageable pageable) {
		//通过openid查询自己及分销订单
        List<String> orderNos = orderService.findOderIdsByOpenId(buyerOpenid,isContainDistributor) ;
        Page<OrderMaster> orderMasterPage =null;
        
        if(CollectionUtils.isEmpty(orderNos)){
        	return PageableExecutionUtils.getPage(Lists.newArrayList(),pageable,new TotalSupplier(){
    			@Override
    			public long get() {
    				return 0L;
    			}
    		});
        }
        orderMasterPage = findOrderMasterByOrderNos(orderNos,orderStatus,pageable);
		List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

		//查询订单详情
		if(!CollectionUtils.isEmpty(orderDTOList)){
			for (OrderDTO orderDTO : orderDTOList){
				orderDTO.setOrderDetailList(orderService.findByOrderId(orderDTO.getOrderNo()));
			}
		}
		return new PageImpl<OrderDTO>(orderDTOList, pageable, orderMasterPage.getTotalElements());
	}

	@Override
	@Transactional
	public OrderDTO cancel(OrderDTO orderDTO) {
		OrderMaster orderMaster = new OrderMaster();

		// 判断订单状态
		if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.UNPAID.getCode().toString())) {
			log.error("【取消订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
			throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
		}

		// 修改订单状态
		orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode().toString());
		BeanUtils.copyProperties(orderDTO, orderMaster);
		OrderMaster updateResult = orderMasterRepository.save(orderMaster);
		if (updateResult == null) {
			log.error("【取消订单】更新失败, orderMaster={}", orderMaster);
			throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
		}

		// 返回库存
		if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
			log.error("【取消订单】订单中无商品详情, orderDTO={}", orderDTO);
			throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
		}
		/*
		 * List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream()
		 * .map(e -> new CartDTO(e.getProductId(), e.getProductQuantity()))
		 * .collect(Collectors.toList());
		 */
		List<CartDTO> cartDTOList = new ArrayList<CartDTO>();
		for (OrderDetail e : orderDTO.getOrderDetailList()) {
			CartDTO cartDTO = new CartDTO(e.getProductId(), e.getProductQuantity());
			cartDTOList.add(cartDTO);
		}
		productService.increaseStock(cartDTOList);

		// 如果已支付, 需要退款
		if (NumberConvert.byteToInt(orderDTO.getPayStatus()) == PayStatusEnum.SUCCESS.getCode()) {
			payService.refund(orderDTO);
		}

		return orderDTO;
	}

	@Override
	@Transactional
	public OrderDTO finish(OrderDTO orderDTO) {
		// 判断订单状态
		if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.UNPAID.getCode().toString())) {
			log.error("【完结订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
			throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
		}

		// 修改订单状态
		orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode().toString());
		OrderMaster orderMaster = new OrderMaster();
		BeanUtils.copyProperties(orderDTO, orderMaster);
		OrderMaster updateResult = orderMasterRepository.save(orderMaster);
		if (updateResult == null) {
			log.error("【完结订单】更新失败, orderMaster={}", orderMaster);
			throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
		}

		// 推送微信模版消息
		pushMessageService.orderStatus(orderDTO);

		return orderDTO;
	}

	@Override
	@Transactional
	public OrderMaster paid(OrderMaster orderMaster) {
		// 判断订单状态
		if (!Integer.valueOf(orderMaster.getOrderStatus()).equals(OrderStatusEnum.UNPAID.getCode())) {
			log.error("【订单支付完成】订单状态不正确, orderId={}, orderStatus={}", orderMaster.getOrderNo(), orderMaster.getOrderStatus());
			throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
		}

		// 判断支付状态
		if (!Integer.valueOf(orderMaster.getPayStatus()).equals(PayStatusEnum.WAIT.getCode())) {
			log.error("【订单支付完成】订单支付状态不正确, orderDTO={}", orderMaster);
			throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
		}

		// 修改支付状态
//		orderMaster.setPayStatus(NumberConvert.intToByte(PayStatusEnum.SUCCESS.getCode()));
		orderMaster.setOrderStatus(String.valueOf(OrderStatusEnum.PAIDED.getCode()));
		orderMaster.setPayStatus(NumberConvert.intToByte(PayStatusEnum.SUCCESS.getCode()));
		orderMaster.setEbayStatus("1");
		OrderMaster updateResult = orderMasterRepository.save(orderMaster);
		if (updateResult == null) {
			log.error("【订单支付完成】更新失败, orderMaster={}", orderMaster);
			throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
		}

		return orderMaster;
	}

	@Override
	public OrderMaster updateGroupNo(OrderMaster orderMaster) {
		//this.orderMasterRepository.
		return this.orderMasterRepository.saveAndFlush(orderMaster);
	}

	@Override
	public Page<OrderDTO> findList(Pageable pageable) {
		Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);

		List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

		return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
	}

	@Override
	public List<OrderDetail> findAll(final OrderDetail orderDetail, final List<Long> productIds, List<Long> orderIds,
			Date startDate, Date endDate) {
		Specification<OrderDetail> spec = new Specification<OrderDetail>() {
			public Predicate toPredicate(Root<OrderDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				// 默认查询有效，未删除的
				if (null != orderDetail.getProductId()) {
					predicate.add(cb.equal(root.get("productId").as(String.class), orderDetail.getProductId()));
				}
				if (null != productIds && !productIds.isEmpty()) {
					In<Long> in = cb.in(root.get("productId"));
					for (Long productId : productIds) {
						if (null != productId) {
							in.value(productId);
						}
					}
					predicate.add(in);
				}
				if (null != orderIds && !orderIds.isEmpty()) {
					In<String> in = cb.in(root.get("orderId"));
					for (Long orderId : orderIds) {
						if (null != orderId) {
							in.value(orderId.toString());
						}
					}
					predicate.add(in);
				}
				if (null != startDate && null != endDate) {
					predicate.add(cb.between(root.<Date>get("createTime"), startDate, endDate));
				}

				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderDetailRepository.findAll(spec);
	}

	@Override
	public Page<OrderMaster> findOrderMasterPage(final OrderMaster orderMaster, List<String> orderIds,
			Pageable pageable) {
		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				// 默认查询有效，未删除的
				predicate.add(cb.equal(root.get("isActive").as(String.class), "Y"));
				predicate.add(cb.equal(root.get("isDelete").as(String.class), "N"));
				if (StringUtils.isNoneBlank(orderMaster.getBuyerOpenid())) {
					predicate.add(cb.equal(root.get("buyerOpenid").as(String.class), orderMaster.getBuyerOpenid()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getOrderStatus())) {
					List<String> statusList = Arrays.asList(orderMaster.getOrderStatus().split("[,;]"));
					In<Object> in = cb.in(root.get("orderStatus"));
					for (String status : statusList) {
						in.value(status);
					}
					predicate.add(in);
				}
				if (null != orderIds && !orderIds.isEmpty()) {
					In<String> in = cb.in(root.get("orderNo"));
					for (String orderId : orderIds) {
						in.value(orderId);
					}
					predicate.add(in);
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderMasterRepository.findAll(spec, pageable);
	}

	@Override
	public List<OrderMaster> findOrderMasterList(final OrderMaster orderMaster, List<String> orderIds) {
		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				// 默认查询有效，未删除的
				predicate.add(cb.equal(root.get("isActive").as(String.class), "Y"));
				predicate.add(cb.equal(root.get("isDelete").as(String.class), "N"));
				// if (StringUtils.isNoneBlank(user.getUserName())) {
				// predicate.add(cb.like(root.get("userName").as(String.class),
				// "%" + user.getUserName() + "%"));
				// }
				if (null != orderIds && !orderIds.isEmpty()) {
					In<String> in = cb.in(root.get("orderNo"));
					for (String orderId : orderIds) {
						in.value(orderId);
					}
					predicate.add(in);
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderMasterRepository.findAll(spec);
	}

	@Override
	public List<OrderMaster> findOrderMasterList(String orderGroupNo) {

		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicate = Lists.newArrayList();
				/// 默认查询有效，未删除的
				predicate.add(cb.equal(root.get("isActive").as(String.class), "Y"));
				predicate.add(cb.equal(root.get("isDelete").as(String.class), "N"));
				predicate.add(cb.equal(root.get("orderGroupNo").as(String.class), orderGroupNo));

				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderMasterRepository.findAll(spec);
	}

	@Override
	public List<OrderMaster> findOrderMasterByOrderNo(String orderNo) {

		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicate = Lists.newArrayList();
				/// 默认查询有效，未删除的
				predicate.add(cb.equal(root.get("isActive").as(String.class), "Y"));
				predicate.add(cb.equal(root.get("isDelete").as(String.class), "N"));
				predicate.add(cb.equal(root.get("orderNo").as(String.class), orderNo));

				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderMasterRepository.findAll(spec);
	}

	@Override
	public Page<OrderMaster> findOrderMasterPage(OrderMaster orderMaster,Pageable pageable) {

		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				/// 默认查询有效，未删除的
				predicate.add(cb.equal(root.get("isActive").as(String.class), "Y"));
				predicate.add(cb.equal(root.get("isDelete").as(String.class), "N"));
				if (StringUtils.isNoneBlank(orderMaster.getOrderNo())) {
					predicate.add(cb.like(root.get("orderNo").as(String.class), "%" + orderMaster.getOrderNo() + "%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerName())) {
					predicate.add(cb.equal(root.get("buyerName").as(String.class), orderMaster.getBuyerName()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerPhone())) {
					predicate.add(cb.equal(root.get("buyerPhone").as(String.class), orderMaster.getBuyerPhone()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerAddress())) {
					predicate.add(cb.equal(root.get("buyerAddress").as(String.class), orderMaster.getBuyerAddress()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerOpenid())) {
					predicate.add(cb.equal(root.get("buyerOpenid").as(String.class), orderMaster.getBuyerOpenid()));
				}
				if (null != orderMaster.getPayStatus()) {
					predicate.add(cb.equal(root.get("payStatus").as(String.class), NumberConvert.byteToInt(orderMaster.getPayStatus())));
				}
				if (StringUtils.isNoneBlank(orderMaster.getEbayNo())) {
					predicate.add(cb.equal(root.get("ebayNo").as(String.class), orderMaster.getEbayNo()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getEbayStatus())) {
					predicate.add(cb.equal(root.get("ebayStatus").as(String.class), orderMaster.getEbayStatus()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeName())) {
					predicate.add(
							cb.like(root.get("cneeName").as(String.class), "%" + orderMaster.getCneeName() + "%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneePhone())) {
					predicate.add(cb.equal(root.get("cneePhone").as(String.class), orderMaster.getCneePhone()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeIdcard())) {
					predicate.add(cb.equal(root.get("cneeIdcard").as(String.class), orderMaster.getCneeIdcard()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeAddress())) {
					predicate.add(cb.equal(root.get("cneeAddress").as(String.class), orderMaster.getCneeAddress()));
				}

				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderMasterRepository.findAll(spec,pageable);
	}
	@Override
	public List<OrderMaster> findOrderMasterByNameList(OrderMaster orderMaster,List<String> names) {

		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				/// 默认查询有效，未删除的
				predicate.add(cb.equal(root.get("isActive").as(String.class), "Y"));
				predicate.add(cb.equal(root.get("isDelete").as(String.class), "N"));

				if (StringUtils.isNoneBlank(orderMaster.getOrderNo())) {
					predicate.add(cb.like(root.get("orderNo").as(String.class),
							"%"+orderMaster.getOrderNo()+"%"));
				}
				if (!CollectionUtils.isEmpty(names)) {
					In<String> in = cb.in(root.get("buyerName"));
					for (String buyerName : names) {
						in.value(buyerName);
					}
					predicate.add(in);
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerName())) {
					predicate.add(cb.like(root.get("buyerName").as(String.class),
							"%"+orderMaster.getBuyerName()+"%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getProductName())) {
					predicate.add(cb.like(root.get("productName").as(String.class),
							"%" + orderMaster.getProductName() + "%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerPhone())) {
					predicate.add(cb.like(root.get("buyerPhone").as(String.class),
							"%"+orderMaster.getBuyerPhone()+"%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerAddress())) {
					predicate.add(cb.equal(root.get("buyerAddress").as(String.class), orderMaster.getBuyerAddress()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerOpenid())) {
					predicate.add(cb.equal(root.get("buyerOpenid").as(String.class), orderMaster.getBuyerOpenid()));
				}
				if (null != orderMaster.getPayStatus()) {
					predicate.add(cb.equal(root.get("payStatus").as(String.class), NumberConvert.byteToInt(orderMaster.getPayStatus())));
				}
				if (StringUtils.isNoneBlank(orderMaster.getEbayNo())) {
					predicate.add(cb.equal(root.get("ebayNo").as(String.class), orderMaster.getEbayNo()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getEbayStatus())) {
					predicate.add(cb.equal(root.get("ebayStatus").as(String.class), orderMaster.getEbayStatus()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getOrderStatus())) {
					predicate.add(cb.equal(root.get("orderStatus").as(String.class), orderMaster.getOrderStatus()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeName())) {
					predicate.add(
							cb.like(root.get("cneeName").as(String.class), "%" + orderMaster.getCneeName() + "%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneePhone())) {
					predicate.add(cb.equal(root.get("cneePhone").as(String.class), orderMaster.getCneePhone()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeIdcard())) {
					predicate.add(cb.equal(root.get("cneeIdcard").as(String.class), orderMaster.getCneeIdcard()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeAddress())) {
					predicate.add(cb.equal(root.get("cneeAddress").as(String.class), orderMaster.getCneeAddress()));
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};

		return orderMasterRepository.findAll(spec);
	}

	@Override
	public Page<OrderDetail> findOrderDetailByOrderNoList(List<String> orderNoLsit,Pageable pageable) {

		Specification<OrderDetail> spec = new Specification<OrderDetail>() {
			public Predicate toPredicate(Root<OrderDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				if (!CollectionUtils.isEmpty(orderNoLsit)) {
					In<String> in = cb.in(root.get("orderId"));
					for (String orderNo : orderNoLsit) {
						in.value(orderNo);
					}
					predicate.add(in);
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};

		return orderDetailRepository.findAll(spec,pageable);
	}

	@Override
	public List<OrderDetail> findByOrderId(String orderId) {

		return orderDetailRepository.findByOrderId(orderId);
	}

	@Override
	public OrderDTO cancel(String openid, String orderId) {
	    OrderDTO orderDTO = checkOrderOwner(openid, orderId);
        if (orderDTO == null) {
            log.error("【取消订单】查不到改订单, orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        return orderService.cancel(orderDTO);
	}
	 private OrderDTO checkOrderOwner(String openid, String orderId) {
	        OrderDTO orderDTO = orderService.findOne(orderId);
	        if (orderDTO == null) {
	            return null;
	        }
	        //判断是否是自己的订单
//	        if (!orderDTO.getBuyerOpenid().equalsIgnoreCase(openid)) {
//	            log.error("【查询订单】订单的openid不一致. openid={}, orderDTO={}", openid, orderDTO);
//	            throw new SellException(ResultEnum.ORDER_OWNER_ERROR);
//	        }
	        return orderDTO;
	    }

	 	@Override
		@Transactional
		public OrderDTO save(OrderDTO orderDTO) {

			BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
			String orderGroupNo = "";
			List<String> orderList =Lists.newArrayList();
			
			// 1. 查询商品（数量, 价格）
			for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
				ProductInfo productInfo = productService.findOne(orderDetail.getProductId());
				if (productInfo == null) {
					throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
				}
				BigDecimal productPrice = productInfo.getProductPrice();
				orderList.add(orderDetail.getOrderId());
				//商品组id （itemId），如果有值则为多属性商品，商品价格子表获取
				String itemId = orderDetail.getItemId() ;
				List<ProductAttr> productAttrList = Lists.newArrayList();
				if (!StringUtils.isEmpty(itemId)){
					ProductAttr productAttr =new ProductAttr();
					productAttr.setProductId(orderDetail.getProductId());
					productAttr.setItemId(itemId);
					productAttrList = productAttrService.findAll(productAttr);
				}
				if (!CollectionUtils.isEmpty(productAttrList)) {
					if (!CollectionUtils.isEmpty(productAttrList)) {
						for (ProductAttr tempVO : productAttrList) {
							if ("price".equalsIgnoreCase(tempVO.getAttrEname())) {
								if (!StringUtils.isEmpty(tempVO.getAttrCvalue())) {

									productPrice = new BigDecimal(tempVO.getAttrCvalue());
								}
							}
						}
					}
				}
				// 2. 计算订单总价
				//运费
				BigDecimal carriageFee =new BigDecimal(BigInteger.ZERO);
				if(productInfo.getCarriageFee() !=null){
					carriageFee =productInfo.getCarriageFee();
				}
				//税费
				BigDecimal taxFee =new BigDecimal(BigInteger.ZERO);
				if(productInfo.getTaxFee() !=null){
					taxFee =productInfo.getTaxFee();
				}
				//
				BigDecimal perPrice = productPrice.multiply(new BigDecimal(orderDetail.getProductQuantity()));
				orderAmount = perPrice.add(orderAmount).add(carriageFee).add(taxFee);

				// 订单详情入库
				// orderDetail.setId(Long.parseLong(KeyUtil.genUniqueKey()));
				Long orderDetailId = orderDetail.getId();
				BeanUtils.copyProperties(productInfo, orderDetail);
				orderDetail.setId(orderDetailId);

				if (orderDetailId != null) {

					OrderDetail orderDetailTemp = orderDetailRepository.findOne(orderDetailId);
					if(orderDetailTemp!=null){

						BeanUtilEx.copyPropertiesIgnoreNull(orderDetail, orderDetailTemp);
						orderDetail = orderDetailTemp;
					}
				}

				orderDetailRepository.save(orderDetail);

				// 3. 写入订单数据库（orderMaster和orderDetail）
				OrderMaster orderMaster = new OrderMaster();
				BeanUtils.copyProperties(orderDTO, orderMaster);
				orderMaster.setCarriageFee(carriageFee);
				orderMaster.setTaxFee(taxFee);
				orderMaster.setOrderAmount(orderAmount);
				orderMaster.setOrderStatus(OrderStatusEnum.UNPAID.getCode().toString());
				orderMaster.setPayStatus(NumberConvert.intToByte(PayStatusEnum.WAIT.getCode()));
				
				if (orderMaster != null && orderMaster.getId()!=null) {
					
					OrderMaster orderMasterTemp = orderMasterRepository.findOne(orderMaster.getId());
					if(orderMasterTemp!=null){
						
						BeanUtilEx.copyPropertiesIgnoreNull(orderMaster, orderMasterTemp);
						orderMaster = orderMasterTemp;
						orderGroupNo = orderMasterTemp.getOrderGroupNo();
					}
				}
				
				
				orderMasterRepository.save(orderMaster);
				
				// 4. 扣库存
				List<CartDTO> cartDTOList = new ArrayList<CartDTO>();
				for (OrderDetail e : orderDTO.getOrderDetailList()) {
					CartDTO cartDTO = new CartDTO(e.getProductId(), e.getProductQuantity());
					cartDTOList.add(cartDTO);
				}
				productService.decreaseStock(cartDTOList);
			}


			// 发送websocket消息
			// webSocket.sendMessage(orderDTO.getOrderId());

			orderDTO.setOrderGroupNo(orderGroupNo);
			if(!CollectionUtils.isEmpty(orderList)){
				
				orderDTO.setOrderNo( String.join(",",orderList));
			}
			return orderDTO;
		}

	public List<OrderMaster> findOrderMasterList(OrderMaster orderMaster,Date startDate,Date endDate) {

		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				/// 默认查询有效，未删除的
				predicate.add(cb.equal(root.get("isActive").as(String.class), "Y"));
				predicate.add(cb.equal(root.get("isDelete").as(String.class), "N"));
				if (StringUtils.isNoneBlank(orderMaster.getOrderNo())) {
					predicate.add(cb.like(root.get("orderNo").as(String.class), "%" + orderMaster.getOrderNo() + "%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getOrderGroupNo())) {
					predicate.add(cb.equal(root.get("orderGroupNo").as(String.class), orderMaster.getOrderGroupNo()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerName())) {
					predicate.add(cb.equal(root.get("buyerName").as(String.class), orderMaster.getBuyerName()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerPhone())) {
					predicate.add(cb.equal(root.get("buyerPhone").as(String.class), orderMaster.getBuyerPhone()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerAddress())) {
					predicate.add(cb.equal(root.get("buyerAddress").as(String.class), orderMaster.getBuyerAddress()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerOpenid())) {
					predicate.add(cb.equal(root.get("buyerOpenid").as(String.class), orderMaster.getBuyerOpenid()));
				}
				if (null != orderMaster.getPayStatus()) {
					predicate.add(cb.equal(root.get("payStatus").as(String.class),
							NumberConvert.byteToInt(orderMaster.getPayStatus())));
				}
				if (StringUtils.isNoneBlank(orderMaster.getEbayNo())) {
					predicate.add(cb.equal(root.get("ebayNo").as(String.class), orderMaster.getEbayNo()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getEbayStatus())) {
					predicate.add(cb.equal(root.get("ebayStatus").as(String.class), orderMaster.getEbayStatus()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeName())) {
					predicate
							.add(cb.like(root.get("cneeName").as(String.class), "%" + orderMaster.getCneeName() + "%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneePhone())) {
					predicate.add(cb.equal(root.get("cneePhone").as(String.class), orderMaster.getCneePhone()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeIdcard())) {
					predicate.add(cb.equal(root.get("cneeIdcard").as(String.class), orderMaster.getCneeIdcard()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeAddress())) {
					predicate.add(cb.equal(root.get("cneeAddress").as(String.class), orderMaster.getCneeAddress()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getOrderStatus())) {
					predicate.add(cb.equal(root.get("orderStatus").as(String.class), orderMaster.getOrderStatus()));
				}
				if (null != startDate && null != endDate) {
					predicate.add(cb.between(root.<Date>get("updated"), startDate, endDate));
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderMasterRepository.findAll(spec);
	}

	public List<OrderDetail> findOrderDetailList(List<String> orderIds) {
		Specification<OrderDetail> spec = new Specification<OrderDetail>() {
			public Predicate toPredicate(Root<OrderDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				if (null != orderIds && !orderIds.isEmpty()) {
					In<String> in = cb.in(root.get("orderId"));
					for (String orderId : orderIds) {
						in.value(orderId);
					}
					predicate.add(in);
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderDetailRepository.findAll(spec);
	}
	@Transactional
	@Modifying
	@CacheEvict(cacheNames = "secondlevels",allEntries = true)
	public void update(OrderListFrom orderListFrom){
		OrderMaster orderMaster=orderMasterRepository.findOne(orderListFrom.getId());
		List<OrderDetail> orderDetail=orderDetailRepository.findByOrderId(orderListFrom.getOrderNo());
		orderMaster.setProductName(orderListFrom.getProductName());
		orderMaster.setCarriageFee(orderListFrom.getCarriageFee());
		orderMaster.setOrderAmount(orderListFrom.getOrderAmount());//订单总金额
		orderMaster.setEbayAmount(orderListFrom.getEbayAmount());
		orderMaster.setCneeName(orderListFrom.getCneeName());
		orderMaster.setCneeAddress(orderListFrom.getCneeAddress());
		orderMaster.setCneePhone(orderListFrom.getCneePhone());
		orderMaster.setOrderStatus(orderListFrom.getOrderStatus());
		orderMaster.setEbayStatus(orderListFrom.getEbayStatus());
		orderMaster.setLogisticsStatus(orderListFrom.getLogisticsStatus());
		orderMaster.setLogisticsNo(orderListFrom.getLogisticsNo());
		for (OrderDetail orderDetail1:orderDetail){
			orderDetail1.setProductPrice(orderListFrom.getProductPrice());
			orderDetail1.setProductQuantity(orderListFrom.getProductQuantity());
		}
		orderMasterRepository.save(orderMaster);
		orderDetailRepository.save(orderDetail);
	}
	/**
	 *  通过openId获取自己以及分销商订单
	 * @param isContainDistributor 0-查询自己订单，1-查询分销商订单
	 * @return
	 */
    public List<String> findOderIdsByOpenId(String openId,Integer isContainDistributor){
    	
    	List<String>  orderNos = Lists.newArrayList();
    	
		if (StringUtils.isEmpty(openId)) {
			return orderNos;
		}
		//1.获取自己的订单号
		List<OrderMaster>  orderMasters =orderMasterRepository.findByBuyerOpenid(openId);
		
		if(!CollectionUtils.isEmpty(orderMasters)){
    		for (OrderMaster orderMaster : orderMasters){
    			orderNos.add(orderMaster.getOrderNo());
    		}
    	}
		
    	//2.根据openid获取译者的商品信息
    	List<Long>  productIds  = Lists.newArrayList();
    	
    	List<String>  openIds  = Lists.newArrayList();
    	openIds.add(openId);
    	//3.获取分销商的订单编号
    	List<String>  distributedOrderNos = Lists.newArrayList();
    	
    	if(isContainDistributor ==1){
			
    		distributedOrderNos = findDistributedOderIdsByOpenIds(openIds);
    		orderNos = distributedOrderNos;
    	}
    	return orderNos ;
    }
    /** 通过openId获分销商订单. */
    public List<String> findDistributedOderIdsByOpenIds(List<String> openIds){
    	
    	List<String>  orderNos = Lists.newArrayList();
    	
		if (CollectionUtils.isEmpty(openIds)) {
			return orderNos;
		}
    	//1.根据openid获取译者的商品信息
    	List<Long>  productIds  = Lists.newArrayList();
    	
    	List<ProductInfo> productInfos = productService.findByUserWxOpenids(openIds);
    	
    	if(!CollectionUtils.isEmpty(productInfos)){
    		for (ProductInfo productInfo : productInfos){
    			productIds.add(productInfo.getId());
    		}
    	}
    	
    	//3.根据商品id获取购买者订单编号
    	List<OrderDetail>  orderDetails = Lists.newArrayList();
    	if(!CollectionUtils.isEmpty(productIds)){
    		
    		orderDetails = findOrderDetailByProductIds(productIds);
    	}
    	
    	if(!CollectionUtils.isEmpty(orderDetails)){
    		
    		for (OrderDetail orderDetail : orderDetails){
    			orderNos.add(orderDetail.getOrderId());
    		}
    	}
    	
    	return orderNos ;
    }
	/**
	 * 通过商品id获取订单信息
	 * @param productIds
	 * @return
	 */
	@Override
	public List<OrderDetail> findOrderDetailByProductIds(List<Long> productIds) {

		Specification<OrderDetail> spec = new Specification<OrderDetail>() {
			public Predicate toPredicate(Root<OrderDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				if (!CollectionUtils.isEmpty(productIds)) {
					In<Long> in = cb.in(root.get("productId"));
					for (Long orderNo : productIds) {
						in.value(orderNo);
					}
					predicate.add(in);
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};

		return orderDetailRepository.findAll(spec);
	}
	/**
	 * 根据订单编号分页查询订单信息
	 * @param orderNos
	 * @param pageable
	 * @return
	 */
	public Page<OrderMaster> findOrderMasterByOrderNos(List<String> orderNos, String orderStatus,Pageable pageable) {
		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				if (!CollectionUtils.isEmpty(orderNos)) {
					In<String> in = cb.in(root.get("orderNo"));
					for (String orderId : orderNos) {
						in.value(orderId);
					}
					predicate.add(in);
				}
				if (StringUtils.isNoneBlank(orderStatus)) {
					predicate.add(cb.equal(root.get("orderStatus").as(String.class),orderStatus));
				}
				Predicate[] pre = new Predicate[predicate.size()];
				query.orderBy(cb.desc(root.get("created").as(Date.class)));
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderMasterRepository.findAll(spec,pageable);
	}
	/**
	 * 查询订单列表
	 * @param orderMaster
	 * @return
	 */
	@Override
	public List<OrderMaster> findOrderMasterList(OrderMaster orderMaster) {

		Specification<OrderMaster> spec = new Specification<OrderMaster>() {
			public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = Lists.newArrayList();
				/// 默认查询有效，未删除的
				predicate.add(cb.equal(root.get("isActive").as(String.class), "Y"));
				predicate.add(cb.equal(root.get("isDelete").as(String.class), "N"));
				if (StringUtils.isNoneBlank(orderMaster.getOrderNo())) {
					predicate.add(cb.like(root.get("orderNo").as(String.class), "%" + orderMaster.getOrderNo() + "%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerName())) {
					predicate.add(cb.equal(root.get("buyerName").as(String.class), orderMaster.getBuyerName()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerPhone())) {
					predicate.add(cb.equal(root.get("buyerPhone").as(String.class), orderMaster.getBuyerPhone()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerAddress())) {
					predicate.add(cb.equal(root.get("buyerAddress").as(String.class), orderMaster.getBuyerAddress()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getBuyerOpenid())) {
					predicate.add(cb.equal(root.get("buyerOpenid").as(String.class), orderMaster.getBuyerOpenid()));
				}
				if (null != orderMaster.getPayStatus()) {
					predicate.add(cb.equal(root.get("payStatus").as(String.class), NumberConvert.byteToInt(orderMaster.getPayStatus())));
				}
				if (StringUtils.isNoneBlank(orderMaster.getEbayNo())) {
					predicate.add(cb.equal(root.get("ebayNo").as(String.class), orderMaster.getEbayNo()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getEbayStatus())) {
					predicate.add(cb.equal(root.get("ebayStatus").as(String.class), orderMaster.getEbayStatus()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeName())) {
					predicate.add(
							cb.like(root.get("cneeName").as(String.class), "%" + orderMaster.getCneeName() + "%"));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneePhone())) {
					predicate.add(cb.equal(root.get("cneePhone").as(String.class), orderMaster.getCneePhone()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeIdcard())) {
					predicate.add(cb.equal(root.get("cneeIdcard").as(String.class), orderMaster.getCneeIdcard()));
				}
				if (StringUtils.isNoneBlank(orderMaster.getCneeAddress())) {
					predicate.add(cb.equal(root.get("cneeAddress").as(String.class), orderMaster.getCneeAddress()));
				}

				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		};
		return orderMasterRepository.findAll(spec);
	}
}
