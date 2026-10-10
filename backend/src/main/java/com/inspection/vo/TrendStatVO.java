package com.inspection.vo;

/**
 * 趋势统计 VO — GET /api/v1/statistics/trend
 * 按日/周/月统计巡检趋势，包含各结果类型数量
 */
public class TrendStatVO {

    /** 日期 (yyyy-MM-dd 或 yyyy-MM) */
    private String date;

    /** 当日总巡检数量 */
    private long totalCount;

    /** 正常数量 (result=0) */
    private long normalCount;

    /** 故障数量 (result=1) */
    private long faultCount;

    /** 隐患数量 (result=2) */
    private long hiddenCount;

    /** 异常率 %，保留两位小数 */
    private double anomalyRate;

    public TrendStatVO() {}

    public TrendStatVO(String date, long totalCount, long normalCount, long faultCount, long hiddenCount) {
        this.date = date;
        this.totalCount = totalCount;
        this.normalCount = normalCount;
        this.faultCount = faultCount;
        this.hiddenCount = hiddenCount;
        this.anomalyRate = totalCount > 0
                ? Math.round(((double)(faultCount + hiddenCount) / totalCount) * 10000.0) / 100.0
                : 0.0;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

    public long getNormalCount() { return normalCount; }
    public void setNormalCount(long normalCount) { this.normalCount = normalCount; }

    public long getFaultCount() { return faultCount; }
    public void setFaultCount(long faultCount) { this.faultCount = faultCount; }

    public long getHiddenCount() { return hiddenCount; }
    public void setHiddenCount(long hiddenCount) { this.hiddenCount = hiddenCount; }

    public double getAnomalyRate() { return anomalyRate; }
    public void setAnomalyRate(double anomalyRate) { this.anomalyRate = anomalyRate; }
}