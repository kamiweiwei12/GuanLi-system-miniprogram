package com.inspection.dto;

import java.time.LocalDateTime;

/**
 * AI 报告请求 DTO — POST /api/v1/ai/report
 */
public class AiReportRequestDTO {

    /** 开始日期 */
    private LocalDateTime startDate;

    /** 结束日期 */
    private LocalDateTime endDate;

    /** 报告类型：DAILY / WEEKLY / MONTHLY */
    private String reportType;

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
}