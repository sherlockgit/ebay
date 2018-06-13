package com.winit.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.winit.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.winit.VO.ResultVO;
import com.winit.dataobject.OrderLogistics;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import com.winit.service.OrderLogisticsService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单物流信息接口
 * Created by liyou
 * 2017-06-18 23:27
 */
@RestController
@RequestMapping("/ebay/logistics")
@Slf4j
@SuppressWarnings("unchecked")
@ConfigurationProperties(  prefix = "winitTracking" )
public class OrderLogisticsController {

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Value("${trackUrl}")
    private String trackUrl;

    @Value("${logisticsToken}")
    private String logisticsToken;

    @Value("${logisticsAppKey}")
    private  String logisticsAppKey;

    @ApiOperation(value = "物流信息保存接口")
    @PostMapping("/save")
    public ResultVO<Map<String, String>> save(@RequestBody OrderLogistics orderLogisticsForm,
                                              BindingResult bindingResult,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin","*");
        if (bindingResult.hasErrors()) {
            log.error("【创建异常订单】参数不正确, orderForm={}", orderLogisticsForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        OrderLogistics orderLogistics = orderLogisticsService.save(orderLogisticsForm);

        Map<String, String> map = new HashMap<>();
        map.put("orderLogisticsId", orderLogistics.getId().toString());

        return ResultVOUtil.success(map);
    }

    @ApiOperation(value = "物流信息查询接口")
    @GetMapping("/list")
    public ResultVO list(
            @ApiParam(value = "订单编号") @RequestParam(required = false, value = "orderNo", defaultValue = "") String orderNo,
            @ApiParam(value = "物流信息") @RequestParam(required = false, value = "logisticsInfo", defaultValue = "") String logisticsInfo,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin","*");

        PageRequest request = new PageRequest(page, size);
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderNo(orderNo);
        orderLogistics.setLogisticsInfo(logisticsInfo);
        Page<OrderLogistics> orderLogisticsPage = orderLogisticsService.findListPage(orderLogistics, request);
        return ResultVOUtil.success(orderLogisticsPage);
    }



    @ApiOperation(value = "物流信息删除接口")
    @PostMapping("/delete")
    public ResultVO cancel(@RequestBody OrderLogistics orderLogisticsForm,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin","*");
        orderLogisticsService.delete(orderLogisticsForm);
        return ResultVOUtil.success();
    }

    @ApiOperation(value = "物流信息接口")
    @GetMapping("/order/getTracking/{logisticsNo}")
    public String  getTracking(
            @ApiParam(value = "订单编号") @PathVariable("logisticsNo") String logisticsNo,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin","*");
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setAction("oso.api.order.getTracking");
        requestMsg.setApp_key(this.logisticsAppKey);
        requestMsg.setOrderNo(logisticsNo);
        Map map = new HashMap();
        map.put("orderNo",new String(logisticsNo));
        requestMsg.setData(map);
        requestMsg.setFormat("json");
        requestMsg.setLanguage("zh_CN");
        requestMsg.setPlatform("");
        requestMsg.setSign_method("md5");
        requestMsg.setTimestamp(String.valueOf(DateUtil.getSecondTimestamp(new Date())));
        requestMsg.setVersion("1.0");
        requestMsg.setSign(this.getSign(requestMsg,this.logisticsToken));
        String retstr = HttpClientUtil.getInstance().sendHttpPost(this.trackUrl,JSONObject.toJSONString(requestMsg));
        log.info("requestMsg: ",JSONObject.toJSONString(requestMsg));

        return retstr;
    }

    public static String buildJsonStr(RequestMsg requestMsg, String logisticsToken) {

        StringBuffer buf = new StringBuffer();
        buf.append(logisticsToken);
        buf.append("action").append(requestMsg.getAction());
        buf.append("app_key").append(requestMsg.getApp_key());
        String data = "";
        if (requestMsg.getData() != null && StringUtils.isNotBlank(requestMsg.getData().toString())) {
            data = JSONObject.toJSONString(requestMsg.getData());
        }
        buf.append("data").append(data);
        buf.append("format").append(requestMsg.getFormat() == null ? "" : requestMsg.getFormat());
        buf.append("platform").append(requestMsg.getPlatform() == null ? "" : requestMsg.getPlatform());
        buf.append("sign_method").append(requestMsg.getSign_method() == null ? "" : requestMsg.getSign_method());
        buf.append("timestamp").append(requestMsg.getTimestamp() == null ? "" : requestMsg.getTimestamp());
        buf.append("version").append(requestMsg.getVersion() == null ? "" : requestMsg.getVersion());
        buf.append(logisticsToken);
        return  buf.toString();
    }

    /**
     * 按字段名字母顺序排列 对参数进行MD5加密取得签名
     *
     * @param requestMsg
     * @param token
     * @return
     */
    public static String getSign(RequestMsg requestMsg, String token) {

       String buildStr =  buildJsonStr(requestMsg, token);

      // System.out.print("decode前:"+buildStr);
       System.out.print("decode后:"+decode(buildStr));

       return MD5Util.getMd5(decode(buildStr));
    }

    /**
     * 按字段名字母顺序排列
     * @return
     */
    public static String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U'))) try {
                    retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                    i += 5;
                } catch (NumberFormatException localNumberFormatException) {
                    retBuf.append(unicodeStr.charAt(i));
                }
                else retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }
}
