package com.inspection.vo;

import java.util.List;

/**
 * 筛选选项视图对象
 */
public class FilterOptionsVO {

    private List<String> areas;
    private List<String> devices;

    public FilterOptionsVO(List<String> areas, List<String> devices) {
        this.areas = areas;
        this.devices = devices;
    }

    public List<String> getAreas() {
        return areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }
}