package com.winit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.json.Json;
import com.winit.config.EbayConfig;
import com.winit.dataobject.LocalizedAspects;
import com.winit.service.EbayService;
import com.winit.utils.JsonUtil;
import com.winit.utils.YoudaofyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonJsonParser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yf on 2017-12-04.
 */

@RestController
@RequestMapping("/ebay")
@Slf4j
public class EbayProductController {

    @Autowired
    EbayService ebayService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    EbayConfig ebayConfig;

    @GetMapping("/getProudctInfo")
    @ResponseBody
    public String getProudctInfo(@RequestParam("itemId") String itemId) {

        JSONObject jsonObject = ebayService.getGoodsInfo(itemId);
        try {
            return  this.productToCn(jsonObject);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JSON.toJSONString(jsonObject);
    }

    public  String productToCn(JSONObject jsonObject) throws  Exception{

        HashMap<Object,Object>  map = (HashMap<Object, Object>) JsonUtil.toMap(JSON.toJSONString(jsonObject));
        HashMap<Object,Object> optionAttrmap = (HashMap<Object, Object>) map.get("optionAttr");
        ArrayList localizedAspectsmap_en = (ArrayList) map.get("localizedAspects");

       //处理多属性商品
        try {

            if(map.get("title")!=null){
                String title = String.valueOf(map.get("title")).replaceAll("\"","");
                map.put("ctitle",YoudaofyUtil.youdaoFanyi(title));
            }

            if(map.get("brand")!=null){
                String brand = String.valueOf(map.get("brand")).replaceAll("\"","");;
                map.put("cbrand",YoudaofyUtil.youdaoFanyi(brand));
            }

            ArrayList localizedAspectsmap_cn = new ArrayList();
            if(localizedAspectsmap_en!=null){
                for (Object obj: localizedAspectsmap_en){
                    LocalizedAspects localizedAspects = JSONObject.parseObject(JsonUtil.toJson(obj), LocalizedAspects.class);
                    localizedAspects.setCvalue(YoudaofyUtil.youdaoFanyi(localizedAspects.getValue()));
                    localizedAspects.setCname(YoudaofyUtil.youdaoFanyi(localizedAspects.getName()));
                    localizedAspectsmap_cn.add(localizedAspects);
                }
                //替换
                map.remove("localizedAspects");
                map.put("localizedAspects",localizedAspectsmap_cn);
            }

            //追加汇率
            String exchangeRate = "6.99";
            if(stringRedisTemplate.hasKey("exchangeRate")){
                exchangeRate =  (String)stringRedisTemplate.opsForValue().get("exchangeRate");
            }
            map.put("usdRate",exchangeRate);

        }catch (Exception ex){
            //出现异常就使用原来的
            map.put("localizedAspects",localizedAspectsmap_en);
        }

       return  JsonUtil.toJson(map);
    }
}
