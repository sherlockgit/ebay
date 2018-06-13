package com.winit.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.winit.VO.ResultVO;
import com.winit.dataobject.Account;
import com.winit.dataobject.AccountCheck;
import com.winit.dataobject.AccountItem;
import com.winit.dataobject.AccountWithdraw;
import com.winit.dataobject.User;
import com.winit.enums.ResultEnum;
import com.winit.service.AccountItemService;
import com.winit.service.AccountService;
import com.winit.service.UserService;
import com.winit.utils.RandomUtil;
import com.winit.utils.ResultVOUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by yhy
 * 2017-10-22 14:27
 */
@Api(tags = "账户信息")
@RestController
@RequestMapping("/user/account")
@Slf4j
@SuppressWarnings("unchecked")
public class AccountController {
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountItemService accountItemService;
    
    @Autowired
    private UserService userService;
    
	 //账户列表
	@ApiOperation(value = "会员账户列表")
    @GetMapping("list")
    public ResultVO<Map<String, String>> userAccountList(
    		@ApiParam(value = "会员名称") @RequestParam(required = false, value = "userName") String userName,
    		@ApiParam(value = "会员号码") @RequestParam(required = false, value = "userPhone") String userPhone,
    		@ApiParam(value = "待审核收益") @RequestParam(required = false, value = "pendingMoney") String pendingMoney,
    		@ApiParam(value = "交易开始时间") @RequestParam(required = false, value = "startDate") String udpateStartDate,
    		@ApiParam(value = "交易结束时间") @RequestParam(required = false, value = "endDate") String udpateEndDate,
    		@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	//获取用户信息
    	boolean isTrue = false;
    	JSONObject relutJson = new JSONObject();
    	List<User> userList = Lists.newArrayList();
    	User user = new User();
    	if(StringUtils.isNoneBlank(userName)){
			isTrue = true;
    		user.setUserName(userName);
    	}
    	if(StringUtils.isNoneBlank(userPhone)){
    		isTrue = true;
    		user.setUserPhone(userPhone);
    	}
    	//获取userId集合
    	List<String> ids = Lists.newArrayList();
    	if(isTrue){
			userList =  userService.selectList(user, null);
			if (userList.isEmpty()) {
				return ResultVOUtil.success(userList);
			}
			for (User user2 : userList) {
    			ids.add(user2.getId());
			}
    	}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd");
    	Account account = new Account();
    	Date startDate = null;
    	Date endDate = null;
    		try {
    			if(StringUtils.isNoneBlank(udpateStartDate)){
    				startDate = simpleDateFormat.parse(udpateStartDate);
    			}
    			if(StringUtils.isNoneBlank(udpateEndDate)){
    				endDate = simpleDateFormat.parse(udpateEndDate);
    			}
			} catch (ParseException e) {
				 return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
		                   "时间格式错误");
			}
    	Sort sort = new Sort(Direction.DESC, "updated");
		PageRequest pageable = new PageRequest(page, size,sort);
		Page<Account> accountList = accountService.findPage(account,ids,startDate,endDate,pageable);
    	
    	List<String> userIds = Lists.newArrayList();
		for (Account account_T : accountList) {
			userIds.add(account_T.getUserId());
		}
    	
