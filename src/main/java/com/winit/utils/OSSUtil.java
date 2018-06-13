package com.winit.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.winit.config.OSSConfig;
import com.winit.enums.ResultEnum;
import com.winit.exception.SellException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 阿里云文件上传
 * author: 小宇宙
 * date: 2018/4/15
 */
@Slf4j
public class OSSUtil {

    //阿里云API的内或外网域名
    private static String ENDPOINT;
    //阿里云API的密钥Access Key ID
    private static String ACCESS_KEY_ID;
    //阿里云API的密钥Access Key Secret
    private static String ACCESS_KEY_SECRET;
    //阿里云API的bucket名称
    private static String BACKET_NAME;
    //阿里云API的文件夹名称
    private static String FOLDER;

    //初始化属性
    static{
        ENDPOINT = OSSConfig.endPoint;
        ACCESS_KEY_ID = OSSConfig.accessKeyId;
        ACCESS_KEY_SECRET = OSSConfig.accessKeySecret;
        BACKET_NAME = OSSConfig.backetName;
        FOLDER = OSSConfig.folder;
    }

    /**
     * 获取阿里云OSS客户端对象
     * @return ossClient
     */
    public static OSSClient getOSSClient(){
        return new OSSClient(ENDPOINT,ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    /**
     * 上传图片至OSS
     * @param ossClient  oss连接
     * @param file
     * @return String 返回的唯一MD5数字签名
     * */
    public static  String uploadObject2OSS(OSSClient ossClient, MultipartFile file, String folder) {
        String bucketName = BACKET_NAME;
        String resultStr = null;
        String newFileName = null;
        try {
            //以输入流的形式上传文件
            InputStream is = file.getInputStream();
            //文件名
            String fileName = file.getOriginalFilename();
            // 获取图片的扩展名
            String extensionName = StringUtils.substringAfter(fileName, ".");
            // 新的图片文件名 = 获取时间戳+"."图片扩展名
            newFileName = String.valueOf(System.nanoTime()) + "." + extensionName;
            //文件大小
            Long fileSize = file.getSize();
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            //上传的文件的长度
            metadata.setContentLength(is.available());
            //指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            //指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            //指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            //指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + newFileName + "/" + fileSize + "Byte.");
            //上传文件   (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(bucketName, folder + newFileName, is, metadata);
            //解析结果
            resultStr = putResult.getETag();

        }catch (Exception e){
            e.printStackTrace();
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
            throw new SellException(ResultEnum.UPLOAD_ERROR.getCode(),ResultEnum.UPLOAD_ERROR.getMessage());
        }
        return "http://"+bucketName+"."+ENDPOINT+"/"+folder + newFileName;
    }
}
