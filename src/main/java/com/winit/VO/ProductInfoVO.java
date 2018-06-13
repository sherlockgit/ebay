package com.winit.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品详情
 * Created by liyou
 * 2017-05-12 14:25
 */
@Data
public class ProductInfoVO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String productNane;

    @JsonProperty("price")
    private BigDecimal productPrice;

    @JsonProperty("description")
    private String productFeatures;

    @JsonProperty("icon")
    private String productIcon;
}
