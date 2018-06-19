package com.winit.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.winit.dataobject.OrderDetail;
import com.winit.enums.OrderStatusEnum;
import com.winit.enums.PayStatusEnum;
import com.winit.utils.EnumUtil;
import com.winit.utils.NumberConvert;
import com.winit.utils.serializer.Date2LongSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liyou
 * 2017-06-11 18:30
 */
@Data
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {



    /** id. */
	private Long id;
    /** 订单id. */
    private String orderId;

    /**订单编号     */
    private String orderNo;

    /** 买家名字. */
    private String buyerName;

    /** 买家手机号. */
    private String buyerPhone;

    /** 买家地址. */
    private String buyerAddress;

    /** 买家微信Openid. */
    private String buyerOpenid;

    /** 订单总金额. */
    private BigDecimal orderAmount;

    /** 订单状态, 默认为0新下单. */
    private String orderStatus;

    /** 支付状态, 默认为0未支付. */
    private Byte payStatus;
    
    /** 运费. */
    private BigDecimal carriageFee;
    
    /**
     * 税费
     */
    private BigDecimal taxFee;
    /**
     * 支付订单Ebay商品总价(不含运费)
     */
    private BigDecimal ebayAmount;
    
    /**
     * EBAY订单号
     */
    private String ebayNo;

    /**
     * EBAY状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成-海外仓已签收）
     */
    private String ebayStatus;

    /**
     * 物流状态（0:海外已入库 1:海外已出仓 3:清关中 4:派送中 5:已签收）
     */
    private String logisticsStatus;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 收货人地址
     */
    private String cneeAddress;
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
     * ebay商品ID
     */
    private String itemId ;
    
    /**
     * 订单组编号
     */
    private String orderGroupNo;
    
    /**
     * 物流编号
     */
    private static  String logisticsNo;

    /**
     * 预计到货时间
     */
    private Date arrivalTime;
    
    /** 创建时间. */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    /** 更新时间. */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

    List<OrderDetail> orderDetailList = new ArrayList<>();
    
    @JsonIgnore
    public OrderStatusEnum getOrderStatusEnum() {
        return EnumUtil.getByCode(Integer.parseInt(orderStatus), OrderStatusEnum.class);
    }

    @JsonIgnore
    public PayStatusEnum getPayStatusEnum() {
        return EnumUtil.getByCode(NumberConvert.byteToInt(payStatus), PayStatusEnum.class);
    }

    public OrderDTO() {
        super();
    }

}
