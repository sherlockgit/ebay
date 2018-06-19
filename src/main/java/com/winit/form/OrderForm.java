package com.winit.form;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.winit.dataobject.OrderDetail;

/**
 * Created by liyou
 * 2017-06-18 23:31
 */
@Data
public class OrderForm {

	/**
     * 订单ID
     */
    private Long orderMasterId;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 买家姓名
     */
    @NotEmpty(message = "姓名必填")
    private String name;

    /**
     * 买家手机号
     */
    @NotEmpty(message = "手机号必填")
    private String phone;

    /**
     * 买家地址
     */
    @NotEmpty(message = "地址必填")
    private String address;

    /**
     * 买家微信openid
     */
    @NotEmpty(message = "openid必填")
    private String openid;

    /**
     * 购物车
     */
    @NotEmpty(message = "购物车不能为空")
    private List<OrderDetail> items;
    
    /** 运费. */
    private BigDecimal carriage;

    /** 税费. */
    private BigDecimal taxFee;

    /**
     * EBAY订单号
     */
    private String ebayNo;

    /**
     * 商品名称
     */
    @NotEmpty(message = "商品名称必填")
    private  String productName;

    /**
     * EBAY状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成-海外仓已签收）
     */
    private String ebayStatus;

    /**
     * 物流状态（0:海外已入库 1:海外已出仓 3:清关中 4:派送中 5:已签收）
     */
    private String logisticsStatus;

    /**
     * 收货人姓名
     */
    private String cneeName;

    /**
     * 收货人号码
     */
    private String cneePhone;

    /**
     * 收货人身份证号码
     */
    private String cneeIdcard;

    /**
     * 收货人地址
     */
    private String cneeAddress;
    /**
     * 订单总金额
     */
    private BigDecimal orderAmount;

    /**
     * 支付订单Ebay商品总价(不含运费)
     */
    private BigDecimal ebayAmount;
}
