package com.winit.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.winit.dataobject.OrderDetail;
import com.winit.dataobject.OrderMaster;
import com.winit.dataobject.ProductInfo;
import com.winit.dto.OrderDTO;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.repository.OrderDetailRepository;
import com.winit.repository.OrderMasterRepository;
import com.winit.repository.ProductInfoRepository;
import com.winit.service.EbayService;
import com.winit.service.OrderService;
import com.winit.service.PayService;
import com.winit.utils.JsonUtil;
import com.winit.utils.KeyUtil;
import com.winit.utils.MathUtil;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundRequest;
import com.lly835.bestpay.model.RefundResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.winit.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liyou
 * 2017-07-04 00:54
 */
@Service
@Slf4j
public class PayServiceImpl implements PayService {

    private static final String ORDER_NAME = "微信点餐订单";

    @Autowired
    private BestPayServiceImpl bestPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private EbayService ebayService;


    @Override
    public Map createEbayPay(String orderGroupNo) {

        Map<String, Object> result = new HashMap<String, Object>();
        List<OrderMaster> orderList = orderService.findOrderMasterList(orderGroupNo);

        log.info("pay-orderGroupNo:"+orderGroupNo);
        if(null==orderList || orderList.size()==0){
            orderList = orderService.findOrderMasterByOrderNo(orderGroupNo);
        }

        String newOrderGroupNo = KeyUtil.genUniqueKey();
        for(OrderMaster orderMaster: orderList){
            orderMaster.setOrderGroupNo(newOrderGroupNo);
            orderService.updateGroupNo(orderMaster);
        }
        orderList.stream().forEach(orderMaster -> {
            orderMaster.setOrderGroupNo(newOrderGroupNo);
        });

//        log.info("orderGroupNo is " + orderGroupNo);
//        OrderMaster OrderMaster = orderList.get(0);

        //校验订单
//        Map<String, Object> checkResult = check(OrderMaster);
//        if(checkResult.get("result").equals(false)){
//            return checkResult;
//        }
        //创建订单信息
        PayRequest payRequest = createPayRequest(orderList);
        log.info("【微信支付】发起支付, request={}", JsonUtil.toJson(payRequest));
        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("【微信支付】发起支付, response={}", JsonUtil.toJson(payResponse));
        result.put("result", true);
        result.put("payResponse", payResponse);

        return result;
    }

    /**
     * 创建支付请求
     * @param list
     * @return
     */
    private PayRequest createPayRequest(List<OrderMaster> list){


        OrderMaster orderMaster = list.get(0);
        BigDecimal amount = new BigDecimal(0);
        amount = amount.setScale(2);
         for(OrderMaster orderMaster1 : list){
             amount = amount.add(orderMaster1.getOrderAmount());
        }

        PayRequest payRequest = new PayRequest();
        payRequest.setOpenid(orderMaster.getBuyerOpenid());
        payRequest.setOrderAmount(amount.doubleValue());
        payRequest.setOrderId(orderMaster.getOrderGroupNo());
        payRequest.setOrderName("万邑淘购物");
        payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);

