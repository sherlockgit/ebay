package com.winit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.winit.config.CreditCard;
import com.winit.config.EbayConfig;
import com.winit.config.ShippingAddressConfig;
import com.winit.constant.TokenConstant;
import com.winit.dataobject.LocalizedAspects;
import com.winit.dataobject.OrderDetail;
import com.winit.dataobject.OrderMaster;
import com.winit.enums.ItemTypeEnum;
import com.winit.service.EbayService;
import com.winit.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

@Service
@Slf4j
public class EbayServiceImpl implements EbayService{

    @Autowired
    EbayConfig ebayConfig;

    @Autowired
    ShippingAddressConfig shippingAddressConfig;

    @Autowired
    CreditCard creditCard;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 获取商品信息 包含组合商品
     * @param itemId
     * @return
     */
    @Override
    public JSONObject getGoodsInfo(String itemId) {

        String itemIdTmp = itemId;
        JSONObject jsonObject = getGroupGoods(itemId);

        if(null != jsonObject.get("errors")){

            //表示处理单属性商品
            itemId = "v1|"+itemId+"|0";
            jsonObject = getGoods(itemId);

            if(null != jsonObject.get("errors")){
                jsonObject = new JSONObject();
                jsonObject.put("errors", "product_id_not_found");

            }else {
                JSONArray jsonArray = jsonObject.getJSONArray("buyingOptions");
                String buyingOptions = jsonArray.getString(0);
                if(!buyingOptions.equals("FIXED_PRICE")){
                    jsonObject = new JSONObject();
                    jsonObject.put("errors", "product_is_not_fixed_price");
                }else{
                    jsonObject = dealGoodsInfo(jsonObject);
                    if(null == jsonObject.get("price")){
                        jsonObject = new JSONObject();
                        jsonObject.put("errors", "product_id_not_found");
                    }else{
                        jsonObject.put("itemType", ItemTypeEnum.SINGLE_ITEM.getCode());
                        jsonObject.put("itemId", itemIdTmp);
                    }
                }
            }
            return jsonObject;

        }else{

            //表示处理多属性商品
            jsonObject = dealGroupInfo(itemId, jsonObject);
            jsonObject.put("itemType", ItemTypeEnum.GROUP_ITEM.getCode());
            jsonObject.put("itemId", itemIdTmp);
            return jsonObject;

        }
    }

    /**
     * 处理单属性商品
     * @param jsonObject
     * @return
     */
    private JSONObject dealGoodsInfo(JSONObject jsonObject){

        JSONObject result = new JSONObject();
        result.put("title", jsonObject.get("title"));
        result.put("shortDescription", jsonObject.get("shortDescription"));
        result.put("price", jsonObject.get("price"));
        result.put("itemLocation", jsonObject.get("itemLocation"));
        result.put("image", jsonObject.get("image"));
        result.put("brand", jsonObject.get("brand"));
        result.put("additionalImages", jsonObject.get("additionalImages"));
        result.put("localizedAspects", jsonObject.get("localizedAspects"));
        result.put("description", jsonObject.get("description"));

        //获取库存信息
        JSONObject estimatedAvailabilities = (JSONObject) jsonObject.getJSONArray("estimatedAvailabilities").get(0);
        Integer stock = getStock(estimatedAvailabilities);
        if(null == stock){
            stock = estimatedAvailabilities.getInteger("estimatedAvailableQuantity");
        }
        result.put("stock", stock);

        return result;
    }

    /**
     * 获取库存数量
     * @param estimatedAvailabilities
     * @return
     */
    private Integer getStock(JSONObject estimatedAvailabilities){
        Integer stock = estimatedAvailabilities.getInteger("availabilityThreshold");
        if(null == stock){
            stock = estimatedAvailabilities.getInteger("estimatedAvailableQuantity");
        }
        return stock;
    }

