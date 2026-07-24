package com.inspection.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传图片文件
     *
     * @param file 上传的文件
     * @return 文件访问 URL
     */
    String upload(MultipartFile file);
}