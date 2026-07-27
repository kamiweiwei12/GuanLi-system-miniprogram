package com.inspection.service;

import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.vo.StatisticsVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /** 获取巡检统计数据 */
    StatisticsVO getStatistics(StatisticsQueryDTO dto);
}