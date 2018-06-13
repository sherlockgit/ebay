package com.winit.service;

import com.winit.dataobject.OrderMaster;
import com.winit.dto.OrderDTO;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundResponse;

import java.util.Map;

/**
 * 支付
 * Created by liyou
 * 2017-07-04 00:53
 */
public interface PayService {

    Map createEbayPay(String orderId);

    PayResponse create(OrderDTO orderDTO);

    PayResponse notify(String notifyData);

    RefundResponse refund(OrderDTO orderDTO);

    /**
     * 万邑网支付通知
     * @param notifyData
     * @return
     */
    public PayResponse notifyEbay(String notifyData);

    /**
     * 万邑网微信退款退款
     * @param orderId
     * @return
     */
    public RefundResponse refundEbay(Long orderId);

}
