package com.winit.controller;

import com.lly835.bestpay.model.RefundResponse;
import com.lly835.bestpay.service.impl.WxPaySignature;
import com.winit.VO.ResultVO;
import com.winit.dto.OrderDTO;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.service.OrderService;
import com.winit.service.PayService;
import com.lly835.bestpay.model.PayResponse;
import com.winit.utils.ResultVOUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付
 * Created by liyou
 * 2017-07-04 00:49
 */
@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayService payService;

    @GetMapping("/create")
    @ResponseBody
    @ApiOperation("万邑淘支付订单创建 result-订单是否创建成功 错误码 out_of_quantity:当前购买数量高于库存数量  out_of_stock:当前商品已经停售  out_of_price：当前商品价格高于录入价格 not_found_item商品已经下架")
    public ResultVO<Map> createPayinfo(@ApiParam("订单id")@RequestParam("orderGroupNo") String orderGroupNo,
                                  @ApiParam("支付成功后返回页面")@RequestParam("returnUrl") String returnUrl){

       Map<String, Object> result = payService.createEbayPay(orderGroupNo);
       result.put("returnUrl",returnUrl);
       return ResultVOUtil.success(result);

    }

    @GetMapping("/ebay/refund")
    @ApiOperation("万邑淘支付订单退款申请")
    public ResultVO<RefundResponse> refund(@ApiParam("订单id")@RequestParam("orderId") Long orderId){

        RefundResponse reuslt = payService.refundEbay(orderId);

        return ResultVOUtil.success(reuslt);

    }

    /**
     * 微信异步通知
     * @param notifyData
     */
    @PostMapping("/notify")
    @GetMapping("/notify")
    @ApiOperation("万邑淘支付订单异步通知  微信平台调用")
    public ModelAndView notify(@RequestBody String notifyData) {
        payService.notifyEbay(notifyData);
        return new ModelAndView("pay/success");
    }
}
