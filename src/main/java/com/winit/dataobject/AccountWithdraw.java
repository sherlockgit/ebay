package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/*
* 提现管理表
*/
@Data
@Entity
public class AccountWithdraw {
    
	@Id
	@GeneratedValue
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 提现编号
     */
    private String tradeNo;

    /**
     * 提现金额
     */
    private BigDecimal drawAmount;

    /**
     * 用户余额
     */
    private BigDecimal userBalance;

    /**
     * 状态（0.待审核 1.已通过 2.不通过 3.暂不处理）
     */
    private String auditStatus;

    /**
     * 审核意见
     */
    private String auditMemo;
    
    /**
     * 危险系数（0:正常 1:低  2:中 3:高）
     */
    private String perilRatio;

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
    private Date created = new Date() ;

    /**
     * 修改时间
     */
    private Date updated = new Date();

    /**
     * 创建者
     */
    private String createdby;

    /**
     * 修改者
     */
    private String updatedby;

}