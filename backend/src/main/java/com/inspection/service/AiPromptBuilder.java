package com.inspection.service;

import com.inspection.vo.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI Prompt 构建器
 * Phase 14: 新增周报/月报模板
 */
@Component
public class AiPromptBuilder {

    /**
     * 构建 Prompt（根据报告类型分发）
     */
    public String build(String reportType, OverviewVO summary, List<AreaStatVO> topAreas,
                        List<DeviceStatVO.DeviceItem> topDevices) {
        if ("WEEKLY".equalsIgnoreCase(reportType)) return buildWeekly(summary, topAreas, topDevices);
        if ("MONTHLY".equalsIgnoreCase(reportType)) return buildMonthly(summary, topAreas, topDevices);
        return buildDefault(summary, topAreas, topDevices);
    }

    /** 日报模板 */
    private String buildDefault(OverviewVO summary, List<AreaStatVO> topAreas,
                                List<DeviceStatVO.DeviceItem> topDevices) {
        return buildBase(summary, topAreas, topDevices, "日");
    }

    /** 周报模板 */
    private String buildWeekly(OverviewVO summary, List<AreaStatVO> topAreas,
                               List<DeviceStatVO.DeviceItem> topDevices) {
        StringBuilder sb = new StringBuilder(buildBase(summary, topAreas, topDevices, "周"));
        sb.append("\n请根据以上 7 天数据生成周报，重点分析：\n");
        sb.append("1) 本周异常趋势（环比上周）\n");
        sb.append("2) 需下周重点关注的区域和设备\n");
        sb.append("3) 本周最严重的 3 个风险点\n");
        return sb.toString();
    }

    /** 月报模板 */
    private String buildMonthly(OverviewVO summary, List<AreaStatVO> topAreas,
                                List<DeviceStatVO.DeviceItem> topDevices) {
        StringBuilder sb = new StringBuilder(buildBase(summary, topAreas, topDevices, "月"));
        sb.append("\n请根据以上月度数据生成月度巡检分析报告，重点分析：\n");
        sb.append("1) 本月异常趋势（环比上月）\n");
        sb.append("2) 风险设备排名及故障原因分析\n");
        sb.append("3) 高风险区域改进建议\n");
        sb.append("4) 下月巡检重点建议\n");
        return sb.toString();
    }

    private String buildBase(OverviewVO summary, List<AreaStatVO> topAreas,
                             List<DeviceStatVO.DeviceItem> topDevices, String period) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("请根据以下设备巡检数据生成一份专业的巡检%s报，包含风险评估和改进建议。\n\n", period));

        sb.append("## 整体数据\n");
        sb.append(String.format("- 总巡检次数：%d\n", summary.getTotalRecords()));
        sb.append(String.format("- 正常数量：%d\n", summary.getNormalCount()));
        sb.append(String.format("- 故障数量：%d\n", summary.getFaultCount()));
        sb.append(String.format("- 隐患数量：%d\n", summary.getHiddenCount()));
        sb.append(String.format("- 异常率：%.2f%%\n\n", summary.getAnomalyRate()));

        if (topAreas != null && !topAreas.isEmpty()) {
            sb.append("## 区域异常率排行\n");
            for (int i = 0; i < Math.min(topAreas.size(), 5); i++) {
                AreaStatVO a = topAreas.get(i);
                sb.append(String.format("%d. %s — 异常率 %.2f%% (巡检%d次, 故障%d, 隐患%d)\n",
                        i + 1, a.getArea(), a.getAnomalyRate(),
                        a.getTotalCount(), a.getFaultCount(), a.getHiddenCount()));
            }
            sb.append("\n");
        }

        if (topDevices != null && !topDevices.isEmpty()) {
            sb.append("## 高频巡检设备 TOP5\n");
            for (int i = 0; i < Math.min(topDevices.size(), 5); i++) {
                DeviceStatVO.DeviceItem d = topDevices.get(i);
                sb.append(String.format("%d. %s — 巡检%d次\n",
                        i + 1, d.getDeviceName(), d.getCount()));
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}