package com.inspection.service;

import com.inspection.dto.AiReportRequestDTO;
import com.inspection.vo.AiReportVO;

/**
 * AI 报告服务接口
 */
public interface AiReportService {

    /**
     * 生成巡检分析报告
     * Phase 13: 返回统计摘要 + AI Prompt，不调用真实 AI
     * 后续 Phase: 接入 DeepSeek/OpenAI 等 AI 服务
     */
    AiReportVO generateReport(AiReportRequestDTO dto);
}