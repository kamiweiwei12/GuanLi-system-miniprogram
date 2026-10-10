package com.inspection.service.impl;

import com.inspection.dto.AiReportRequestDTO;
import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.service.AiPromptBuilder;
import com.inspection.service.AiReportService;
import com.inspection.service.StatisticsService;
import com.inspection.vo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI 报告服务实现
 * Phase 13: 生成统计摘要 + AI Prompt，不调用真实 AI
 */
@Service
public class AiReportServiceImpl implements AiReportService {

    private final StatisticsService statisticsService;
    private final AiPromptBuilder promptBuilder;

    public AiReportServiceImpl(StatisticsService statisticsService,
                                AiPromptBuilder promptBuilder) {
        this.statisticsService = statisticsService;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public AiReportVO generateReport(AiReportRequestDTO dto) {
        // 构造统计查询参数
        StatisticsQueryDTO query = new StatisticsQueryDTO();
        query.setStartDate(dto.getStartDate());
        query.setEndDate(dto.getEndDate());

        // 聚合统计数据
        OverviewVO overview = statisticsService.getOverview(query);
        List<AreaStatVO> areaStats = statisticsService.getAreaStats(query);
        DeviceStatVO deviceStats = statisticsService.getDeviceStats(query);
        List<TrendStatVO> trendStats = statisticsService.getTrendStats(query);

        // 构建 AI Prompt（Phase 14: 支持周报/月报模板）
        String reportType = dto.getReportType() != null ? dto.getReportType() : "DAILY";
        String prompt = promptBuilder.build(reportType, overview, areaStats, deviceStats.getHighFrequencyDevices());

        // 计算趋势（简易环比）
        String trend = "持平";
        if (trendStats.size() >= 2) {
            double recent = trendStats.get(0).getAnomalyRate();
            double previous = trendStats.get(1).getAnomalyRate();
            if (recent > previous + 1) trend = "上升";
            else if (recent < previous - 1) trend = "下降";
        }

        // 组装响应
        AiReportVO vo = new AiReportVO();
        vo.setReportId(UUID.randomUUID().toString());
        vo.setGeneratedAt(LocalDateTime.now());
        vo.setPeriod(new AiReportVO.ReportPeriod(
                dto.getStartDate(), dto.getEndDate(),
                dto.getReportType() != null ? dto.getReportType() : "DAILY"
        ));
        vo.setSummary(new AiReportVO.ReportSummary(
                overview.getTotalRecords(), overview.getAnomalyRate(), trend
        ));
        vo.setTopAreas(areaStats.size() > 5 ? areaStats.subList(0, 5) : areaStats);
        vo.setTopDevices(deviceStats.getHighFrequencyDevices().size() > 10
                ? deviceStats.getHighFrequencyDevices().subList(0, 10)
                : deviceStats.getHighFrequencyDevices());
        vo.setTrendData(trendStats);
        vo.setAiAnalysis(null); // Phase 13 不接入 AI
        vo.setAiPrompt(prompt);

        return vo;
    }
}