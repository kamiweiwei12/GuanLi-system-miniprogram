package com.inspection.vo;

import java.util.List;
import java.util.Map;

/**
 * 巡检统计结果 VO
 */
public class StatisticsVO {

    /** 记录总数 */
    private long totalRecords;

    /** 按巡检结果统计：{ "normal": 100, "fault": 30, "hidden": 20 } */
    private Map<String, Long> byResult;

    /** 按部门统计 */
    private List<DepartmentStat> byDepartment;

    /** 按日期统计（最近 30 天） */
    private List<DateStat> byDate;

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Map<String, Long> getByResult() {
        return byResult;
    }

    public void setByResult(Map<String, Long> byResult) {
        this.byResult = byResult;
    }

    public List<DepartmentStat> getByDepartment() {
        return byDepartment;
    }

    public void setByDepartment(List<DepartmentStat> byDepartment) {
        this.byDepartment = byDepartment;
    }

    public List<DateStat> getByDate() {
        return byDate;
    }

    public void setByDate(List<DateStat> byDate) {
        this.byDate = byDate;
    }

    public static class DepartmentStat {
        private String department;
        private long count;

        public DepartmentStat() {}

        public DepartmentStat(String department, long count) {
            this.department = department;
            this.count = count;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }

    public static class DateStat {
        private String date;
        private long count;

        public DateStat() {}

        public DateStat(String date, long count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}