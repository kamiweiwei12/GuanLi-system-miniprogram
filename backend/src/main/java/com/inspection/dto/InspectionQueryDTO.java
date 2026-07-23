package com.inspection.dto;

import java.time.LocalDateTime;

/**
 * 巡检记录查询条件 DTO
 */
public class InspectionQueryDTO {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String area;
    private Integer result;
    private String deviceName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * 计算分页偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}