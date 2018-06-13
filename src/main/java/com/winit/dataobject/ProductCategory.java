package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

/*
* 商品类目表
 */

@Data
@Entity
public class ProductCategory {

    @Id
    @GeneratedValue
    private Integer id;

    /**
     * 类目父ID
     */
    private String pid;

    /**
     * 类目名字
     */
    private String name;

    /**
     * 类目图片
     */
    private String imageUrl;

    /**
     * 是否有效
     */
    private String isValid;

    /**
     * 类目大图片
     */
    private String bigImageUrl;

    /**
     * Banner链接
     */
    @Transient
    private String clickUrl;

    /**
     * 品类运费
     */
    private String fee;

    /**
     * 类目排序
     */
    private String queue;

    /**
     * 说明
     */
    private String memo;

    /**
     * 类目级别(0,一级，支持二级目录)
     */
    private String leve;

    /**
     * 类目编号
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createTime = new Date();

    /**
     * 修改时间
     */
    private Date updateTime = new Date();

}