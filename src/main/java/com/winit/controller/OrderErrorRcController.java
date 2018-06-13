package com.winit.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.winit.VO.ResultVO;
import com.winit.dataobject.OrderErrorRc;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.service.OrderErrorRcService;
import com.winit.utils.ResultVOUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单异常轨迹接口
 * Created by liyou
 * 2017-06-18 23:27
 */
@RestController
@RequestMapping("/ebay/errorrc")
@Slf4j
@SuppressWarnings("unchecked")
public class OrderErrorRcController {

    @Autowired
    private OrderErrorRcService orderErrorRcService;


    //创建异常轨迹订单
    @ApiOperation(value = "创建异常轨迹订单")
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@RequestBody OrderErrorRc orderErrorForm,
                                                BindingResult bindingResult,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
            log.error("【创建异常订单】参数不正确, orderForm={}", orderErrorForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }


        OrderErrorRc orderError = orderErrorRcService.save(orderErrorForm);

        Map<String, String> map = new HashMap<>();
        map.put("orderErrorId", orderError.getId().toString());

        return ResultVOUtil.success(map);
    }
    //修改异常轨迹订单
    @ApiOperation(value = "编辑异常轨迹订单")
    @PutMapping("/update")
    public ResultVO<Map<String, String>> update(@RequestBody OrderErrorRc orderErrorRcForm,
                                                BindingResult bindingResult,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
            log.error("【修改异常订单】参数不正确, orderForm={}", orderErrorRcForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        OrderErrorRc orderErrorRc = orderErrorRcService.save(orderErrorRcForm);

        return ResultVOUtil.success();
    }
    //异常轨迹订单列表
    @ApiOperation(value = "异常轨迹订单列表")
    @GetMapping("/list")
	public ResultVO list(
			@ApiParam(value = "异常编号") @RequestParam(required = false, value = "errorNo", defaultValue = "") String errorNo,
			@ApiParam(value = "异常类型") @RequestParam(required = false, value = "errorType", defaultValue = "") String errorType,
			@ApiParam(value = "异常状态（0:待解决 1:已解决 2:未解决 3:挂起）") @RequestParam(required = false, value = "errorStatus", defaultValue = "") String errorStatus,
			@ApiParam(value = "异常描述") @RequestParam(required = false, value = "errorMemo", defaultValue = "") String errorMemo,
			@ApiParam(value = "解决说明") @RequestParam(required = false, value = "sloveMemo", defaultValue = "") String sloveMemo,
			@ApiParam(value = "处理方式") @RequestParam(required = false, value = "handerType", defaultValue = "") String handerType,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size, HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	Sort sort = new Sort(Direction.DESC, "updated");
        PageRequest request = new PageRequest(page, size,sort);
        OrderErrorRc orderErrorRc = new OrderErrorRc();
        orderErrorRc.setErrorNo(errorNo);
        orderErrorRc.setErrorType(errorType);
        orderErrorRc.setErrorStatus(errorStatus);
        orderErrorRc.setErrorMemo(errorMemo);
        orderErrorRc.setSloveMemo(sloveMemo);
        orderErrorRc.setHanderType(handerType);
        Page<OrderErrorRc> orderErrorRcPage = orderErrorRcService.findAllPage(orderErrorRc, request);
        return ResultVOUtil.success(orderErrorRcPage);
    }



    @ApiOperation(value = "删除异常轨迹订单")
    @DeleteMapping("/delete")
    public ResultVO cancel(@RequestBody OrderErrorRc orderErrorRcForm,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	orderErrorRcService.delete(orderErrorRcForm);
        return ResultVOUtil.success();
    }
}
