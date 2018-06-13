package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/*
* 订单物流信息表
 */
@Data
@Entity
public class OrderLogistics {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 物流信息
     */
    private String logisticsInfo;

    /**
     * 组织ID
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