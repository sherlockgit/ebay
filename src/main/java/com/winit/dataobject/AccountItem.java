package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/*
* 账户明细表
*/
@Data
@Entity
public class AccountItem {
	
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 交易编号
     */
    private String tradeNo;
    
    /**
     * 商品ID
     */
    private String productId;

    /**
     * 交易类型（0.收入 1.支出 2.提现）
     */
    private String tradeType;

    /**
     * 交易状态（0.失败 1.成功 2.处理中）
     */
    private String tradeStatus;

    /**
     * 收入金额
     */
    private String tradeInAmount;

    /**
     * 支出金额
     */
    private String tradeOutAmount;

    /**
     * 组织
     */
    private Long organizationId;

    /**
     * 是否有效(Y 有效  N 无效)
     */
    private String isActive = "Y";

    /**
     * 是否删除(Y 删除 N 未删除)
     */
    private String isDelete = "N";

    /**
     * 创建时间
     */
    private Date created = new Date();

    /**
     * 修改时间
     */
    private Date updated = new Date() ;

    /**
     * 创建者
     */
    private String createdby;

    /**
     * 修改者
     */
    private String updatedby;

}