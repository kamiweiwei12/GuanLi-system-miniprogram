package com.inspection.controller;

import com.inspection.common.Result;
import com.inspection.dto.InspectionReportDTO;
import com.inspection.service.InspectionRecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 巡检记录控制器
 */
@RestController
@RequestMapping("/api/v1")
public class InspectionRecordController {

    private final InspectionRecordService service;

    public InspectionRecordController(InspectionRecordService service) {
        this.service = service;
    }

    @PostMapping("/report")
    public Result<Long> submitReport(@RequestBody InspectionReportDTO dto) {
        Long id = service.submitReport(dto);
        return Result.success(id);
    }
}