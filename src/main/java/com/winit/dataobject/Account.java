package com.winit.dataobject;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by yhy
 * 2017-06-11 17:08
 */
@Entity
@Data
public class Account{

    @Id
    @GeneratedValue
	private String id;
    
    /** 用戶ID. */
    private String userId;

    /** 用户余额 */
    private BigDecimal userBalance;

    /** 提现冻结金额*/
    private BigDecimal userDrawable;
    
    /** 组织*/
    private Long organizationId;
    
	/** 是否有效(Y 有效  N 无效)*/
    private String isActive  = "Y";
    
    /** 是否删除(Y 删除 N 未删除)*/
    private String isDelete = "N";
    
    /** 创建时间*/
    private Date created = new Date();
    
    /** 修改时间*/
    private Date updated = new Date();
    
    /** 创建者*/
    private String createdby;
    
    /** 修改者*/
    private String updatedby;

}
