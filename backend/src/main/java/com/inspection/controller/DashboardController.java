package com.inspection.controller;

import com.inspection.common.Result;
import com.inspection.service.DashboardService;
import com.inspection.vo.DashboardVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard 首页控制器
 * Phase 14: 管理员/DEPT_ADMIN 可见，EMPLOYEE 禁止
 */
@RestController
@RequestMapping("/api/v1")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        DashboardVO vo = dashboardService.getDashboard();
        return Result.success(vo);
    }
}