    /**
     * 处理分组商品
     * @param jsonObject
     * @return
     */
    private JSONObject dealGroupInfo(String id, JSONObject jsonObject){

        JSONObject result = new JSONObject();
        JSONArray items = jsonObject.getJSONArray("items");

        Map<String, Object> localizedAspects = new HashMap<String, Object>();
        Map<String, Object> priceAndStocks = new HashMap<String, Object>();

        for(int i=0 ; i<items.size() ; i++){

            JSONObject item = items.getJSONObject(i);
            JSONObject image = item.getJSONObject("image");

            //获取价格
            Map<String, Object> priceAndStock = new HashMap<String, Object>();
            priceAndStock.put("imageUrl", image.getString("imageUrl"));
            JSONObject price = item.getJSONObject("price");
            priceAndStock.put("price", price.get("value"));

            //获取库存
            JSONObject estimatedAvailabilities = (JSONObject) item.getJSONArray("estimatedAvailabilities").get(0);
            Integer stock = getStock(estimatedAvailabilities);
            priceAndStock.put("stock", stock);

            String itemId = item.getString("itemId");
            priceAndStocks.put(itemId, priceAndStock);

            //多属性处理
            JSONArray localizedArray = item.getJSONArray("localizedAspects");
            for(int j=0 ; j<localizedArray.size() ; j++){

                JSONObject localized = (JSONObject) localizedArray.get(j);
                String name = localized.getString("name");
                String value = localized.getString("value");
                Map nameMap = (Map) localizedAspects.get(name);

                if(null == nameMap){
                    nameMap = new HashMap<String, Object>();
                }

                List list = (List) nameMap.get(value);
                if(null == list){
                    list = new ArrayList();
                }

                list.add(itemId);
                nameMap.put(value, list);
                localizedAspects.put(name, nameMap);

            }
        }

        JSONObject itemTemp = (JSONObject) items.get(0);
        Map optionAttr = getOptionAttr(localizedAspects);
        result.put("optionAttr", optionAttr);

        Map<String, Object> itemsAttr = getItemAttr(localizedAspects);
        itemsAttr = mergeItemAttr(priceAndStocks, itemsAttr);

        JSONObject commonDescriptions = (JSONObject)(jsonObject.getJSONArray("commonDescriptions").get(0));
        result.put("title",itemTemp.get("title"));
        result.put("shortDescription",itemTemp.get("shortDescription"));
        result.put("price",itemTemp.get("price"));
        result.put("itemLocation",itemTemp.get("itemLocation"));
        result.put("image",itemTemp.getJSONObject("image"));
        result.put("description", commonDescriptions.get("description"));
        JSONObject primaryItemGroup = itemTemp.getJSONObject("primaryItemGroup");
        result.put("additionalImages", primaryItemGroup.get("itemGroupAdditionalImages"));

        //去掉个性化固有属性
        JSONArray localizedAspectsArray = itemTemp.getJSONArray("localizedAspects");
        deleteLocalizedAspects(optionAttr.keySet(), localizedAspectsArray);
        result.put("localizedAspects",localizedAspectsArray);
        result.put("itemsAttr",itemsAttr);
/*        String itemAttrString = JSON.toJSONString(itemsAttr);
        redisTemplate.boundValueOps(id).set(itemAttrString);*/

        return result;
    }

    private void deleteLocalizedAspects(Set<String> value, JSONArray localizedAspects){

        for(int i=0 ; i<localizedAspects.size(); i++){
            JSONObject attr = (JSONObject) localizedAspects.get(i);
            if(value.contains(attr.get("name"))){
                localizedAspects.remove(attr);
                i--;
            }
        }

    }

    /**
     * 获取组合商品信息 取一次就会被销毁
     * @param id
     * @return
     */
    public JSONObject getItemInfoById(String id){

        String result = (String) redisTemplate.boundValueOps(id).get();
        redisTemplate.delete(id);
        JSONObject itemInfo = null;
        if(null != result){
            itemInfo = JSON.parseObject(result);
        }
        return itemInfo;
    }

