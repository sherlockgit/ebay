package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/*
* 商品类目表
 */

@Data
@Entity
public class BannerInfo {

    @Id
    @GeneratedValue
    private Integer id;


    /**
     * Banner名称
     */
    private String name;

    /**
     * 是否有效
     */
    private String isValid;

    /**
     * Banner图片
     */
    private String imageUrl;

    /**
     * Banner链接
     */
    private String clickUrl;

    /**
     * Banner排序
     */
    private String queue;

    /**
     * memo
     */
    private String memo;

    /**
     * 创建时间
     */
    private Date createTime = new Date();

    /**
     * 修改时间
     */
    private Date updateTime = new Date();

}