package com.inspection.controller;

import com.inspection.common.Result;
import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.service.StatisticsService;
import com.inspection.vo.StatisticsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制器（ADMIN / DEPT_ADMIN 专用）
 */
@RestController
@RequestMapping("/api/v1")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics(StatisticsQueryDTO dto) {
        StatisticsVO vo = statisticsService.getStatistics(dto);
        return Result.success(vo);
    }
}