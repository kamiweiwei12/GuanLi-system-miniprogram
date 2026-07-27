package com.inspection.dto;

import java.time.LocalDateTime;

/**
 * 统计查询参数
 */
public class StatisticsQueryDTO {

    /** 开始日期（inspection_time >= startDate） */
    private LocalDateTime startDate;

    /** 结束日期（inspection_time <= endDate） */
    private LocalDateTime endDate;

    /** 部门过滤（Service 层根据权限自动填充，不从前端接收） */
    private String department;

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
}