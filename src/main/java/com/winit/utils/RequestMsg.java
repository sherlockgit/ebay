package com.winit.utils;

import lombok.Data;

import java.util.Map;

/**
 * Created by yf on 2017-11-30.
 */
@Data
public class RequestMsg {

   public String  action; //	接口地址	String
   public String  app_key; //	请求用户	String
   public Map<String,Object> data; //	请求参数
   public String  orderNo; //	订单号	String
   public String  format; //	业务操作	String
   public String  language; //	语种	String
   public String  platform; //	来源	String
   public String  sign; //	签名	String
   public String  sign_method; //	签名方法	String
   public String  timestamp	; //请求时间	String 格式(yyyy-MM-dd HH:mm:ss
   public String  version; //	版本号	String

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
        this.data.put("orderNo",this.getOrderNo());
    }
}
