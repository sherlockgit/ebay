package com.winit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "shippingAddress")
public class ShippingAddressConfig {

    public String recipient;

    public String phoneNumber;

    public String addressLine1;

    public String city;

    public String stateOrProvince;

    public String postalCode;

    public String country;

}
