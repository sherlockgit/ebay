package com.winit.dataobject;

import com.winit.utils.DateUtil;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/*
* 订单主表
 */

@Data
@Entity
public class OrderMaster {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 买家名字
     */
    private String buyerName;

    /**
     * 买家电话
     */
    private String buyerPhone;

    /**
     * 买家地址
     */
    private String buyerAddress;

    /**
     * 买家微信OPENID
     */
    private String buyerOpenid;

    /**
     * 订单总金额
     */
    private BigDecimal orderAmount;
    
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
     * 订单状态（1:待支付 2:已取消 3:已支付 4:已发货 5:已完成 6:已评价 7:退款中 8:已退款 9：已删除）
     */
    private String orderStatus;

    /**
     *支付状态, 默认未支付
     */
    private Byte payStatus;

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
     * 收货人姓名
     */
    private String cneeName;

    /**
     * 收货人号码
     */
    private String cneePhone;

    /**
     * 收货人身份证
     */
    private String cneeIdcard;

    /**
     * 收货人地址
     */
    private String cneeAddress;

    /**
     * ebay商品ID
     */
    private String itemId;
    /**
     * 组织ID
     */
    private Long organizationId;
    
    /**
     * 订单组编号
     */
    private String orderGroupNo;
    

    /**
     * 是否有效(Y 有效  N 无效)
     */
    private String isActive ="Y";

    /**
     * 是否删除(Y 删除 N 未删除)
     */
    private String isDelete="N";
    
    /**
     * 物流编号
     */
    private String logisticsNo;

    /**
     * 创建时间
     */
    private Date created = new Date();

    /**
     * 修改时间
     */
    private Date updated = new Date();

    /**
     * 预计到货时间
     */
    private Date arrivalTime;

    /**
     * 创建者
     */
    private String createdby;

    /**
     * 修改者
     */
    private String updatedby;

    /**
     * 商品名称
     */
    private String productName;
}