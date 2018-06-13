package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/*
* 商品数据模型表
 */

@Data
@Entity
public class ProductMode {
    @Id
    private Long id;

    /**
     * 数据模型名称
     */
    private String modeName;

    /**
     * 数据模型JSON
     */
    private String modeJson;

    /**
     * 模型类型[?]
     */
    private String modeType;

    /**
     * 商品分类
     */
    private String productType;

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
     * 修改时间（翻译时间)
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