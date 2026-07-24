package com.inspection.service.impl;

import com.inspection.exception.BusinessException;
import com.inspection.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传服务实现
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/jpg"
    );
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.access-prefix}")
    private String accessPrefix;

    @Override
    public String upload(MultipartFile file) {
        // 1. 文件为空校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 2. 文件类型校验
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("不支持的文件类型，仅允许 jpg/png/jpeg");
        }

        // 3. 文件大小校验
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件大小超过限制（最大 10MB）");
        }

        // 4. 生成文件名（日期分层 + UUID）
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString() + extension;
        String relativePath = today + "/" + newFileName;

        // 5. 保存文件
        try {
            Path targetDir = Paths.get(uploadPath, today);
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(newFileName);
            file.transferTo(targetFile.toFile());
            log.info("文件上传成功：{}", targetFile);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException(500, "文件保存失败");
        }

        // 6. 返回访问 URL
        return accessPrefix + "/" + relativePath;
    }
}