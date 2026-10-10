package com.inspection.controller;

import com.inspection.common.Result;
import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.service.StatisticsService;
import com.inspection.vo.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计控制器
 * 支持 ADMIN / DEPT_ADMIN / EMPLOYEE 三种角色
 * 权限过滤在 Service 层完成
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /** 已有 — 统计仪表盘（Phase 10.3，保持不变） */
    @GetMapping
    public Result<StatisticsVO> getStatistics(StatisticsQueryDTO dto) {
        StatisticsVO vo = statisticsService.getStatistics(dto);
        return Result.success(vo);
    }

    /** 统计总览（总数 + 正常/故障/隐患 + 异常率） */
    @GetMapping("/overview")
    public Result<OverviewVO> getOverview(StatisticsQueryDTO dto) {
        OverviewVO vo = statisticsService.getOverview(dto);
        return Result.success(vo);
    }

    /** 区域统计排行（按异常率降序） */
    @GetMapping("/area")
    public Result<List<AreaStatVO>> getAreaStats(StatisticsQueryDTO dto) {
        List<AreaStatVO> list = statisticsService.getAreaStats(dto);
        return Result.success(list);
    }

    /** 设备统计（高频/故障/风险 TOP10） */
    @GetMapping("/device")
    public Result<DeviceStatVO> getDeviceStats(StatisticsQueryDTO dto) {
        DeviceStatVO vo = statisticsService.getDeviceStats(dto);
        return Result.success(vo);
    }

    /** 趋势统计（按日分组，含各结果类型数量） */
    @GetMapping("/trend")
    public Result<List<TrendStatVO>> getTrendStats(StatisticsQueryDTO dto) {
        List<TrendStatVO> list = statisticsService.getTrendStats(dto);
        return Result.success(list);
    }
}
