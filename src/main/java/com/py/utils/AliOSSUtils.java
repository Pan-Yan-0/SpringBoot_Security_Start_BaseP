package com.py.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

/**
 * 阿里云 OSS 工具类
 */
@Slf4j
@Component
public class AliOSSUtils {

//    @Value("${aliyun.oss.endpoint}")
//    private String endpoint ;
//    @Value("${aliyun.oss.accessKeyId}")
//    private String accessKeyId ;
//    @Value("${aliyun.oss.accessKeySecret}")
//    private String accessKeySecret ;
//    @Value("${aliyun.oss.bucketName}")
//    private String bucketName ;

    @Autowired
    private AliOSSProperties aliOSSProperties;

    /**
     * 实现上传图片到OSS
     */
    public String upload(MultipartFile file) throws IOException {
        //获取阿里云OSS参数
        String endpoint = aliOSSProperties.getEndpoint();
        String accessKeyId = aliOSSProperties.getAccessKeyId();
        String accessKeySecret = aliOSSProperties.getAccessKeySecret();
        String bucketName = aliOSSProperties.getBucketName();
        log.info("endpoint:" + endpoint);
        log.info("accessKeyId:" + accessKeyId);
        log.info("accessKeySecret" + accessKeySecret);
        log.info("bucketName" + bucketName);
        String string = Objects.toString(file);
        log.info(string);
        if (file.isEmpty()) {
            log.info("类型不得");
        }

        // 获取上传的文件的输入流
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            log.info("error of the file is null");
            throw e;
        }
        log.info("获取流成功！");
        // 避免文件覆盖
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        log.info("登录到oss用户成功捏！");
        ossClient.putObject(bucketName, fileName, inputStream);
        log.info("上传成功捏1！");
        //文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
        log.info("上传成功捏2！");
        // 关闭ossClient
        ossClient.shutdown();
        log.info("上传成功！url：" + url);
        return url;// 把上传到oss的路径返回
    }

    public String upload(String file) throws IOException {
        //获取阿里云OSS参数
        String endpoint = aliOSSProperties.getEndpoint();
        String accessKeyId = aliOSSProperties.getAccessKeyId();
        String accessKeySecret = aliOSSProperties.getAccessKeySecret();
        String bucketName = aliOSSProperties.getBucketName();

        // 获取上传的文件的输入流
        InputStream inputStream = new FileInputStream(file);


        // 避免文件覆盖
        // String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("
        // ."));

        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, "1.png", inputStream);

        //文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + "1.png";
        // 关闭ossClient
        ossClient.shutdown();
        return url;// 把上传到oss的路径返回
    }

}
