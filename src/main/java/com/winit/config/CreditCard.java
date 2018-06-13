package com.winit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by liyou
 * 2017-07-03 01:31
 */
@Data
@Component
@ConfigurationProperties(prefix = "creditCard")
public class CreditCard {

    public String accountHolderName;

    public Map<String, Object> billingAddress;

    public String brand;

    public String cardNumber;

    public String cvvNumber;

    public String expireMonth;

    public String expireYear;


}
