package com.inspection.service;

import com.inspection.vo.DashboardVO;

/**
 * Dashboard 首页服务接口
 * Phase 14: 管理员登录首页实时状态
 */
public interface DashboardService {

    /** 获取 Dashboard 首页数据（含今日统计、最近异常、高风险区域） */
    DashboardVO getDashboard();
}