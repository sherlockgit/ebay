package com.winit.enums;

import lombok.Getter;

/**
 * Created by liyou
 * 2017-06-11 17:12
 * 订单状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成 6:已评价 7:退款中 8:已退款 9：已删除）
 */
@Getter
public enum OrderStatusEnum implements CodeEnum {
	UNPAID(1, "待支付"),
    CANCEL(2, "已取消"),
    PAIDED(3, "已支付"),
    DELIVERED(4, "已发货"),
    FINISHED(5, "已完成"),
    EVALUATED(6, "已评价"),
    REFUNDING(7, "退款中"),
    REFUNDED(8, "已退款"),
    DELETED(9, "已删除"),
    ;

    private Integer code;

    private String message;

    OrderStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
