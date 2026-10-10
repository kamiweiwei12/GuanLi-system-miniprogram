package com.inspection.vo;

/**
 * 统计总览 VO — GET /api/v1/statistics/overview
 */
public class OverviewVO {

    /** 总巡检次数 */
    private long totalRecords;

    /** 正常数量 (result=0) */
    private long normalCount;

    /** 故障数量 (result=1) */
    private long faultCount;

    /** 隐患数量 (result=2) */
    private long hiddenCount;

    /** 异常率 % = (fault + hidden) / total * 100，保留两位小数 */
    private double anomalyRate;

    public OverviewVO() {}

    public OverviewVO(long totalRecords, long normalCount, long faultCount, long hiddenCount) {
        this.totalRecords = totalRecords;
        this.normalCount = normalCount;
        this.faultCount = faultCount;
        this.hiddenCount = hiddenCount;
        this.anomalyRate = totalRecords > 0
                ? Math.round(((double)(faultCount + hiddenCount) / totalRecords) * 10000.0) / 100.0
                : 0.0;
    }

    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }

    public long getNormalCount() { return normalCount; }
    public void setNormalCount(long normalCount) { this.normalCount = normalCount; }

    public long getFaultCount() { return faultCount; }
    public void setFaultCount(long faultCount) { this.faultCount = faultCount; }

    public long getHiddenCount() { return hiddenCount; }
    public void setHiddenCount(long hiddenCount) { this.hiddenCount = hiddenCount; }

    public double getAnomalyRate() { return anomalyRate; }
    public void setAnomalyRate(double anomalyRate) { this.anomalyRate = anomalyRate; }
}