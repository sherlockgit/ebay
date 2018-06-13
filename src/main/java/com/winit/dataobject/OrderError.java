package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

/*
*   订单异常表
*
*/
@Data
@Entity
@DynamicUpdate
public class OrderError {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 异常编号
     */
    private String errorNo;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 异常类型
     */
    private String errorType;

    /**
     * 异常状态（0:待解决 1:已解决 2:未解决 3:挂起）
     */
    private String errorStatus;

    /**
     * 异常描述
     */
    @Column(nullable=false)
    private String errorMemo;

    /**
     * 解决说明
     */
    private String sloveMemo;

    /**
     * 最后处理人
     */
    private String handerby;

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
    private Date updated = new Date();

    /**
     * 修改者
     */
    private String createdby;

    /**
     * 创建者
     */
    private String updatedby;

}