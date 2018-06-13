package com.winit.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/*
* 基础数据码表
 */

@Data
@Entity
public class SysZcode {
    @Id
    private Long codeId;

    /**
     * 父节点
     */
    private String codePid;

    /**
     * 名称
     */
    private String codeName;

    /**
     * 英文
     */
    private String codeEn;

    /**
     * 值
     */
    private String codeValue;

    /**
     * 排序
     */
    private String codeSort;

    /**
     * 创建时间
     */
    private String crtTime;

    /**
     *
     */
    private String uptTime;

    /**
     *
     */
    private String codeIsenable;

}