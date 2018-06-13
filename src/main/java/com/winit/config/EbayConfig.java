package com.winit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ebay")
public class EbayConfig {

    public String tokenUrl;

    public String authorization;

    public String redirectUri;

    public String itemUrl;
    
    public String itemGroupUrl;

    public String checkoutSessionUrl;

    public String placeOrderUrl;

    public String updatePaymentInfoUrl;

}
