package com.inspection.controller;

import com.inspection.common.PageResult;
import com.inspection.common.Result;
import com.inspection.dto.InspectionQueryDTO;
import com.inspection.dto.InspectionReportDTO;
import com.inspection.service.InspectionRecordService;
import com.inspection.vo.InspectionRecordVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/report")
    public Result<PageResult<InspectionRecordVO>> queryList(InspectionQueryDTO dto) {
        PageResult<InspectionRecordVO> page = service.queryList(dto);
        return Result.success(page);
    }

    @GetMapping("/report/{id}")
    public Result<InspectionRecordVO> getById(@PathVariable Long id) {
        var record = service.getById(id);
        InspectionRecordVO vo = new InspectionRecordVO();
        vo.setId(record.getId());
        vo.setDepartment(record.getDepartment());
        vo.setEmployeeName(record.getEmployeeName());
        vo.setPhoneNumber(record.getPhoneNumber());
        vo.setArea(record.getArea());
        vo.setInspectionTime(record.getInspectionTime());
        vo.setDeviceName(record.getDeviceName());
        vo.setResult(record.getResult());
        vo.setDescription(record.getDescription());
        vo.setCreatedAt(record.getCreatedAt());
        return Result.success(vo);
    }
}
