package com.winit.dto;

import lombok.Data;

/**
 * 购物车
 * Created by liyou
 * 2017-06-11 19:37
 */
@Data
public class CartDTO {

    /** 商品Id. */
    private Long productId;

    /** 数量. */
    private Integer productQuantity;

    public CartDTO(Long productId, Integer productQuantity) {
        this.productId = productId;
        this.productQuantity = productQuantity;
    }
}
