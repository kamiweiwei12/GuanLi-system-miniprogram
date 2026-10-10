package com.inspection.controller;

import com.inspection.entity.InspectionImage;
import com.inspection.mapper.InspectionImageMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * 图片安全访问控制器
 * Phase 14: 通过 imageId 返回文件流，不暴露真实文件路径
 * GET /api/v1/file/{imageId}
 *
 * TODO: Phase 15 完全移除 /uploads/** 公开映射，所有图片访问统一走此接口
 */
@RestController
@RequestMapping("/api/v1")
public class FileAccessController {

    private final InspectionImageMapper imageMapper;

    public FileAccessController(InspectionImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @GetMapping("/file/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Long imageId) {
        InspectionImage image = imageMapper.selectById(imageId);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        // image_url 格式: /uploads/yyyy/MM/dd/UUID.jpg
        String imageUrl = image.getImageUrl();
        // 转换为文件系统路径（uploads 目录在项目运行目录下或配置的 FILE_UPLOAD_PATH）
        File file = new File(imageUrl.startsWith("/") ? "." + imageUrl : imageUrl);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        // 根据扩展名设置 Content-Type
        String contentType = "image/jpeg";
        if (imageUrl.endsWith(".png")) contentType = "image/png";
        else if (imageUrl.endsWith(".gif")) contentType = "image/gif";
        else if (imageUrl.endsWith(".webp")) contentType = "image/webp";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}