package com.winit.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OSS基本常量
 * author: 小宇宙
 * date: 2018/4/15
 */
@Component
public class OSSConfig {

    /**
     * 阿里云API的外网域名
     */
    public static String endPoint;

    /**
     * 阿里云API的密钥Access Key ID
     */
    public static String accessKeyId;

    /**
     * 阿里云API的密钥Access Key Secret
     */
    public static String accessKeySecret;

    /**
     * 阿里云API的bucket名称
     */
    public static String backetName;

    /**
     * 阿里云API的文件夹名称
     */
    public static String folder;

    @Value("${oss.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        OSSConfig.accessKeyId = accessKeyId;
    }

    @Value("${oss.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        OSSConfig.accessKeySecret = accessKeySecret;
    }

    @Value("${oss.backetName}")
    public void setBacketName(String backetName) {
        OSSConfig.backetName = backetName;
    }

    @Value("${oss.folder}")
    public void setFolder(String folder) {
        OSSConfig.folder = folder;
    }

    @Value("${oss.endPoint}")
    public void setEndPoint(String endPoint) {

        OSSConfig.endPoint = endPoint;
    }
}
