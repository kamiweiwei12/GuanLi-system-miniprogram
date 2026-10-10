package com.inspection.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 报告响应 VO — POST /api/v1/ai/report
 * Phase 13 版本不含 AI 实际分析结果，仅返回统计摘要 + Prompt
 */
public class AiReportVO {

    /** 报告唯一 ID */
    private String reportId;

    /** 报告生成时间 */
    private LocalDateTime generatedAt;

    /** 报告周期 */
    private ReportPeriod period;

    /** 统计摘要 */
    private ReportSummary summary;

    /** Top 区域排行（按异常率降序） */
    private List<AreaStatVO> topAreas;

    /** Top 设备排行（高频巡检） */
    private List<DeviceStatVO.DeviceItem> topDevices;

    /** 异常趋势列表 */
    private List<TrendStatVO> trendData;

    /** AI 分析结果（Phase 13 为 null，未来 AI 填充） */
    private String aiAnalysis;

    /** 生成的 AI Prompt 文本（供后续 AI 调用使用） */
    private String aiPrompt;

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public ReportPeriod getPeriod() { return period; }
    public void setPeriod(ReportPeriod period) { this.period = period; }

    public ReportSummary getSummary() { return summary; }
    public void setSummary(ReportSummary summary) { this.summary = summary; }

    public List<AreaStatVO> getTopAreas() { return topAreas; }
    public void setTopAreas(List<AreaStatVO> topAreas) { this.topAreas = topAreas; }

    public List<DeviceStatVO.DeviceItem> getTopDevices() { return topDevices; }
    public void setTopDevices(List<DeviceStatVO.DeviceItem> topDevices) { this.topDevices = topDevices; }

    public List<TrendStatVO> getTrendData() { return trendData; }
    public void setTrendData(List<TrendStatVO> trendData) { this.trendData = trendData; }

    public String getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }

    public String getAiPrompt() { return aiPrompt; }
    public void setAiPrompt(String aiPrompt) { this.aiPrompt = aiPrompt; }

    /**
     * 报告周期
     */
    public static class ReportPeriod {
        private LocalDateTime start;
        private LocalDateTime end;
        private String type; // DAILY / WEEKLY / MONTHLY

        public ReportPeriod() {}
        public ReportPeriod(LocalDateTime start, LocalDateTime end, String type) {
            this.start = start;
            this.end = end;
            this.type = type;
        }

        public LocalDateTime getStart() { return start; }
        public void setStart(LocalDateTime start) { this.start = start; }

        public LocalDateTime getEnd() { return end; }
        public void setEnd(LocalDateTime end) { this.end = end; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    /**
     * 统计摘要
     */
    public static class ReportSummary {
        private long totalRecords;
        private double anomalyRate;
        private String trend; // "上升" / "下降" / "持平"

        public ReportSummary() {}
        public ReportSummary(long totalRecords, double anomalyRate, String trend) {
            this.totalRecords = totalRecords;
            this.anomalyRate = anomalyRate;
            this.trend = trend;
        }

        public long getTotalRecords() { return totalRecords; }
        public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }

        public double getAnomalyRate() { return anomalyRate; }
        public void setAnomalyRate(double anomalyRate) { this.anomalyRate = anomalyRate; }

        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
    }
}