		//注意 ： 如果没有用户姓名和手机号的过滤。需要更加账户里面的userID查一次用户信息
		if(!isTrue){
			userList = userService.selectList(userIds);
		}
		
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.DATE,   -1);
	    String yesterday = simpleDateFormat.format(calendar.getTime());
		
		AccountItem accountItem = new AccountItem();
		if(StringUtils.isNoneBlank(pendingMoney)){
			accountItem.setTradeInAmount(pendingMoney);
    	}
		accountItem.setTradeType("0");
    	accountItem.setTradeStatus("1");
    	List<AccountItem> accountItemList = accountItemService.findAll(accountItem,userIds,null,null,null);
    	List<JSONObject> list = Lists.newArrayList();
    	for (Account account_C : accountList) {
			JSONObject json = (JSONObject) JSONObject.toJSON(account_C);
			for (User user_B : userList) {
				if(account_C.getUserId().equals(user_B.getId())){
					user_B.setUserTxpwd(null);
					json.put("user", user_B);
					break;
				}
			}
	    	//昨提收益 （Yesterday）
	    	BigDecimal yIncome = new BigDecimal(Float.valueOf(0f));
	    	//历史收益（History）
	    	BigDecimal hIncome = new BigDecimal(Float.valueOf(0f));
	    	//待审核收益 （Yesterday）
	    	BigDecimal pIncome = new BigDecimal(Float.valueOf(0f));
			
			for (AccountItem c_accountItem : accountItemList) {
				if(account_C.getUserId().equals(c_accountItem.getUserId())){
					BigDecimal tradeInAmount = new BigDecimal(Float.valueOf(c_accountItem.getTradeInAmount()!= null?c_accountItem.getTradeInAmount():"0"));
					if(yesterday.equals(simpleDateFormat.format(c_accountItem.getCreated()))){
						yIncome = yIncome.add(tradeInAmount); 
					}
					//待审核收益
					if("0".equals(c_accountItem.getTradeType()) && 
							"2".equals(c_accountItem.getTradeStatus())){
						pIncome = pIncome.add(tradeInAmount); 
					}
					hIncome = hIncome.add(tradeInAmount);
				}
			}
			//昨提收益
	    	json.put("yIncome", yIncome.setScale(2,BigDecimal.ROUND_HALF_DOWN));
	    	//待审核收益
	    	json.put("pIncome", pIncome.setScale(2,BigDecimal.ROUND_HALF_DOWN));
	    	//历史收益
	    	json.put("hIncome", hIncome.setScale(2,BigDecimal.ROUND_HALF_DOWN));
	    	list.add(json);
		}
    	relutJson.put("content", list);
		relutJson.put("size", accountList.getSize());
		relutJson.put("totalPages", accountList.getTotalPages());
		relutJson.put("number", accountList.getNumber());
		relutJson.put("sort", accountList.getSort());
		relutJson.put("total", accountList.getTotalElements());
        return ResultVOUtil.success(relutJson);
    }
	
	 //会员账户详情
	@ApiOperation(value = "账户详情")
    @GetMapping("{id}")
    public ResultVO<Map<String, String>> userAccount(@PathVariable("id") String id,
    		 @RequestParam(value = "page", defaultValue = "0") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size,
             @ApiParam(value = "交易时间") @RequestParam(required = false, value = "createDate") String createDate,
             @ApiParam(value = "交易状态") @RequestParam(required = false, value = "tradeStatus") String tradeStatus,
            HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	//获取用户信息
    	User user = userService.selectOne(id);
    	if(null == user){
    		log.error("会员信息不存在");
            return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
                   "会员信息不存在");
    	}
    	JSONObject relutJson = new JSONObject();
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Account account = accountService.findByUserId(id);
    	if(null == account){
    		log.error("会员账户信息不存在");
            return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
                   "会员账户信息不存在");
    	}
    	List<JSONObject> list = Lists.newArrayList();
    	AccountItem accountItem = new AccountItem();
    	accountItem.setCreated(null);
    	accountItem.setUserId(user.getId());
    	if(StringUtils.isNoneBlank(createDate)){
    		try {
				accountItem.setCreated(simpleDateFormat.parse(createDate));
			} catch (ParseException e) {
				return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
		                   "时间格式错误");
			}
    	}
    	if(StringUtils.isNoneBlank(tradeStatus)){
    		accountItem.setTradeStatus(tradeStatus);
    	}
    	Sort sort = new Sort(Direction.DESC, "created");
		PageRequest pageable = new PageRequest(page, size,sort);
    	Page<AccountItem> accountItemList = accountItemService.findPage(accountItem,null,null,pageable);
    	for (AccountItem c_accountItem : accountItemList) {
    		JSONObject j_accountItem = (JSONObject) JSONObject.toJSON(c_accountItem);
    		j_accountItem.put("user", user);
    		j_accountItem.put("userBalance", account.getUserBalance());
    		list.add(j_accountItem);
		}
    	relutJson.put("content", list);
		relutJson.put("size", accountItemList.getSize());
		relutJson.put("totalPages", accountItemList.getTotalPages());
		relutJson.put("number", accountItemList.getNumber());
		relutJson.put("sort", accountItemList.getSort());
		relutJson.put("total", accountItemList.getTotalElements());
        return ResultVOUtil.success(relutJson);
    }
	
	 //体现列表
    @GetMapping("/withdraw/list")
    @ApiOperation(value = "体现列表")
    public ResultVO<List<AccountWithdraw>> list(
    		 @ApiParam(value = "用户名称") @RequestParam(required = false, value = "userName", defaultValue = "") String userName,
			 @ApiParam(value = "用户号码") @RequestParam(required = false, value = "userPhone", defaultValue = "") String userPhone,
			 @ApiParam(value = "状态（0.待审核 1.已通过 2.不通过 3.暂不处理）") @RequestParam(required = false, value = "auditStatus", defaultValue = "") String auditStatus,
			 @ApiParam(value = "危险系数") @RequestParam(required = false, value = "perilRatio", defaultValue = "") String perilRatio,
             @RequestParam(value = "page", defaultValue = "0") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	JSONObject relutJson = new JSONObject();
    	AccountWithdraw accountWithdraw = new AccountWithdraw();
    	accountWithdraw.setAuditStatus(auditStatus);
    	accountWithdraw.setPerilRatio(perilRatio);
    	List<String> userIds = Lists.newArrayList();
    	if(StringUtils.isNoneBlank(userName) || StringUtils.isNoneBlank(userPhone)){
    		User user = new User();
    		user.setUserName(userName);
    		user.setUserPhone(userPhone);
    		List<User> userList = userService.selectList(user, null);
			if (userList.isEmpty()) {
				return ResultVOUtil.success(userList);
			}
			for (User user_c : userList) {
    			userIds.add(user_c.getId());
    		}
    	}
    	Sort sort = new Sort(Direction.DESC, "created");
        PageRequest request = new PageRequest(page, size,sort);
        Page<AccountWithdraw> awPage = accountService.findAWPage(accountWithdraw,userIds,request);
        List<String> userIdList = Lists.newArrayList();
        List<String> tradeNoList = Lists.newArrayList();
        for (AccountWithdraw accountWithdraw2 : awPage) {
        	userIdList.add(accountWithdraw2.getUserId());
        	tradeNoList.add(accountWithdraw2.getTradeNo());
		}
        
        List<User> userList = Lists.newArrayList();
        if(!userIdList.isEmpty()){
        	userList = userService.selectList(userIdList);
        }
        List<AccountItem> accountItemList = Lists.newArrayList();
        if(!userIdList.isEmpty()){
        	AccountItem accountItem = new AccountItem();
        	accountItem.setTradeType("2");
        	accountItemList = accountItemService.findAll(accountItem, null, tradeNoList, null, null);
        }
        
        List<JSONObject> jsonList = Lists.newArrayList();
        for (AccountWithdraw aw : awPage) {
        	JSONObject json = (JSONObject) JSONObject.toJSON(aw);
        	for (User user_json : userList) {
				if(user_json.getId().equals(aw.getUserId())){
					json.put("user", user_json);
				}
			}
        	for (AccountItem accountItem_T : accountItemList) {
				if(accountItem_T.getTradeNo().equals(aw.getTradeNo())){
					json.put("accountItem", accountItem_T);
				}
			}
        	jsonList.add(json);
		}
        relutJson.put("content", jsonList);
		relutJson.put("size", awPage.getSize());
		relutJson.put("totalPages", awPage.getTotalPages());
		relutJson.put("number", awPage.getNumber());
		relutJson.put("sort", awPage.getSort());
		relutJson.put("total", awPage.getTotalElements());
        return ResultVOUtil.success(relutJson);
    }
	
    //提现详细
  	@GetMapping("/withdraw/{id}")
  	@ApiOperation(value = "体现详细")
    public ResultVO<Map<String, String>> withdrawEdit(
    											@PathVariable("id") String id,
                                              HttpServletResponse response) {
	  response.setHeader("Access-Control-Allow-Origin","*");
	  AccountWithdraw accountWithdraw = accountService.withdrawSelectOne(id);
	  User user = userService.selectOne(accountWithdraw.getUserId());
	  JSONObject json = (JSONObject) JSONObject.toJSON(accountWithdraw);
	  json.put("user", user);
      return ResultVOUtil.success(json);
    }
    
    //提现编辑
  	@PostMapping("/withdraw/{id}/edit")
  	@ApiOperation(value = "体现审核")
    public ResultVO<Map<String, String>> withdrawEdit(@RequestBody AccountWithdraw accountWithdraw,
    											@PathVariable("id") String id,
                                              BindingResult bindingResult,
                                              HttpServletResponse response) {
	  response.setHeader("Access-Control-Allow-Origin","*");
	  AccountWithdraw accountWithdraw_T = accountService.withdrawSelectOne(id);
	  if(null == accountWithdraw_T){
		  return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
                  "未找到提现信息");
	  }
	  //状态（
	  if(StringUtils.isNoneBlank(accountWithdraw.getAuditStatus())) {
		  if("0".equals(accountWithdraw_T.getAuditStatus())){
			  //审核中 到审核不通过
			  Account account = accountService.findByUserId(accountWithdraw.getUserId());
			  if("2".equals(accountWithdraw.getAuditStatus())){
				  BigDecimal userBalance = new BigDecimal(Float.valueOf(0f));
				  userBalance = account.getUserBalance().add(accountWithdraw.getDrawAmount());
				  account.setUserBalance(userBalance);
			  }
			  //审核占不处理，钱先冻结
			  if("3".equals(accountWithdraw.getAuditStatus())){
				  BigDecimal userDrawable = new BigDecimal(Float.valueOf(0f));
				  userDrawable = account.getUserDrawable().add(accountWithdraw.getDrawAmount());
				  account.setUserDrawable(userDrawable);
			  }
			  accountService.create(account);
		  }
		  accountWithdraw_T.setAuditStatus(accountWithdraw.getAuditStatus());
	  }
	  //危险系数
	  if(StringUtils.isNoneBlank(accountWithdraw.getPerilRatio())) {
		  accountWithdraw_T.setPerilRatio(accountWithdraw.getPerilRatio());
	  }
	  //审核意见
	  if(StringUtils.isNoneBlank(accountWithdraw.getAuditMemo())) {
		  accountWithdraw_T.setAuditMemo(accountWithdraw.getAuditMemo());
	  }
	  //删除
	  if(StringUtils.isNoneBlank(accountWithdraw.getIsDelete())) {
		  accountWithdraw_T.setIsDelete(accountWithdraw.getIsDelete());
	  }
	  //禁用
	  if(StringUtils.isNoneBlank(accountWithdraw.getIsActive())) {
		  accountWithdraw_T.setIsActive(accountWithdraw.getIsActive());
	  }
	  //修改者
	  if(StringUtils.isNoneBlank(accountWithdraw.getUpdatedby())) {
		  accountWithdraw_T.setUpdatedby(accountWithdraw.getUpdatedby());
	  }
	  accountService.editWithdraw(accountWithdraw_T);
      Map<String, String> map = new HashMap<>();
      map.put("id", accountWithdraw_T.getId());
      return ResultVOUtil.success(map);
  }
  	
  	 //创建
	@PostMapping("/withdraw/create")
	@ApiOperation(value = "发起提现")
    public ResultVO<Map<String, String>> create(
    		@ApiParam(value = "用户ID") @RequestParam(required = true, value = "userId") String userId,
    		@ApiParam(value = "提现金额") @RequestParam(required = false, value = "drawAmount",defaultValue = "0") BigDecimal drawAmount,
                                                HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		if(StringUtils.isBlank(userId)){
			return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
	                  "会员id不能为空");
		}
		User user = userService.selectOne(userId);
		if(user == null){
			return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
	                  "会员信息不存在");
		}
		Account account = accountService.findByUserId(user.getId());
		if(null == account){
			return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
	                  "会员账户不存在");
		}
		if(drawAmount.equals(BigDecimal.ZERO)){
			return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
	                  "提现金额不能为 0");
		}
		if(drawAmount.compareTo(account.getUserBalance()) == 1){
			return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
	                  "提现金额大于账户余额");
		}
		//跟新用户余额
		BigDecimal userBalance = new BigDecimal(Float.valueOf(0f));
		userBalance = account.getUserBalance().subtract(drawAmount);
		account.setUserBalance(userBalance);
		accountService.create(account);
		AccountWithdraw accountWithdraw = new AccountWithdraw();
		accountWithdraw.setUserId(userId);
		accountWithdraw.setDrawAmount(drawAmount);
		accountWithdraw.setUserBalance(account.getUserBalance());
		accountWithdraw.setAuditStatus("0");
		accountWithdraw.setTradeNo(RandomUtil.generateString(16));
		accountService.editWithdraw(accountWithdraw);
        Map<String, String> map = new HashMap<>();
        map.put("id", accountWithdraw.getId());
        return ResultVOUtil.success(map);
    }
  	
  	 //对账管理
    @GetMapping("/check/list")
    @ApiOperation(value = "对账管理")
    public ResultVO<List<AccountCheck>> accountChecklist(
    		 @ApiParam(value = "交易编号") @RequestParam(required = false, value = "tradeNo", defaultValue = "") String tradeNo,
			 @ApiParam(value = "用户姓名") @RequestParam(required = false, value = "userName", defaultValue = "") String userName,
			 @ApiParam(value = "核对结果") @RequestParam(required = false, value = "", defaultValue = "") String auditStatus,
			 @ApiParam(value = "交易开始时间【yyyy-MM-dd HH:mm:ss】") @RequestParam(required = false, value = "startDate", defaultValue = "") String startDate,
			 @ApiParam(value = "交易结束时间【yyyy-MM-dd HH:mm:ss】") @RequestParam(required = false, value = "endDate", defaultValue = "") String endDate,
             @RequestParam(value = "page", defaultValue = "0") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin","*");
    	JSONObject relutJson = new JSONObject();
    	AccountCheck accountCheck = new AccountCheck();
    	accountCheck.setTradeNo(tradeNo);
    	accountCheck.setCheckStatus(auditStatus);
    	
    	List<String> userIds = Lists.newArrayList();
    	if(StringUtils.isNoneBlank(userName)){
    		User user = new User();
    		user.setUserName(userName);
    		List<User> userList = userService.selectList(user, null);
    		for (User user_c : userList) {
    			userIds.add(user_c.getId());
    		}
    	}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
    	Date startDate_T = null;
    	Date endDate_T = null;
		try {
			if(StringUtils.isNoneBlank(startDate)){
				startDate_T = simpleDateFormat.parse(startDate);
			}
			if(StringUtils.isNoneBlank(endDate)){
				endDate_T = simpleDateFormat.parse(endDate);
			}
		} catch (ParseException e) {
			 return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),
	                   "时间格式错误");
		}
    	Sort sort = new Sort(Direction.DESC, "created");
        PageRequest pageable = new PageRequest(page, size,sort);
        Page<AccountCheck> ackPage = accountItemService.findCheckPage(accountCheck, startDate_T, endDate_T, pageable);
        
        List<String> tradeNoList = Lists.newArrayList();
        for (AccountCheck accountCheck2 : ackPage) {
        	tradeNoList.add(accountCheck2.getTradeNo());
		}
        
        AccountItem accountItem = new AccountItem();
        
        List<AccountItem> accountItemList = accountItemService.findAll(accountItem, userIds, tradeNoList, null, null);
        List<String> userIds_T = Lists.newArrayList();
        for (AccountItem accountItem2 : accountItemList) {
        	userIds_T.add(accountItem2.getUserId());
		}
        List<User> userList = Lists.newArrayList();
        if(!userIds_T.isEmpty()){
        	userList = userService.selectList(userIds_T);
        }
        List<JSONObject> jsonList = Lists.newArrayList();
        for (AccountCheck ack : ackPage) {
        	JSONObject json = (JSONObject) JSONObject.toJSON(ack);
        	for (AccountItem accountItem_T : accountItemList) {
        		if(accountItem_T.getTradeNo().equals(ack.getTradeNo())){
        			json.put("accountItem", accountItem_T);
        			for (User user : userList) {
        				if(user.getId().equals(accountItem_T.getUserId())){
        					json.put("user", user);
    					}
					}
        		}
        	}
        	jsonList.add(json);
		}
        relutJson.put("content", jsonList);
		relutJson.put("size", ackPage.getSize());
		relutJson.put("totalPages", ackPage.getTotalPages());
		relutJson.put("number", ackPage.getNumber());
		relutJson.put("sort", ackPage.getSort());
		relutJson.put("total", ackPage.getTotalElements());
        return ResultVOUtil.success(relutJson);
    }
    
    
    //对账处理
   	@PostMapping("/check/{id}/edit")
   	@ApiOperation(value = "对账处理")
     public ResultVO<Map<String, String>> checkEdit(@RequestBody AccountCheck accountCheck,
     											@PathVariable("id") String id,
                                               BindingResult bindingResult,
                                               HttpServletResponse response) {
 	  response.setHeader("Access-Control-Allow-Origin","*");
 	  AccountCheck accountCheck_T = accountItemService.selectOneCheck(id);
 	  if(null == accountCheck_T){
 		  return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(),
                   "未找到对账信息");
 	  }
 	  if(StringUtils.isNoneBlank(accountCheck.getCheckStatus())){
 		 accountCheck_T.setCheckStatus(accountCheck.getCheckStatus());
 	  }
 	  if(StringUtils.isNoneBlank(accountCheck.getEbayStatus())){
 		 accountCheck_T.setEbayStatus(accountCheck.getEbayStatus());
 	  }
 	  if(StringUtils.isNoneBlank(accountCheck.getPlatformStatus())){
 		  accountCheck_T.setPlatformStatus(accountCheck.getPlatformStatus());
 	  }
 	  if(StringUtils.isNoneBlank(accountCheck.getHandleMemo())){
 		  accountCheck_T.setHandleMemo(accountCheck.getHandleMemo());
 	  }
 	  if(null != accountCheck.getPlatformTime()){
 		  accountCheck_T.setPlatformTime(accountCheck.getPlatformTime());
 	  }
 	  if(null != accountCheck.getEbayTime()){
 		  accountCheck_T.setEbayTime(accountCheck.getEbayTime());
 	  }
 	  if(StringUtils.isNoneBlank(accountCheck.getIsActive())){
		  accountCheck_T.setIsActive(accountCheck.getIsActive());
	  }
 	  if(StringUtils.isNoneBlank(accountCheck.getIsDelete())){
		  accountCheck_T.setIsDelete(accountCheck.getIsDelete());
	  }
 	  accountItemService.editCheck(accountCheck_T);
      Map<String, String> map = new HashMap<>();
      map.put("id", accountCheck_T.getId());
      return ResultVOUtil.success(map);
   }
}