        return payRequest;
    }

    /**
     * 校验订单
     * @param orderMaster
     * @return
     */
    private Map check(OrderMaster orderMaster){

        Map<String, Object> result = new HashMap<String, Object>();
        String itemId = orderMaster.getItemId();
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderMaster.getOrderNo());
        for (OrderDetail orderDetail:orderDetailList){

            JSONObject jsonObject = ebayService.getCompactGoods(itemId);
            JSONArray jsonArray = (JSONArray) jsonObject.get("estimatedAvailabilities");
            JSONObject estimatedAvailabilities = (JSONObject) jsonArray.get(0);
            String estimatedAvailabilityStatus = (String) estimatedAvailabilities.get("estimatedAvailabilityStatus");

            //验证商品是否还在卖
            if("OUT_OF_STOCK".equals(estimatedAvailabilityStatus)){
                result.put("result", false);
                result.put("message","out_of_stock");
                break;
            }

            //验证是否超库存
            Object quantity  = (String) estimatedAvailabilities.get("estimatedAvailableQuantity");
            if(null!=quantity){
                Integer orderQuantity = orderDetail.getProductQuantity();
                Integer estimatedAvailableQuantity = Integer.valueOf(quantity.toString());
                if(orderQuantity>estimatedAvailableQuantity){
                    result.put("result", false);
                    result.put("message","out_of_quantity");
                    break;
                }
            }else{
                Integer availabilityThreshold  = Integer.valueOf(estimatedAvailabilities.get("availabilityThreshold").toString());
                Integer orderQuantity = orderDetail.getProductQuantity();
                if(orderQuantity>availabilityThreshold){
                    result.put("result", false);
                    result.put("message","out_of_quantity");
                    break;
                }
            }

            //验证价格是否超额
            JSONObject price = (JSONObject) jsonObject.get("price");
            ProductInfo productInfo = productInfoRepository.findOne(orderDetail.getProductId());
            BigDecimal productPrice = productInfo.getProductUsd();
            BigDecimal currentPrice = new BigDecimal(price.get("value").toString());
            if(currentPrice.compareTo(productPrice) == 1){ //currentPrice > productPrice
                result.put("result", false);
                result.put("message","out_of_price");
                break;
            }
        }

        result.put("result", true);
        return result;
    }

    @Override
    public PayResponse create(OrderDTO orderDTO) {

        PayRequest payRequest = new PayRequest();
        payRequest.setOpenid(orderDTO.getBuyerOpenid());
        payRequest.setOrderAmount(orderDTO.getOrderAmount().doubleValue());
        payRequest.setOrderId(orderDTO.getOrderId());
        payRequest.setOrderName(ORDER_NAME);
        payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);
        log.info("【微信支付】发起支付, request={}", JsonUtil.toJson(payRequest));

        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("【微信支付】发起支付, response={}", JsonUtil.toJson(payResponse));
        return payResponse;
    }

    /**
     * 万邑网支付通知
     * @param notifyData
     * @return
     */
    public PayResponse notifyEbay(String notifyData) {
        //1. 验证签名
        //2. 支付的状态
        //3. 支付金额
        //4. 支付人(下单人 == 支付人)

        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("【微信支付】异步通知, payResponse={}", JsonUtil.toJson(payResponse));

        //查询订单
/*
        OrderMaster orderMaster = orderMasterRepository.findByOrderNo(payResponse.getOrderId()).get(0);
*/
        List<OrderMaster> orderList = orderService.findOrderMasterList(payResponse.getOrderId());

        if(null==orderList || orderList.size()==0){
            orderList = orderService.findOrderMasterByOrderNo(payResponse.getOrderId());
        }
        /*OrderDTO orderDTO = orderService.findOne(payResponse.getOrderId());*/

        //判断订单是否存在
/*        if (orderMaster == null) {
            log.error("【微信支付】异步通知, 订单不存在, orderId={}", payResponse.getOrderId());
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        //判断金额是否一致(0.10   0.1)
        BigDecimal amount = orderMaster.getOrderAmount().add(orderMaster.getCarriageFee());
        if (!MathUtil.equals(payResponse.getOrderAmount(), amount.doubleValue())) {
            log.error("【微信支付】异步通知, 订单金额不一致, orderId={}, 微信通知金额={}, 系统金额={}",
                    payResponse.getOrderId(),
                    payResponse.getOrderAmount(),
                    amount);
            throw new SellException(ResultEnum.WXPAY_NOTIFY_MONEY_VERIFY_ERROR);
        }*/

        //修改订单的支付状态
        for(OrderMaster orderMaster : orderList) {
            orderService.paid(orderMaster);
        }

        return payResponse;
    }

    @Override
    public PayResponse notify(String notifyData) {
        //1. 验证签名
        //2. 支付的状态
        //3. 支付金额
        //4. 支付人(下单人 == 支付人)

        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("【微信支付】异步通知, payResponse={}", JsonUtil.toJson(payResponse));

        //查询订单
        OrderDTO orderDTO = orderService.findOne(payResponse.getOrderId());

        //判断订单是否存在
        if (orderDTO == null) {
            log.error("【微信支付】异步通知, 订单不存在, orderId={}", payResponse.getOrderId());
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        //判断金额是否一致(0.10   0.1)
        if (!MathUtil.equals(payResponse.getOrderAmount(), orderDTO.getOrderAmount().doubleValue())) {
            log.error("【微信支付】异步通知, 订单金额不一致, orderId={}, 微信通知金额={}, 系统金额={}",
                    payResponse.getOrderId(),
                    payResponse.getOrderAmount(),
                    orderDTO.getOrderAmount());
            throw new SellException(ResultEnum.WXPAY_NOTIFY_MONEY_VERIFY_ERROR);
        }

        //修改订单的支付状态
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderService.paid(orderMaster);

        return payResponse;
    }

    /**
     * 退款
     * @param orderDTO
     */
    @Override
    public RefundResponse refund(OrderDTO orderDTO) {
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderId(orderDTO.getOrderId());
        refundRequest.setOrderAmount(orderDTO.getOrderAmount().doubleValue());
        refundRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);
        log.info("【微信退款】request={}", JsonUtil.toJson(refundRequest));

        RefundResponse refundResponse = bestPayService.refund(refundRequest);
        log.info("【微信退款】response={}", JsonUtil.toJson(refundResponse));

        return refundResponse;
    }

    /**
     * 万邑网微信退款退款
     * @param orderId
     * @return
     */
    public RefundResponse refundEbay(Long orderId){

        OrderMaster orderMaster = orderMasterRepository.findOne(orderId);
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderId(orderMaster.getOrderGroupNo());
        BigDecimal amount = orderMaster.getOrderAmount();
        refundRequest.setOrderAmount(amount.doubleValue());
        refundRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);
        log.info("【微信退款】request={}", JsonUtil.toJson(refundRequest));

        RefundResponse refundResponse = bestPayService.refund(refundRequest);
        log.info("【微信退款】response={}", JsonUtil.toJson(refundResponse));

        return refundResponse;
    }
}
