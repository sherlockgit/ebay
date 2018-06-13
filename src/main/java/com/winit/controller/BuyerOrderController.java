package com.winit.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.winit.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.winit.VO.ResultVO;
import com.winit.converter.OrderForm2OrderDTOConverter;
import com.winit.dataobject.OrderMaster;
import com.winit.dto.OrderDTO;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.form.OrderForm;
import com.winit.service.BuyerService;
import com.winit.service.OrderService;
import com.winit.utils.ResultVOUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by liyou
 * 2017-06-18 23:27
 */
@RestController
@RequestMapping("/buyer/order")
@Slf4j
@SuppressWarnings("unchecked")
public class BuyerOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BuyerService buyerService;

    //创建订单
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@RequestBody OrderForm orderForm,
                                                BindingResult bindingResult,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm={}", orderForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("【创建订单】购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }

        OrderDTO createResult = orderService.create(orderDTO);

        Map<String, String> map = new HashMap<>();
        map.put("orderGroupNo", createResult.getOrderGroupNo());
        map.put("orderId", createResult.getOrderNo());

        return ResultVOUtil.success(map);
    }
    //修改订单
    @ApiOperation(value = "买家编辑订单")
    @PutMapping("/save")
    public ResultVO<Map<String, String>> save(@RequestBody OrderForm orderForm,
                                                BindingResult bindingResult,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm={}", orderForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("【创建订单】购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }

        OrderDTO createResult =  orderService.save(orderDTO);
        Map<String, String> map = new HashMap<>();
        map.put("orderGroupNo", createResult.getOrderGroupNo());
        map.put("orderId", createResult.getOrderNo());
        return ResultVOUtil.success(map);
    }
    //订单列表
    @GetMapping("/list/{openid}/{isContainDistributor}/{page}/{size}")
    public ResultVO<List<OrderDTO>> list(@PathVariable("openid") String openid,
    										@PathVariable("isContainDistributor") Integer isContainDistributor,
                                            @RequestParam(required = false, value = "orderStatus",defaultValue = "") String orderStatus,
    										@PathVariable("page") Integer page,
    										@PathVariable("size")Integer size,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        if (StringUtils.isEmpty(openid)) {
            log.error("【查询订单列表】openid为空");
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        PageRequest request = new PageRequest(page, size);
        Page<OrderDTO> orderDTOPage = orderService.findList(openid,orderStatus,isContainDistributor, request);
        return ResultVOUtil.success(orderDTOPage.getContent(),orderDTOPage.getContent().size());
    }


    //订单详情
    @GetMapping("/detail/{openid}/{orderId}")
    public ResultVO<OrderDTO> detail(@PathVariable("openid") String openid,
    								 @PathVariable("orderId") String orderId,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        OrderDTO orderDTO = buyerService.findOrderOne(openid, orderId);
        return ResultVOUtil.success(orderDTO);
    }

    //取消订单
    @PostMapping("/cancel/{openid}/{orderId}")
    public ResultVO cancel( @PathVariable("openid") String openid,
    						@PathVariable("orderId") String orderId, HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        buyerService.cancelOrder(openid, orderId);
        OrderMaster orderMaster = new OrderMaster();
        orderMaster.setOrderNo(orderId);
        orderMaster.setBuyerOpenid(openid);
        List<OrderMaster> orderMasterList = orderService.findOrderMasterByNameList(orderMaster, null);
        if(!CollectionUtils.isEmpty(orderMasterList)){
        	orderMaster =orderMasterList.get(0) ;
        }
        return ResultVOUtil.success(orderMaster);
    }
}
