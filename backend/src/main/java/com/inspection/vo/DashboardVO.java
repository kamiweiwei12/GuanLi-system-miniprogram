package com.inspection.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dashboard 首页 VO — GET /api/v1/dashboard
 * Phase 14: 管理员登录首页实时状态
 */
public class DashboardVO {

    /** 今日巡检总数 */
    private long todayCount;

    /** 今日异常数（故障+隐患） */
    private long todayAnomalies;

    /** 今日异常率 % */
    private double anomalyRate;

    /** 总巡检数（所有时间） */
    private long totalCount;

    /** 最近故障记录（最多5条） */
    private List<RecentRecord> recentFaults;

    /** 最近隐患记录（最多5条） */
    private List<RecentRecord> recentHiddens;

    /** 高风险区域 TOP3 */
    private List<AreaStatVO> topRiskAreas;

    public long getTodayCount() { return todayCount; }
    public void setTodayCount(long todayCount) { this.todayCount = todayCount; }

    public long getTodayAnomalies() { return todayAnomalies; }
    public void setTodayAnomalies(long todayAnomalies) { this.todayAnomalies = todayAnomalies; }

    public double getAnomalyRate() { return anomalyRate; }
    public void setAnomalyRate(double anomalyRate) { this.anomalyRate = anomalyRate; }

    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

    public List<RecentRecord> getRecentFaults() { return recentFaults; }
    public void setRecentFaults(List<RecentRecord> recentFaults) { this.recentFaults = recentFaults; }

    public List<RecentRecord> getRecentHiddens() { return recentHiddens; }
    public void setRecentHiddens(List<RecentRecord> recentHiddens) { this.recentHiddens = recentHiddens; }

    public List<AreaStatVO> getTopRiskAreas() { return topRiskAreas; }
    public void setTopRiskAreas(List<AreaStatVO> topRiskAreas) { this.topRiskAreas = topRiskAreas; }

    /**
     * 最近异常记录
     */
    public static class RecentRecord {
        private long id;
        private String deviceName;
        private String area;
        private String description;
        private LocalDateTime inspectionTime;

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public LocalDateTime getInspectionTime() { return inspectionTime; }
        public void setInspectionTime(LocalDateTime inspectionTime) { this.inspectionTime = inspectionTime; }
    }
}