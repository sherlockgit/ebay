package com.winit.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by liyou
 * 2017-06-11 17:08
 */
@Entity
@Data
@DynamicUpdate
public class User{

    @Id
    @GeneratedValue
	private String id;
    
    /** 用户姓名. */
    @NotEmpty(message = "姓名必填")
    private String userName;

    /** 用户手机号码. */
    //@NotEmpty(message = "手机号必填")
    private String userPhone;

    /**会员类型(1:分销商、2:普通用户)**/
    private String userCtype;
    
    /**地址**/
    private String userAddr;
    
    /**提现密码**/
    private String userTxpwd;
    
    /**微信号名称**/
    private String userWxName;
    
    /**微信号(OPENID)**/
    private String userWxOpenid;
    
    /**用户图片**/
    private String userWxPicture;
    
    /** 组织*/
    private Long organizationId;
    
	/** 是否有效(Y 有效  N 无效)*/
    private String isActive = "Y";
    
    /** 是否删除(Y 删除 N 未删除)*/
    private String isDelete = "N";
    
    /** 创建时间*/
    private Date created = new Date();
    
    /** 修改时间*/
    private Date updated  = new Date();
    
    /** 创建者*/
    private String createdby;
    
    /** 修改者*/
    private String updatedby;

}
