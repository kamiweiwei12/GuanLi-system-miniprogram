package com.inspection.controller;

import com.inspection.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * Phase 14: 供 Docker healthcheck 使用
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/ping")
    public Result<Map<String, Object>> ping() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "ok");
        data.put("timestamp", LocalDateTime.now().toString());
        return Result.success(data);
    }
}