package com.inspection.service;

import com.inspection.entity.OperationLog;
import com.inspection.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 操作日志服务 — Controller 通过此 Service 记录日志，禁止直接调用 Mapper
 */
@Service
public class OperationLogService {

    private final OperationLogMapper logMapper;

    public OperationLogService(OperationLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    public void log(Long userId, String type, String desc, HttpServletRequest request) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setOperationType(type);
        log.setTargetDesc(desc);
        log.setIp(getClientIp(request));
        logMapper.insert(log);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }
}