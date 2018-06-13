package com.winit.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.winit.VO.ResultVO;
import com.winit.dataobject.Account;
import com.winit.dataobject.AccountItem;
import com.winit.dataobject.OrderDetail;
import com.winit.dataobject.OrderMaster;
import com.winit.dataobject.ProductInfo;
import com.winit.dataobject.User;
import com.winit.dataobject.UserCnee;
import com.winit.enums.ResultEnum;
import com.winit.service.AccountItemService;
import com.winit.service.AccountService;
import com.winit.service.OrderService;
import com.winit.service.ProductService;
import com.winit.service.UserCneeService;
import com.winit.service.UserService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yhy
 * 2017-10-22 14:27
 */
@Api(tags = "会员信息")
@RestController
@RequestMapping("/ebay/user")
@Slf4j
@SuppressWarnings("unchecked")
public class UserController {
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountItemService accountItemService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserCneeService userCneeService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;


    //会员列表
    @GetMapping("/list")
    @ApiOperation(value = "会员列表")
    public ResultVO<List<User>> list(@ApiParam(value = "会员名称") @RequestParam(required = false, value = "userName", defaultValue = "") String userName,
									 @ApiParam(value = "会员号码") @RequestParam(required = false, value = "userPhone", defaultValue = "") String userPhone,
									 @ApiParam(value = "会员类型【1:分销商、2:普通用户】") @RequestParam(required = false, value = "userCtype", defaultValue = "") String userCtype,
									 @ApiParam(value = "地址") @RequestParam(required = false, value = "userAddr", defaultValue = "") String userAddr,
									 @ApiParam(value = "微信号名称") @RequestParam(required = false, value = "userWxName", defaultValue = "") String userWxName,
									 @ApiParam(value = "微信号(OPENID)") @RequestParam(required = false, value = "userWxOpenid", defaultValue = "") String userWxOpenid,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	User user = new User();
    	user.setUserName(userName);
    	user.setUserPhone(userPhone);
    	user.setUserCtype(userCtype);
    	user.setUserAddr(userAddr);
    	user.setUserWxName(userWxName);
    	user.setUserWxOpenid(userWxOpenid);
    	Sort sort = new Sort(Direction.DESC, "updated");
        PageRequest request = new PageRequest(page, size,sort);
        Page<User> userPage = userService.findPage(user,request);
        return ResultVOUtil.success(userPage);
    }
    
    //会员详情
    @GetMapping("{id}")
    @ApiOperation(value = "会员详情")
    public ResultVO<User> findByUserId(@PathVariable("id") String id,
            HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
        return ResultVOUtil.success(userService.selectOne(id));
    }

    //创建
	@PostMapping("/create")
	@ApiOperation(value = "添加会员")
    public ResultVO<Map<String, String>> create(@RequestBody User user,
                                                BindingResult bindingResult,
                                                HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
           log.error("【创建】参数不正确, user={}", user);
           return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        userService.create(user);
        Map<String, String> map = new HashMap<>();
        map.put("id", user.getId());
        return ResultVOUtil.success(map);
    }
	
