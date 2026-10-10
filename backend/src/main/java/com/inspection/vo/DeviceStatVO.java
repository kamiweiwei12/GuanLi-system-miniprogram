package com.inspection.vo;

import java.util.List;

/**
 * 设备统计 VO — GET /api/v1/statistics/device
 * 包含高频巡检设备、故障设备、风险设备三类排行
 */
public class DeviceStatVO {

    /** 高频巡检设备 TOP10 */
    private List<DeviceItem> highFrequencyDevices;

    /** 故障率最高设备 TOP10 */
    private List<FaultDeviceItem> faultDevices;

    /** 隐患率最高设备 TOP10 */
    private List<HiddenDeviceItem> riskDevices;

    public List<DeviceItem> getHighFrequencyDevices() { return highFrequencyDevices; }
    public void setHighFrequencyDevices(List<DeviceItem> highFrequencyDevices) { this.highFrequencyDevices = highFrequencyDevices; }

    public List<FaultDeviceItem> getFaultDevices() { return faultDevices; }
    public void setFaultDevices(List<FaultDeviceItem> faultDevices) { this.faultDevices = faultDevices; }

    public List<HiddenDeviceItem> getRiskDevices() { return riskDevices; }
    public void setRiskDevices(List<HiddenDeviceItem> riskDevices) { this.riskDevices = riskDevices; }

    /**
     * 高频设备项
     */
    public static class DeviceItem {
        private String deviceName;
        private long count;

        public DeviceItem() {}
        public DeviceItem(String deviceName, long count) {
            this.deviceName = deviceName;
            this.count = count;
        }

        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }

    /**
     * 故障设备项
     */
    public static class FaultDeviceItem {
        private String deviceName;
        private long faultCount;
        private long totalCount;
        private double faultRate; // %

        public FaultDeviceItem() {}
        public FaultDeviceItem(String deviceName, long faultCount, long totalCount) {
            this.deviceName = deviceName;
            this.faultCount = faultCount;
            this.totalCount = totalCount;
            this.faultRate = totalCount > 0
                    ? Math.round(((double) faultCount / totalCount) * 10000.0) / 100.0
                    : 0.0;
        }

        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public long getFaultCount() { return faultCount; }
        public void setFaultCount(long faultCount) { this.faultCount = faultCount; }
        public long getTotalCount() { return totalCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
        public double getFaultRate() { return faultRate; }
        public void setFaultRate(double faultRate) { this.faultRate = faultRate; }
    }

    /**
     * 隐患设备项
     */
    public static class HiddenDeviceItem {
        private String deviceName;
        private long hiddenCount;
        private long totalCount;
        private double hiddenRate; // %

        public HiddenDeviceItem() {}
        public HiddenDeviceItem(String deviceName, long hiddenCount, long totalCount) {
            this.deviceName = deviceName;
            this.hiddenCount = hiddenCount;
            this.totalCount = totalCount;
            this.hiddenRate = totalCount > 0
                    ? Math.round(((double) hiddenCount / totalCount) * 10000.0) / 100.0
                    : 0.0;
        }

        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public long getHiddenCount() { return hiddenCount; }
        public void setHiddenCount(long hiddenCount) { this.hiddenCount = hiddenCount; }
        public long getTotalCount() { return totalCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
        public double getHiddenRate() { return hiddenRate; }
        public void setHiddenRate(double hiddenRate) { this.hiddenRate = hiddenRate; }
    }
}