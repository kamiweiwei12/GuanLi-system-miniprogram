package com.inspection.entity;

import java.time.LocalDateTime;

/**
 * 操作日志实体 — sys_operation_log
 * Phase 14: 记录登录、角色修改、删除操作
 */
public class OperationLog {

    private Long id;
    private Long userId;
    private String operationType; // LOGIN / UPDATE_ROLE / DELETE_RECORD
    private String targetDesc;
    private String ip;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getTargetDesc() { return targetDesc; }
    public void setTargetDesc(String targetDesc) { this.targetDesc = targetDesc; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}