    /**
     *
     * @param orderNo
     * @return
     */
    @Override
    public JSONObject createEbayOrder(String orderNo) {

        Map<String, Object> parameter = new HashMap<String, Object>();

        List<OrderMaster> list = orderService.findOrderMasterList(orderNo);
        if(null==list || list.size()==0){
            list = orderService.findOrderMasterByOrderNo(orderNo);
        }

        Map<String, Object> shippingAddress = new HashMap<String, Object>();
        shippingAddress.put("addressLine1", shippingAddressConfig.addressLine1);
        shippingAddress.put("city", shippingAddressConfig.city);
        shippingAddress.put("country", shippingAddressConfig.country);
        shippingAddress.put("phoneNumber", shippingAddressConfig.phoneNumber);
        shippingAddress.put("postalCode", shippingAddressConfig.postalCode);
        shippingAddress.put("recipient", shippingAddressConfig.recipient);
        shippingAddress.put("stateOrProvince", shippingAddressConfig.stateOrProvince);
        parameter.put("shippingAddress", shippingAddress);

        List<String> orderNoList = new ArrayList<String>();
        for(OrderMaster orderMaster : list){
            orderNoList.add(orderMaster.getOrderNo());
        }

        List<OrderDetail> orderDetailList = orderService.findOrderDetailList(orderNoList);
        List<Map<String, Object>> lineItemInputs = new ArrayList<Map<String, Object>>();
        for(OrderDetail orderDetail : orderDetailList){
            Map<String, Object> line = new HashMap<String, Object>();
            line.put("itemId", orderDetail.getItemId());
            line.put("quantity", orderDetail.getProductQuantity());
            lineItemInputs.add(line);
        }
        parameter.put("lineItemInputs", lineItemInputs);

        Map<String, Object> creditCards = new HashMap<String, Object>();
        creditCards.put("accountHolderName", creditCard.accountHolderName);
        creditCards.put("billingAddress", creditCard.billingAddress);
        creditCards.put("brand", creditCard.brand);
        creditCards.put("cardNumber", creditCard.cardNumber);
        creditCards.put("cvvNumber", creditCard.cvvNumber);
        creditCards.put("expireMonth", creditCard.expireMonth);
        creditCards.put("expireYear", creditCard.expireYear);
        parameter.put("creditCard", creditCards);

        HttpPost httpPost = new HttpPost(ebayConfig.checkoutSessionUrl);
        httpPost.setHeader("Authorization","Bearer Name\t"+ TokenConstant.TOKEN);
        httpPost.setHeader("Content-Type","application/json");

        // 构建消息实体
        StringEntity entity = new StringEntity(JSONObject.toJSONString(parameter), Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");
        // 发送Json格式的数据请求
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        JSONObject jsonObject = doPost(ebayConfig.checkoutSessionUrl, parameter);

        return jsonObject;
    }

    /**
     * 合并商品动态属性和价格库存
     * @param stockAndPrices
     * @param itemAttrs
     * @return
     */
    private Map mergeItemAttr(Map stockAndPrices, Map itemAttrs){

        Iterator<String> it = itemAttrs.keySet().iterator();
        while (it.hasNext()){
            String itemId = it.next();
            Map stockAndPrice = (Map) stockAndPrices.get(itemId);
            Map itemAttr = (Map) itemAttrs.get(itemId);
            itemAttr.putAll(stockAndPrice);

        }

        return itemAttrs;

    }

    /**
     * 获取item动态属性
     * @param parameter
     * @return
     */
    private Map getItemAttr(Map parameter){

        Map<String, Object> result = new HashMap<String, Object>();
        Iterator<String> it = parameter.keySet().iterator();

        while (it.hasNext()){

            String attrName = it.next();
            Map attrValueMap = (Map) parameter.get(attrName);

            if(attrValueMap.size() > 1){

                Iterator<String> nameIt = attrValueMap.keySet().iterator();

                while (nameIt.hasNext()){
                    String attrValue = nameIt.next();
                    List<String> itemIds = (List) attrValueMap.get(attrValue);

                    for(String itemId: itemIds){
                        Map itemAttr = (Map) result.get(itemId);
                        if(null == itemAttr){
                            itemAttr = new HashMap<String, Object>();

                        }
                        itemAttr.put(attrName, attrValue);
                        result.put(itemId, itemAttr);
                    }

                }
            }
        }
        return result;
    }

    /**
     * 获取选项属性
     * @param parameter
     * @return
     */
    private Map getOptionAttr(Map parameter){

        Map<String, Object> optionAttr = new HashMap<String, Object>();
        Iterator<String> it = parameter.keySet().iterator();
        while (it.hasNext()){

            String name = it.next();
            Map nameMap = (Map) parameter.get(name);

            if(nameMap.size() > 1){

                Iterator<String> nameIt = nameMap.keySet().iterator();
                List valueList = new ArrayList();
                while (nameIt.hasNext()){
                    valueList.add(nameIt.next());
                }
                optionAttr.put(name, valueList);

            }else {
                parameter.remove(name);
                it = parameter.keySet().iterator();
            }

        }

        return optionAttr;
    }

    public JSONObject getCompactGoods(String itemId){
        itemId = URLEncoder.encode(itemId);
        String url = ebayConfig.itemUrl+itemId+"?fieldgroups=COMPACT";
        HttpGet httpGet =cretateHttpGet(url);
        JSONObject jsonObject = getFromEbay(httpGet);
        return jsonObject;
    }

    private JSONObject getGoods(String itemId){
        itemId = URLEncoder.encode(itemId);
        String url = ebayConfig.itemUrl+itemId;
        HttpGet httpGet =cretateHttpGet(url);
        JSONObject jsonObject = getFromEbay(httpGet);
        return jsonObject;
    }

    private JSONObject getGroupGoods(String itemGroupId){
        itemGroupId = URLEncoder.encode(itemGroupId);
        String url = ebayConfig.itemGroupUrl+itemGroupId;
        HttpGet httpGet =cretateHttpGet(url);
        JSONObject jsonObject = getFromEbay(httpGet);
        return jsonObject;
    }

    private HttpGet cretateHttpGet(String url){
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization","Bearer Name\t"+ TokenConstant.TOKEN);
        httpGet.setHeader("Content-Type","application/json");
        return httpGet;
    }

    /**
     * post 请求
     * @param url
     * @return
     */
    private JSONObject doPost(String url, Map<String, Object> parameter){

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization","Bearer Name\t"+ TokenConstant.TOKEN);
        httpPost.setHeader("Content-Type","application/json");

        // 构建消息实体
        StringEntity entity = new StringEntity(JSONObject.toJSONString(parameter), Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");

        // 发送Json格式的数据请求
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();

            if(null == httpEntity){
                throw  new Exception();
            }

            InputStreamReader isr = new InputStreamReader(httpEntity.getContent());

            StringBuffer sb = new StringBuffer("");
            int tmp;
            tmp = isr.read();

            while (tmp>-1){
                sb.append((char)tmp);
                tmp = isr.read();
            }

            String result = sb.toString();
            JSONObject jsonObject = JSON.parseObject(result);
            return jsonObject;

        } catch (Exception e) {
            Map<String, String> result = new HashMap<String, String>();
            e.printStackTrace();
            return null;
        }

    }

    private JSONObject getFromEbay(HttpGet httpGet){

        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();

            if(null == httpEntity){
                throw  new Exception();
            }

            InputStreamReader isr = new InputStreamReader(httpEntity.getContent());

            StringBuffer sb = new StringBuffer("");
            int tmp;
            tmp = isr.read();

            while (tmp>-1){
                sb.append((char)tmp);
                tmp = isr.read();
            }

            String result = sb.toString();
            JSONObject jsonObject = JSON.parseObject(result);
            String id = (String) jsonObject.get("itemId");
            log.info(result);
            log.info(id);

            return jsonObject;

        } catch (Exception e) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("error","goods not found!");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 处理商品熟悉，翻译成中文
     * @param jsonObject
     * @return
     */
    public Object  localizedAspectsToCn(JSONObject jsonObject){
        try{

            LocalizedAspects localizedAspects= JSONObject.parseObject(jsonObject.get("localizedAspects").toString(),LocalizedAspects.class);

            localizedAspects.getName();
            return  localizedAspects;

        }catch (Exception ex){
            return  jsonObject.get("localizedAspects");
        }
    }

}
