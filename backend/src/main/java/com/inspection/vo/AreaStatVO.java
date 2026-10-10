package com.inspection.vo;

/**
 * 区域统计 VO — GET /api/v1/statistics/area
 */
public class AreaStatVO {

    /** 区域名称 */
    private String area;

    /** 总巡检数量 */
    private long totalCount;

    /** 正常数量 */
    private long normalCount;

    /** 故障数量 */
    private long faultCount;

    /** 隐患数量 */
    private long hiddenCount;

    /** 异常率 %，保留两位小数 */
    private double anomalyRate;

    public AreaStatVO() {}

    public AreaStatVO(String area, long totalCount, long normalCount, long faultCount, long hiddenCount) {
        this.area = area;
        this.totalCount = totalCount;
        this.normalCount = normalCount;
        this.faultCount = faultCount;
        this.hiddenCount = hiddenCount;
        this.anomalyRate = totalCount > 0
                ? Math.round(((double)(faultCount + hiddenCount) / totalCount) * 10000.0) / 100.0
                : 0.0;
    }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

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