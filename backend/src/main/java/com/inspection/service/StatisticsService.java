package com.inspection.service;

import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.vo.*;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /** 获取巡检统计数据（Phase 10.3 已有，保持不变） */
    StatisticsVO getStatistics(StatisticsQueryDTO dto);

    /** 获取统计总览（总数+正常/故障/隐患+异常率） */
    OverviewVO getOverview(StatisticsQueryDTO dto);

    /** 获取区域统计排行（按异常率） */
    List<AreaStatVO> getAreaStats(StatisticsQueryDTO dto);

    /** 获取设备统计（高频/故障/风险排行） */
    DeviceStatVO getDeviceStats(StatisticsQueryDTO dto);

    /** 获取趋势统计（按日分组，含各结果类型） */
    List<TrendStatVO> getTrendStats(StatisticsQueryDTO dto);
}
