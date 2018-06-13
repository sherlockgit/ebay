package com.winit.controller;

import com.alibaba.fastjson.JSONObject;
import com.winit.VO.ResultVO;
import com.winit.dataobject.Account;
import com.winit.dataobject.Distribution;
import com.winit.dataobject.OrderDetail;
import com.winit.dataobject.User;
import com.winit.dto.OrderDTO;
import com.winit.enums.ResultEnum;
import com.winit.service.AccountItemService;
import com.winit.service.AccountService;
import com.winit.service.DistributionService;
import com.winit.service.OrderService;
import com.winit.service.UserService;
import com.winit.utils.BeanUtilEx;
import com.winit.utils.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yhy
 * 2017-10-22 14:27
 */
@Api(tags = "分销结算")
@RestController
@RequestMapping("distribution/")
@Slf4j
@SuppressWarnings("unchecked")
public class DistributionController {
    
    @Autowired
    private DistributionService distributionService;
    
    @Autowired
    private OrderService orderService;
    
	 //分销结算列表
	@ApiOperation(value = "分销结算列表")
    @GetMapping("list")
    public ResultVO<List<User>> distributionList(
    		@ApiParam(value = "订单编号") @RequestParam(required = false, value = "orderNo") String orderNo,
    		@ApiParam(value = "商品名称") @RequestParam(required = false, value = "productName") String productName,
    		@ApiParam(value = "用户姓名") @RequestParam(required = false, value = "buyUserName") String buyUserName,
    		@ApiParam(value = "审核状态[0-待审核 1-未通过 2-已通过 3-作废]") @RequestParam(required = false, value = "auditStatus") String auditStatus,
    		@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	Distribution distribution = new Distribution();
    	distribution.setOrderNo(orderNo);
    	distribution.setProductName(productName);
    	distribution.setBuyUserName(buyUserName);
    	distribution.setAuditStatus(auditStatus);
    	Sort sort = new Sort(Direction.DESC, "created");
		PageRequest pageable = new PageRequest(page, size,sort);
		Page<Distribution> distributionList = distributionService.findPage(distribution,pageable);
        return ResultVOUtil.success(distributionList);
    }
	
	@ApiOperation(value = "分销详情")
    @GetMapping("{id}")
    public ResultVO<Map<String, String>> userAccount(@PathVariable("id") String id,
            HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	//获取用户信息
    	JSONObject relutJson = new JSONObject();
    	
    	if (StringUtils.isEmpty(id)){
    		log.error("找不到分销信息");
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                   "找不到分销信息");
    	}
    	Distribution distribution = distributionService.selectOne(Long.valueOf(id));
    	if(null == distribution){
    		log.error("找不到分销信息");
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                   "找不到分销信息");
    	}
    	if(StringUtils.isBlank(distribution.getOrderNo())){
    		  return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                      "订单信息不全");
    	}
    	OrderDTO orderDTO = orderService.findOne(distribution.getOrderNo());
    	relutJson.put("distribution", distribution);
    	relutJson.put("order", orderDTO);
        return ResultVOUtil.success(relutJson);
    }
	
	//编辑
	@PostMapping("/{id}/edit")
	@ApiOperation(value = "分销编辑")
	public ResultVO<Map<String, String>> edit(
			@PathVariable("id") String id,
			@RequestBody Distribution distribution, 
	            HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		if (StringUtils.isEmpty(id)){
    		log.error("找不到分销信息");
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                   "找不到分销信息");
    	}
		
		Distribution dtemp =  distributionService.selectOne(Long.valueOf(id));
		if(null == dtemp){
			 return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
					 "未找到分销数据");
		}
		distribution.setId(Long.valueOf(id));
		
		if(StringUtils.isNoneBlank(distribution.getFirstDistRatio()) && null != distribution.getFirstCommission() && null != dtemp.getProductTotalPrice()){
			BigDecimal firstDistRatio_T = new BigDecimal(distribution.getFirstDistRatio().replaceAll("%", ""));
			BigDecimal temp = dtemp.getProductTotalPrice().multiply(firstDistRatio_T).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_DOWN);
			if(!distribution.getFirstCommission().setScale(2,BigDecimal.ROUND_HALF_DOWN).equals(temp)){
				return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                     "一级提成分销数据不合法");
			}
			distribution.setTotalCommission(distribution.getFirstCommission());
		}
		if(StringUtils.isNoneBlank(distribution.getSecondDistRatio()) &&
				!"0".equals(distribution.getSecondDistRatio()) &&
				!"NaN".equals(distribution.getSecondDistRatio())
				&& null != distribution.getSecondCommission()
				&& null != dtemp.getProductTotalPrice()){
			BigDecimal secondDistRatio_T = new BigDecimal(distribution.getSecondDistRatio().replaceAll("%", ""));
			BigDecimal temp = dtemp.getProductTotalPrice().multiply(secondDistRatio_T).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_DOWN);
			if(!distribution.getSecondCommission().setScale(2,BigDecimal.ROUND_HALF_DOWN).equals(temp)){
				return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
						"二级提成分销数据不合法");
			}
			
			if(null != distribution.getFirstCommission()){
				BigDecimal totalCommission = distribution.getFirstCommission().add(distribution.getSecondCommission());
				distribution.setTotalCommission(totalCommission);
			}else{
				if(null != dtemp.getFirstCommission()){
					distribution.setTotalCommission(dtemp.getFirstCommission().add(distribution.getSecondCommission()));
				}else{
					distribution.setTotalCommission(distribution.getSecondCommission());
				}
			}
		}
		
		String errMsg = distributionService.createNew(distribution, dtemp);
		if(StringUtils.isNoneEmpty(errMsg)){
			return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
					errMsg);
		}
		return ResultVOUtil.success(dtemp);
	}
}
