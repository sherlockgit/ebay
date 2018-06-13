package com.winit.scheduled;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.winit.dataobject.Distribution;
import com.winit.dataobject.OrderDetail;
import com.winit.dataobject.OrderMaster;
import com.winit.dataobject.ProductInfo;
import com.winit.dataobject.User;
import com.winit.enums.OrderStatusEnum;
import com.winit.service.DistributionService;
import com.winit.service.OrderService;
import com.winit.service.ProductService;
import com.winit.service.UserService;
import com.winit.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 分销定时任务
 * @author yeyalin
 *
 */
@Component
@EnableScheduling
@Slf4j
public class DistributionScheduled {
	@Autowired
    private DistributionService distributionService;
	
	@Autowired
    private OrderService orderService;
	
	@Autowired
	private ProductService productService ;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 分销结算
	 */
	@Scheduled(cron = "${scheduled.distributionCron}")
	public void caculateDistirbute(){
		
		log.info("每3秒钟开始分销结算, 开始分销结算");
		
 		OrderMaster orderMaster = new OrderMaster();
		orderMaster.setIsActive("Y");
		orderMaster.setIsDelete("N");
		orderMaster.setOrderStatus(OrderStatusEnum.FINISHED.getCode().toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		
		Date startDate = DateUtil.getNowDateBeforOneDay(new Date(), sdf);
		Date endDate = DateUtil.getNowDate(sdf);
		
		List<OrderMaster>  orderMasterList = orderService.findOrderMasterList(orderMaster,startDate,endDate) ;
		
		 //获取orderId集合
    	List<String> orderIds = Lists.newArrayList();
    	
    	if(!CollectionUtils.isEmpty(orderMasterList)){
    		
    		for(OrderMaster orderMasterVO:orderMasterList){
    			
				List<Distribution> isDistributionList = distributionService.findByOrderNo(orderMasterVO.getOrderNo());

				if (CollectionUtils.isEmpty(isDistributionList)) {

					orderIds.add(orderMasterVO.getOrderNo());
				}
    		}
    	}
    	List<OrderDetail> orderDetailList = Lists.newArrayList();
    	
    	//获取订单详情
    	if(!CollectionUtils.isEmpty(orderIds)){
    		orderDetailList = orderService.findOrderDetailList(orderIds);
    	}
    	
    	
    	//封装分销结算对象
    	List<Distribution> distributionList = Lists.newArrayList();
    	
    	if(! CollectionUtils.isEmpty(orderMasterList) && !CollectionUtils.isEmpty(orderDetailList)){
    		
    		for(OrderMaster orderMasterVO : orderMasterList){
    			
    			for(OrderDetail orderDetailVO : orderDetailList){
        			
        			if(orderMasterVO.getOrderNo() .equals(orderDetailVO.getOrderId())){
        				
        				Distribution distribution = new Distribution();
        				
        				distribution.setOrderNo(orderMasterVO.getOrderNo());
        				distribution.setProductTotalPrice(orderMasterVO.getOrderAmount());
        				distribution.setBuyUserName(orderMasterVO.getBuyerName());
        				
        				distribution.setProductName(orderDetailVO.getProductName());
        				//获取卖家信息
        				ProductInfo productInfo = productService.findOne(orderDetailVO.getProductId());
        				
        				//一级微信号
        				String firstWxName ="";
        				if(productInfo != null){
        					firstWxName = productInfo.getUserWxOpenid() ;
        				}
        				
        				//一级分销姓名
        				String  firstDistName  = "" ;
        				if(!StringUtils.isEmpty(firstWxName)){
        					
        					//通过微信号获取会员姓名
        					User user = userService.selectByUserWxOpenid(firstWxName);
        					
        					if(null != user){
        						firstDistName = user.getUserName();
        					}
        				}
        				
        				distribution.setFirstWxName(firstWxName);
        				distribution.setFirstDistName(firstDistName);
        				
        				distribution.setAuditStatus("0");
        				distribution.setIsActive("Y");
        				distribution.setIsDelete("N");
        				
        				//ebay总金额
        				BigDecimal ebayAmount = new BigDecimal(BigInteger.ZERO);
        				
        				//订单总金额
        				BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        				
        				//订单差价
        				BigDecimal productGapAmount = new BigDecimal(BigInteger.ZERO);
        				
        				if( null != orderMasterVO.getEbayAmount()){
        					
        					ebayAmount = orderMasterVO.getEbayAmount();
        				}
        				
						if (null != orderMasterVO.getOrderAmount()) {

							orderAmount = orderMasterVO.getOrderAmount();
						}
						
						//订单差价  = 订单总金额  - ebay总金额   
						productGapAmount =  orderAmount.subtract(ebayAmount);
						
						distribution.setEbayAmount(ebayAmount);
        				distribution.setProductTotalPrice(orderAmount);
        				distribution.setProductGapAmount(productGapAmount);
        				distribution.setCreatedby(firstDistName);
        				
        				distributionList.add(distribution);
        			}
        		}
    		}
    		
    	}
    	//创建分销信息
    	if(! CollectionUtils.isEmpty(distributionList)){
    		
    		//检查是否已经分销了，如果分销了，则订单完结后，不产生分销数据
			for (Distribution distribution : distributionList){
				
				List<Distribution> isDistributionList = distributionService.findByOrderNo(distribution.getOrderNo());
				
				if( CollectionUtils.isEmpty(isDistributionList)) {
					
					distributionService.create(distribution) ;
				}
			}
		}
    	
		log.info("每3秒钟开始分销结算, 分销结算结束");
	}
}
