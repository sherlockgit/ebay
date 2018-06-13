package com.winit.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.winit.VO.OrderErrorVo;
import com.winit.VO.ResultVO;
import com.winit.dataobject.OrderError;
import com.winit.dataobject.OrderErrorRc;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.service.OrderErrorRcService;
import com.winit.service.OrderErrorService;
import com.winit.utils.KeyUtil;
import com.winit.utils.ResultVOUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单异常跟踪接口
 * Created by liyou
 * 2017-06-18 23:27
 */
@RestController
@RequestMapping("/ebay/error")
@Slf4j
@SuppressWarnings("unchecked")
public class OrderErrorController {

    @Autowired
    private OrderErrorService orderErrorService;
    
    @Autowired
    private OrderErrorRcService orderErrorRcService;


    //创建异常订单
    @ApiOperation(value = "创建异常订单")
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@RequestBody OrderError orderErrorForm,
                                                BindingResult bindingResult,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
            log.error("【创建异常订单】参数不正确, orderForm={}", orderErrorForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        String errorNo = KeyUtil.genUniqueKey();
        
        orderErrorForm.setErrorNo(errorNo);
        OrderError orderError = orderErrorService.save(orderErrorForm);
       
        OrderErrorRc orderErrorRc = new OrderErrorRc();
        BeanUtils.copyProperties(orderError, orderErrorRc);
        orderErrorRc.setId(null);
        orderErrorRc.setCreatedby(orderErrorForm.getHanderby());
        orderErrorRcService.save(orderErrorRc);

        Map<String, String> map = new HashMap<>();
        map.put("orderErrorId", orderError.getId().toString());

        return ResultVOUtil.success(map);
    }
    
    @ApiOperation(value = "异常订单编辑")
    @PutMapping("/save")
    public ResultVO<Map<String, String>> save(@RequestBody OrderErrorVo orderErrorForm,
                                                BindingResult bindingResult,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
            log.error("【修改异常订单】参数不正确, orderForm={}", orderErrorForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        OrderError orderError=new OrderError();
        BeanUtils.copyProperties(orderErrorForm,orderError);
       orderErrorService.save(orderError);
        OrderErrorRc orderErrorRc = new OrderErrorRc();
        BeanUtils.copyProperties
                (orderErrorForm, orderErrorRc);
        orderErrorRc.setId(null);
       // orderErrorRc.setHanderType(handerType);
        orderErrorRc.setCreatedby(orderErrorForm.getHanderby());
        orderErrorRcService.save(orderErrorRc);
        

        return ResultVOUtil.success();
    }
    //订单列表
    @ApiOperation(value = "异常订单列表")
    @GetMapping("/list")
	public ResultVO list(
			@ApiParam(value = "异常编号") @RequestParam(required = false, value = "errorNo", defaultValue = "") String errorNo,
			@ApiParam(value = "订单编号") @RequestParam(required = false, value = "orderNo", defaultValue = "") String orderNo,
			@ApiParam(value = "异常类型") @RequestParam(required = false, value = "errorType", defaultValue = "") String errorType,
			@ApiParam(value = "异常状态（0:待解决 1:已解决 2:未解决 3:挂起）") @RequestParam(required = false, value = "errorStatus", defaultValue = "") String errorStatus,
			@ApiParam(value = "异常描述") @RequestParam(required = false, value = "errorMemo", defaultValue = "") String errorMemo,
			@ApiParam(value = "解决说明") @RequestParam(required = false, value = "sloveMemo", defaultValue = "") String sloveMemo,
			@ApiParam(value = "最后处理人") @RequestParam(required = false, value = "handerby", defaultValue = "") String handerby,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size, HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");

        PageRequest request = new PageRequest(page, size);
        OrderError orderError = new OrderError();
        orderError.setErrorNo(errorNo);
        orderError.setOrderNo(orderNo);
        orderError.setErrorType(errorType);
        orderError.setErrorStatus(errorStatus);
        orderError.setErrorMemo(errorMemo);
        orderError.setSloveMemo(sloveMemo);
        orderError.setHanderby(handerby);
        Page<OrderError> orderErrorPage = orderErrorService.findAll(orderError, request);
        return ResultVOUtil.success(orderErrorPage);
    }



    @ApiOperation(value = "删除异常订单")
    @DeleteMapping("/delete/{id}")
    public ResultVO cancel(@PathVariable("id") String id,
    		HttpServletResponse response) {
    	OrderError orderErrorForm = new OrderError();
    	orderErrorForm.setId(Long.valueOf(id));
    	response.setHeader("Access-Control-Allow-Origin","*");
    	orderErrorService.delete(orderErrorForm);
        return ResultVOUtil.success();
    }
}
