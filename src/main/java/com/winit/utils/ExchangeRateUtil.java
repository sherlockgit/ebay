package com.winit.utils;

import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *  汇率
 * Created by yf on 2017-12-02.
 */

@EnableScheduling // 启用定时任务
@Component
public class ExchangeRateUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //每1小时更新一次汇率
    @Scheduled(fixedRate = 60*60*1000)
    public String getExchangeRate(){

        String exchangeRate ="7";
        String host = "https://ali-waihui.showapi.com";
        String path = "/waihui-list";
        String method = "GET";
        String appcode = "60f5977f1c2b4648b9cd297013c3a0f0";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("code","USD");

        try {

            HttpGet request = new HttpGet(host+path);
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }

            HttpResponse response = ExHttpUtils.doGet(host, path, method, headers, querys);
            String responseContext =  EntityUtils.toString(response.getEntity(), "UTF-8");
            if(responseContext!=null){
                Map map = JsonUtil.toMap(responseContext);
                if (!StringUtils.equals("0",String.valueOf(map.get("listSize")))){
                    HashMap hashMap = (HashMap) map.get("showapi_res_body");
                    JSONArray jsonArray = JSONArray.fromObject(JsonUtil.toJson(hashMap.get("list")));
                    System.out.println(jsonArray.getJSONObject(0).get("zhesuan"));
                    exchangeRate = String.valueOf(usdRate(String.valueOf(jsonArray.getJSONObject(0).get("zhesuan"))));
              }
            }

            stringRedisTemplate.opsForValue().set("exchangeRate",exchangeRate);
            return  exchangeRate;

        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }


    public double usdToCny(double usd){
        // 获取Rate
        double  f1 = 0d;
        try {
            String  exchangeRate  =  stringRedisTemplate.opsForValue().get("exchangeRate");
            if(exchangeRate==null){
//                exchangeRate =this.getExchangeRate();
            }
            Double cnyPrice = usd*Double.parseDouble(exchangeRate);
            BigDecimal b   =   new   BigDecimal(cnyPrice);
            f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }catch (Exception ex){
            ex.printStackTrace();
        }
       return  f1;
    }

    /**
     * 汇率转换/100
     * @param usdstr
     */
    public double  usdRate(String usdstr){
        double c =0d;
        try{
            BigDecimal b = new BigDecimal(usdstr);
            c = b.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }catch (Exception ex){
            return 0d;
        }
        return c;
    }
}
