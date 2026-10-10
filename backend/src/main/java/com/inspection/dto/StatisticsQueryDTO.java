package com.inspection.dto;

import java.time.LocalDateTime;

/**
 * 统计查询参数（支持权限过滤 + 区域/设备/时间范围筛选）
 */
public class StatisticsQueryDTO {

    /** 开始日期（inspection_time >= startDate） */
    private LocalDateTime startDate;

    /** 结束日期（inspection_time <= endDate） */
    private LocalDateTime endDate;

    /** 部门过滤（Service 层根据权限自动填充，不从前端接收） */
    private String department;

    /** 用户 ID 过滤（Service 层 EMPLOYEE 角色自动填充，不从前端接收） */
    private Long userId;

    /** 区域过滤（可选，前端传入） */
    private String area;

    /** 设备名称过滤（可选，前端传入） */
    private String deviceName;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
