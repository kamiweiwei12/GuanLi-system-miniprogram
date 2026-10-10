package com.inspection.controller;

import com.inspection.common.Result;
import com.inspection.dto.AiReportRequestDTO;
import com.inspection.service.AiReportService;
import com.inspection.vo.AiReportVO;
import org.springframework.web.bind.annotation.*;

/**
 * AI 报告控制器
 * Phase 13: 生成统计摘要 + AI Prompt，不调用真实 AI
 */
@RestController
@RequestMapping("/api/v1")
public class AiReportController {

    private final AiReportService aiReportService;

    public AiReportController(AiReportService aiReportService) {
        this.aiReportService = aiReportService;
    }

    @PostMapping("/ai/report")
    public Result<AiReportVO> generateReport(@RequestBody AiReportRequestDTO dto) {
        AiReportVO vo = aiReportService.generateReport(dto);
        return Result.success(vo);
    }
}