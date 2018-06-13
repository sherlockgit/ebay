package com.winit.VO;

import lombok.Data;

import java.util.Date;

@Data
public class OrderErrorVo {


    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 最后处理人
     */
    private String handerby;



    private Long id;

    /**
     * 异常编号
     */
    private String errorNo;

    /**
     * 异常类型
     */
    private String errorType;

    /**
     * 异常状态（0:待解决 1:已解决 2:未解决 3:挂起）
     */
    private String errorStatus;

    /**
     * 异常说明
     */
    private String errorMemo;

    /**
     * 解决说明
     */
    private String sloveMemo;

    /**
     * 处理方式
     */
    private String handerType;

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
     * 创建者
     */
    private String createdby ;

    /**
     * 修改者
     */
    private String updatedby;
}
