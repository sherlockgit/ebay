package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/*
* 会员收货人表
 */

@Data
@Entity
public class UserCnee {
    @Id
    @GeneratedValue
    private String id;

    /**
     *会员ID
     */
    @NotEmpty(message = "会员ID必填")
    private String userId;

    /**
     *收货人姓名
     */
    @NotEmpty(message = "收货人姓名必填")
    private String cneeName;

    /**
     *收货人号码
     */
    @NotEmpty(message = "收货人号码必填")
    private String cneePhone;

    /**
     *收货人身份证
     */
    private String cneeIdcard;

    /**
     *收货人地址
     */
    @NotEmpty(message = "收货人地址必填")
    private String cneeAddress;

    /**
     *组织
     */
    private Long organizationId;

    /**
     *是否有效(Y 有效  N 无效)
     */
    private String isActive = "Y";

    /**
     *是否删除(Y 删除 N 未删除)
     */
    private String isDelete = "N";
    
    /**
     *是否为 默认地址（ Y 默认  N 无效）
     */
    private String isDefaute = "N";

    /**
     *创建时间
     */
    private Date created = new Date();

    /**
     *修改时间
     */
    private Date updated = new Date();

    /**
     *创建者
     */
    private String createdby;

    /**
     *修改者
     */
    private String updatedby;

}