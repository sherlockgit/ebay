package com.winit.scheduled;

import com.winit.config.EbayConfig;
import com.winit.constant.TokenConstant;
import com.winit.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Configurable
@EnableScheduling
@Slf4j
public class TokenScheduledTasks {

    @Autowired
    EbayConfig ebayConfig;

    @Scheduled(fixedRate  = 1000*60*110 )
    public void getToken(){


        HttpPost httpPost = new HttpPost(ebayConfig.getTokenUrl());
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Authorization", ebayConfig.authorization);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type","client_credentials"));
        params.add(new BasicNameValuePair("redirect_uri",ebayConfig.redirectUri));
        params.add(new BasicNameValuePair("scope","https://api.ebay.com/oauth/api_scope"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            String result = EntityUtils.toString(httpEntity);//取出应答字符串
            Map<Object, Object> resultMap = JsonUtil.toMap(result);
            String access_token = resultMap.get("access_token").toString();
            TokenConstant.TOKEN = access_token;
            log.info(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