	//创建
	@PostMapping("/weixin/create")
	@ApiOperation(value = "添加会员(微信回调用)")
    public ResultVO<Map<String, String>> weixinCreate(
										    		@ApiParam(value = "会员名称") @RequestParam(required = false, value = "userName", defaultValue = "") String userName,
										    		@ApiParam(value = "会员手机号") @RequestParam(required = true, value = "userPhone") String userPhone,
										    		@ApiParam(value = "会员类型(1:分销商、2:普通用户)") @RequestParam(required = false, value = "userCtype", defaultValue = "2") String userCtype,
										    		@ApiParam(value = "会员地址") @RequestParam(required = false, value = "userAddr", defaultValue = "") String userAddr,
										    		@ApiParam(value = "提现密码") @RequestParam(required = false, value = "userTxpwd", defaultValue = "") String userTxpwd,
										    		@ApiParam(value = "微信名称") @RequestParam(required = true, value = "userWxName") String userWxName,
										    		@ApiParam(value = "微信openID") @RequestParam(required = true, value = "userWxOpenid") String userWxOpenid,
										    		@ApiParam(value = "会员图片地址") @RequestParam(required = false, value = "userWxPicture", defaultValue = "") String userWxPicture,
	                                                HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		if(StringUtils.isBlank(userWxOpenid)){
			 return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "openID 不存在");
		}
		User user_db = userService.selectByUserWxOpenid(userWxOpenid);
		if(null == user_db || StringUtils.isBlank(user_db.getId())){
			user_db = new User();
		}
		if(StringUtils.isNotBlank(userPhone)){
			user_db.setUserPhone(userPhone);
		}
		if(StringUtils.isNotBlank(userTxpwd)){
			user_db.setUserTxpwd(userTxpwd);
		}
		if(StringUtils.isNotBlank(userCtype)){
			user_db.setUserCtype(userCtype);
		}
		if(StringUtils.isNotBlank(userAddr)){
			user_db.setUserAddr(userAddr);
		}
		if(StringUtils.isNotBlank(userWxPicture)){
			user_db.setUserWxPicture(userWxPicture);
		}
		if(StringUtils.isNotBlank(userWxOpenid)){
			user_db.setUserWxOpenid(userWxOpenid);
		}
		if(StringUtils.isNotBlank(userWxName)){
			user_db.setUserWxName(userWxName);
			user_db.setUserName(userWxName);
		}
		if(StringUtils.isNotBlank(userName)){
			user_db.setUserName(userName);
		}
        userService.create(user_db);
        Map<String, String> map = new HashMap<>();
        map.put("id", user_db.getId());
        return ResultVOUtil.success(map);
    }
	
	//修改
	@PostMapping("/{id}/update")
	@ApiOperation(value = "会员信息修改")
	public ResultVO<Map<String, String>> update(
			@PathVariable("id") String id,
			@RequestBody User user,
	            HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		User c_user = userService.selectOne(id);
		if(null == c_user){
			log.error("【修改】找不到该用户");
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                   "【修改】找不到该用户");
		}
		if(StringUtils.isNoneBlank(user.getUserName())){
			c_user.setUserName(user.getUserName());
		}

		c_user.setUserPhone(user.getUserPhone());

		if(StringUtils.isNoneBlank(user.getUserAddr())){
			c_user.setUserAddr(user.getUserAddr());
		}
		if(StringUtils.isNoneBlank(user.getUserCtype())){
			c_user.setUserCtype(user.getUserCtype());
		}
		if(StringUtils.isNoneBlank(user.getUserTxpwd())){
			c_user.setUserTxpwd(user.getUserTxpwd());
		}
		if(StringUtils.isNoneBlank(user.getUserWxName())){
			c_user.setUserWxName(user.getUserWxName());
		}
		if(StringUtils.isNoneBlank(user.getUserWxOpenid())){
			c_user.setUserWxOpenid(user.getUserWxOpenid());
		}
		if(null != user.getOrganizationId()){
			c_user.setOrganizationId(user.getOrganizationId());
		}
		if(StringUtils.isNoneBlank(user.getIsActive())){
			c_user.setIsActive(user.getIsActive());
		}
		if(StringUtils.isNoneBlank(user.getIsDelete())){
			c_user.setIsDelete(user.getIsDelete());
		}
		if(StringUtils.isNoneBlank(user.getUpdatedby())){
			c_user.setUpdatedby(user.getUpdatedby());
		}
		c_user.setUpdated(new Date());
		userService.create(c_user);
		Map<String, String> map = new HashMap<>();
		map.put("id", c_user.getId());
		return ResultVOUtil.success(map);
	}
	
	//批量软删除
	@ApiOperation(value = "会员信息批量删除")
	@DeleteMapping("/{ids}/delete")
	public ResultVO<Map<String, String>> delete(
			@PathVariable("ids") String ids,
            HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		Map<String, String> map = new HashMap<>();
		if(StringUtils.isBlank(ids)){
			log.error("id集合不能为空");
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                   "参数错误");
		}
		List<User> list = userService.selectList(Arrays.asList(ids.split("[,;]")));
		for (User user : list) {
			user.setIsActive("N");
			user.setIsDelete("Y");
		}
		userService.batch(list);
		map.put("ids", ids);
		return ResultVOUtil.success(map);
	}
	
	 //会员账户详情
	@ApiOperation(value = "会员账户详情")
    @GetMapping("account/{id}")
    public ResultVO<User> userAccount(@PathVariable("id") String id,
            HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	//获取用户信息
    	User user = userService.selectOne(id);
    	if(null == user){
    		log.error("会员信息不存在");
            return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
                   "会员信息不存在");
    	}
    	// 后续开发
    	Account account = accountService.findByUserId(id);
    	AccountItem accountItem = new AccountItem();
    	accountItem.setUserId(id);
    	accountItem.setTradeType("0");
    	accountItem.setTradeStatus("1");
    	List<AccountItem> accountItemList = accountItemService.findAll(accountItem,null,null,null,null);
    	
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd");
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.DATE,   -1);
	    String yesterday = simpleDateFormat.format(calendar.getTime());
    	//昨提收益 （Yesterday）
    	BigDecimal yIncome = new BigDecimal(Float.valueOf(0f));
    	//历史收益（History）
    	BigDecimal hIncome = new BigDecimal(Float.valueOf(0f));
    	for (AccountItem c_accountItem : accountItemList) {
    		BigDecimal tradeInAmount = new BigDecimal(Float.valueOf(c_accountItem.getTradeInAmount()!= null?c_accountItem.getTradeInAmount():"0"));
    		if(yesterday.equals(simpleDateFormat.format(c_accountItem.getCreated()))){
    			yIncome = yIncome.add(tradeInAmount); 
    		}
    		hIncome = hIncome.add(tradeInAmount);
		}
    	JSONObject json = (JSONObject) JSONObject.toJSON(user);
    	json.remove("userTxpwd");
    	json.put("userBalance", account!=null?account.getUserBalance():0);
    	//昨提收益
    	json.put("yIncome", yIncome.setScale(2,BigDecimal.ROUND_HALF_DOWN));
    	//历史收益
    	json.put("hIncome", hIncome.setScale(2,BigDecimal.ROUND_HALF_DOWN));
        return ResultVOUtil.success(json);
    }
    
    //收货地址列表
	@ApiOperation(value = "会员地址列表")
    @GetMapping("/userCnee/list")
    public ResultVO<List<UserCnee>> list(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         @ApiParam(value = "会员ID") @RequestParam(required = false, value = "userId", defaultValue = "") String userId,
                                         @ApiParam(value = "收货人姓名") @RequestParam(required = false, value = "cneeName", defaultValue = "") String cneeName,	
                                         @ApiParam(value = "收货人号码") @RequestParam(required = false, value = "cneePhone", defaultValue = "") String cneePhone,
                                         @ApiParam(value = "收货人地址") @RequestParam(required = false, value = "cneeAddress", defaultValue = "") String cneeAddress,
                                         @ApiParam(value = "收货人省份证") @RequestParam(required = false, value = "cneeIdcard", defaultValue = "") String cneeIdcard,
                                         @ApiParam(value = "是否默认地址") @RequestParam(required = false, value = "isDefaute", defaultValue = "") String isDefaute,
                                         HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	UserCnee userCnee = new UserCnee();
    	userCnee.setUserId(userId);
    	userCnee.setCneeAddress(cneeAddress);
    	userCnee.setCneeName(cneeName);
    	userCnee.setCneePhone(cneePhone);
    	userCnee.setCneeIdcard(cneeIdcard);
    	userCnee.setIsDefaute(isDefaute);
    	Sort sort = new Sort(Direction.DESC, "created");
        PageRequest request = new PageRequest(page, size,sort);
        Page<UserCnee> userPage = userCneeService.findPage(userCnee,request);
        return ResultVOUtil.success(userPage);
    }
	
	//创建
	@PostMapping("/userCnee/create")
	@ApiOperation(value = "添加会员地址")
    public ResultVO<Map<String, String>> create(@RequestBody UserCnee userCnee,
                                                BindingResult bindingResult,
                                               HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
           log.error("【创建】参数不正确, userCnee={}", userCnee);
           return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        if("Y".equals(userCnee.getIsDefaute())){
        	PageRequest request = new PageRequest(0, 10);
        	UserCnee userCnee_A = new UserCnee();
        	userCnee_A.setUserId(userCnee.getUserId());
        	userCnee_A.setIsDefaute("Y");
        	Page<UserCnee> list = userCneeService.findPage(userCnee_A, request);
        	for (UserCnee userCnee2 : list) {
        		userCnee2.setIsDefaute("N");
        		userCneeService.create(userCnee2);
			}
        }
        userCneeService.create(userCnee);
        Map<String, String> map = new HashMap<>();
        map.put("id", userCnee.getId()+"");
        return ResultVOUtil.success(map);
    }
	
	//修改地址/删除地址
	@PostMapping("/userCnee/{id}/update")
	@ApiOperation(value = "修改会员地址")
    public ResultVO<Map<String, String>> updateUserCnee(@RequestBody UserCnee userCnee,
    												@PathVariable("id") String id,
                                                BindingResult bindingResult,
                                               HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		UserCnee dbUserCnee = userCneeService.selectOne(id);
		if(null == dbUserCnee){
			log.error("【修改】找不到地址信息");
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
                   "【修改】找不到地址信息");
		}
		if(StringUtils.isNoneBlank(userCnee.getCneeAddress())){
			dbUserCnee.setCneeAddress(userCnee.getCneeAddress());
		}
		if(StringUtils.isNoneBlank(userCnee.getCneePhone())){
			dbUserCnee.setCneePhone(userCnee.getCneePhone());
		}
		if(StringUtils.isNoneBlank(userCnee.getCneeName())){
			dbUserCnee.setCneeName(userCnee.getCneeName());
		}
		if(StringUtils.isNoneBlank(userCnee.getCneeIdcard())){
			dbUserCnee.setCneeIdcard(userCnee.getCneeIdcard());
		}
		if(StringUtils.isNoneBlank(userCnee.getIsActive())){
			dbUserCnee.setIsActive(userCnee.getIsActive());
		}
		if(StringUtils.isNoneBlank(userCnee.getIsDelete())){
			dbUserCnee.setIsDelete(userCnee.getIsDelete());
		}
		if(StringUtils.isNoneBlank(userCnee.getIsDefaute())){
			dbUserCnee.setIsDefaute(userCnee.getIsDefaute());
		}
		
		if("Y".equals(userCnee.getIsDefaute())){
        	if(StringUtils.isNoneBlank(dbUserCnee.getUserId())){
        		PageRequest request = new PageRequest(0, 10);
        		UserCnee userCnee_A = new UserCnee();
        		userCnee_A.setUserId(userCnee.getUserId());
        		userCnee_A.setIsDefaute("Y");
        		Page<UserCnee> list = userCneeService.findPage(userCnee_A, request);
        		for (UserCnee userCnee2 : list) {
        			userCnee2.setIsDefaute("N");
        			userCneeService.create(userCnee2);
        		}
        	}
        }
		dbUserCnee.setUpdated(new Date());
        userCneeService.create(dbUserCnee);
        Map<String, String> map = new HashMap<>();
        map.put("id", userCnee.getId()+"");
        return ResultVOUtil.success(map);
    }
	
    /**
     * 我的客户
     * @return
     */
	@ApiOperation(value = "我的客户")
    @GetMapping("/user/myCustomer")
    public ResultVO<Map<String, String>> myCustomer(
    		@ApiParam(value = "微信openId") @RequestParam(required = true, value = "userWxOpenid") String userWxOpenid,
    		@ApiParam(value = "开始日期") @RequestParam(required = true, value = "startDate") String startDate,
    		@ApiParam(value = "结束日期") @RequestParam(required = true, value = "endDate") String endDate,
    		@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
    		 HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	try {
	    	ProductInfo productInfo = new ProductInfo();
	    	productInfo.setUserWxOpenid(userWxOpenid);
	    	productInfo.setAuditStatus("");
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    	//根据openID 获取用户下的所有商品
			List<ProductInfo> productInfoList = productService.findAll(productInfo, null, null);
			if(null == productInfoList || productInfoList.isEmpty()){
				return ResultVOUtil.success("");
			}
			List<Long> productIds = Lists.newArrayList();
			for (ProductInfo productInfo2 : productInfoList) {
				productIds.add(productInfo2.getId());
			}
			
			Map<String, Date> order_id_date = Maps.newHashMap();
			//根据商品获取所有订单信息
			OrderDetail orderDetail = new OrderDetail();
			List<OrderDetail> orderDetailList = orderService.findAll(orderDetail,productIds,null, sdf.parse(startDate), sdf.parse(endDate));
			if(null == orderDetailList || orderDetailList.isEmpty()){
				return ResultVOUtil.success("");
			}
			List<String> orderIds = Lists.newArrayList(); 
			for (OrderDetail orderDetail2 : orderDetailList) {
				orderIds.add(orderDetail2.getOrderId());
				order_id_date.put(orderDetail2.getOrderId(), orderDetail2.getCreateTime());
			}
			
			Sort sort = new Sort(Direction.DESC, "created");
			PageRequest pageable = new PageRequest(page, size,sort);
			OrderMaster orderMaster = new OrderMaster();
			Page<OrderMaster> orderMasterList = orderService.findOrderMasterPage(orderMaster, orderIds, pageable);
			if(null == orderMasterList){
				return ResultVOUtil.success("");
			}
			
			Map<String, Date> open_id_date = Maps.newHashMap();
			//获取订单详细信息中的买家微信openID
			List<String> buyerWxOpenIds = Lists.newArrayList();
			for (OrderMaster orderMaster2 : orderMasterList) {
				if(StringUtils.isNoneBlank(orderMaster2.getBuyerOpenid())){
					buyerWxOpenIds.add(orderMaster2.getBuyerOpenid());
					//根据orderNO 找寻order_id_date 中的订单详情时间 
					if(null != order_id_date.get(orderMaster2.getOrderNo())){
						open_id_date.put(orderMaster2.getBuyerOpenid(), order_id_date.get(orderMaster2.getOrderNo()));
					}
				}
			}
			if(null == buyerWxOpenIds || buyerWxOpenIds.isEmpty()){
				return ResultVOUtil.success("");
			}
			
			//根绝openId 查询用户的信息
			List<User> list = userService.selectList(new User(),buyerWxOpenIds);
			for (User user : list) {
				user.setUserTxpwd(null);
				if(null != open_id_date.get(user.getUserWxOpenid())){
					user.setCreated(open_id_date.get(user.getUserWxOpenid()));
				}
			}
			return ResultVOUtil.success(list);
		} catch (ParseException e) {
			 return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "时间格式错误");
		}
    }
    
    /**
     * 我的客户总量
     * @param categoryId
     * @param map
     * @return
     */
    @ApiOperation(value = "我的客户总量")
    @GetMapping("/user/myCustomerCount")
    public ResultVO<Map<String, String>> myCustomerCount(
    		@ApiParam(value = "用户openId") @RequestParam(required = true, value = "userWxOpenid") String userWxOpenid,
    		HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
		ProductInfo productInfo = new ProductInfo();
		productInfo.setUserWxOpenid(userWxOpenid);
		productInfo.setAuditStatus("");
		//根据openID 获取用户下的所有商品
		List<ProductInfo> productInfoList = productService.findAll(productInfo, null, null);
		if(null == productInfoList || productInfoList.isEmpty()){
			return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到数据");
		}
		List<Long> productIds = Lists.newArrayList();
		for (ProductInfo productInfo2 : productInfoList) {
			productIds.add(productInfo2.getId());
		}
		//根据商品获取所有订单信息
		OrderDetail orderDetail = new OrderDetail();
		List<OrderDetail> orderDetailList = orderService.findAll(orderDetail,productIds,null,null,null);
		if(null == orderDetailList || orderDetailList.isEmpty()){
			return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到数据");
		}
		List<String> orderIds = Lists.newArrayList(); 
		for (OrderDetail orderDetail2 : orderDetailList) {
			orderIds.add(orderDetail2.getOrderId());
		}
		
		OrderMaster orderMaster = new OrderMaster();
		List<OrderMaster> orderMasterList = orderService.findOrderMasterList(orderMaster, orderIds);
		if(null == orderMasterList){
			return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到数据");
		}
		
		//获取订单详细信息中的买家微信openID
		List<String> buyerWxOpenIds = Lists.newArrayList();
		for (OrderMaster orderMaster2 : orderMasterList) {
			if(StringUtils.isNoneBlank(orderMaster2.getBuyerOpenid()) && 
					!buyerWxOpenIds.contains(orderMaster2.getBuyerOpenid())){
				buyerWxOpenIds.add(orderMaster2.getBuyerOpenid());
			}
		}
		if(null == buyerWxOpenIds || buyerWxOpenIds.isEmpty()){
			return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到数据");
		}
		
		//根绝openId 查询用户的信息
		List<User> list = userService.selectList(new User(),buyerWxOpenIds);
		return ResultVOUtil.success(null!=list?list.size():0);
    }
    
    @GetMapping("/user/myWallet")
    @ApiOperation(value = "我的钱包")
    public ResultVO<List<AccountItem>> list(@ApiParam(value = "微信openID") @RequestParam(required = false, value = "userWxOpenid", defaultValue = "") String userWxOpenid,
						    		 @ApiParam(value = "开始日期") @RequestParam(required = true, value = "startDate") String startDate,
						    		 @ApiParam(value = "结束日期") @RequestParam(required = true, value = "endDate") String endDate,
    								 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	User user = userService.selectByUserWxOpenid(userWxOpenid);
    	if(null == user){
    		return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到该用户");
    	}
		try {
			AccountItem accountItem = new AccountItem();
			accountItem.setUserId(user.getId());
			accountItem.setTradeStatus("1");
			Sort sort = new Sort(Direction.DESC, "created");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			PageRequest pageable = new PageRequest(page, size,sort);
			Page<AccountItem> accountItemList = accountItemService.findPage(accountItem,sdf.parse(startDate),sdf.parse(endDate),pageable);
			List<Long> productId = Lists.newArrayList();
			for (AccountItem accountItem2 : accountItemList) {
				if(StringUtils.isNoneBlank(accountItem2.getProductId())){
					productId.add(Long.valueOf(accountItem2.getProductId()));
				}
			}
			List<ProductInfo> list = productService.findListByIds(productId);
			
			List<JSONObject> jsonObjectList = Lists.newArrayList();
			for (AccountItem accountItem_C : accountItemList) {
				JSONObject json = (JSONObject) JSONObject.toJSON(accountItem_C);
				for (ProductInfo productInfo : list) {
					if(StringUtils.isNoneBlank(accountItem_C.getProductId()) &&
							productInfo.getId().toString().equals(accountItem_C.getProductId())){
						json.put("product", productInfo);
					}
				}
				jsonObjectList.add(json);
			}
			return ResultVOUtil.success(jsonObjectList);
		} catch (ParseException e) {
			 return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "数据格式错误");
		}
    }
    
    @GetMapping("/user/myWallet/count")
    @ApiOperation(value = "我的钱包收入支出数量")
    public ResultVO<List<AccountItem>> list(@ApiParam(value = "微信openID") @RequestParam(required = false, value = "userWxOpenid", defaultValue = "") String userWxOpenid,
						    		 @ApiParam(value = "开始日期") @RequestParam(required = true, value = "startDate") String startDate,
						    		 @ApiParam(value = "结束日期") @RequestParam(required = true, value = "endDate") String endDate,
                                     HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	User user = userService.selectByUserWxOpenid(userWxOpenid);
    	if(null == user){
    		return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到该用户");
    	}
    	JSONObject json = new JSONObject(); 
    	try {
			AccountItem accountItem = new AccountItem();
			accountItem.setUserId(user.getId());
			accountItem.setTradeStatus("1");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<AccountItem> accountItemList;
			accountItemList = accountItemService.findAll(accountItem,null,null,sdf.parse(startDate),sdf.parse(endDate));
			//支出
	    	BigDecimal zIncome = new BigDecimal(Float.valueOf(0f));
	    	//收入
	    	BigDecimal sIncome = new BigDecimal(Float.valueOf(0f));
			for (AccountItem accountItem2 : accountItemList) {
				if("0".equals(accountItem2.getTradeType())){
					if(StringUtils.isNoneBlank(accountItem2.getTradeInAmount())){
						sIncome = sIncome.add(new BigDecimal(Float.parseFloat(accountItem2.getTradeInAmount())));
					}
				}
				if("1".equals(accountItem2.getTradeType()) || "2".equals(accountItem2.getTradeType()) ){
					if(StringUtils.isNoneBlank(accountItem2.getTradeOutAmount())){
						zIncome = zIncome.add(new BigDecimal(Float.parseFloat(accountItem2.getTradeOutAmount())));
					}
				}
			}
			json.put("zIncome", zIncome.setScale(2,BigDecimal.ROUND_HALF_DOWN));
			json.put("sIncome", sIncome.setScale(2,BigDecimal.ROUND_HALF_DOWN));
		} catch (ParseException e) {
			return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "数据格式错误");
		}
		return ResultVOUtil.success(json);
    }
    
    /**
     * 我的订单
     * @param userWxOpenid
     * @param map
     * @return
     */
    @ApiOperation(value = "我的订单")
    @GetMapping("/user/myOrder")
    public ResultVO<Map<String, String>> myOrderList(
    		@ApiParam(value = "用户openId") @RequestParam(required = true, value = "userWxOpenid") String userWxOpenid,
    		@ApiParam(value = "订单状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成 6:已评价 7:退款中 8:已退款 9：已删除）") @RequestParam(required = false, value = "orderStatus") String orderStatus,
    		 @RequestParam(value = "page", defaultValue = "0") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size,
    		HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	
    	User user = userService.selectByUserWxOpenid(userWxOpenid);
    	if(null == user){
    		return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到该用户");
    	}
    	OrderMaster orderMaster = new OrderMaster();
    	orderMaster.setBuyerOpenid(userWxOpenid);
    	orderMaster.setOrderStatus(orderStatus);
    	
    	Sort sort = new Sort(Direction.DESC, "created");
		PageRequest pageable = new PageRequest(page, size,sort);
    	Page<OrderMaster> pageList = orderService.findOrderMasterPage(orderMaster, null, pageable);
    	
    	if(null == pageList || pageList.getContent().isEmpty()){
    		return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到数据");
		}
    	
    	List<Long> orderIds = Lists.newArrayList();
		for (OrderMaster orderMaster_C : pageList) {
			orderIds.add(Long.valueOf(orderMaster_C.getOrderNo()));
		}
		
		//根据订单获取所有商品信息
		OrderDetail orderDetail = new OrderDetail();
		List<OrderDetail> orderDetailList = orderService.findAll(orderDetail,null,orderIds,null,null);
		if(null == orderDetailList || orderDetailList.isEmpty()){
			return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "未找到数据");
		}
		List<Long> productIds = Lists.newArrayList(); 
		for (OrderDetail orderDetail2 : orderDetailList) {
			productIds.add(orderDetail2.getProductId());
		}
		
		List<ProductInfo> list = productService.findListByIds(productIds);
		JSONObject relutJson = new JSONObject();
		List<JSONObject> jsonObjectList = Lists.newArrayList();
		for (OrderMaster orderMaster_S : pageList) {
			JSONObject json = (JSONObject) JSONObject.toJSON(orderMaster_S);
			List<JSONObject> productList = Lists.newArrayList();
			for (OrderDetail orderDetail_S : orderDetailList) {
				if(orderMaster_S.getOrderNo().toString().equals(orderDetail_S.getOrderId().toString())){
					for (ProductInfo productInfo : list) {
						if(productInfo.getId().toString().equals(orderDetail_S.getProductId().toString())){
							JSONObject jsonProductInfo = (JSONObject) JSONObject.toJSON(productInfo);
							jsonProductInfo.put("orderDetail", orderDetail_S);
							productList.add(jsonProductInfo);
						}
					}
				}
			}
			json.put("productList", productList);
			jsonObjectList.add(json);
		}
		relutJson.put("content", jsonObjectList);
		relutJson.put("size", pageList.getSize());
		relutJson.put("totalPages", pageList.getTotalPages());
		relutJson.put("number", pageList.getNumber());
		relutJson.put("sort", pageList.getSort());
		relutJson.put("total", pageList.getTotalElements());
		return ResultVOUtil.success(relutJson);
    }
}
