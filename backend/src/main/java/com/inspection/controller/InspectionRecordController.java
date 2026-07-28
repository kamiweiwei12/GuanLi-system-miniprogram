package com.inspection.controller;

import com.inspection.common.PageResult;
import com.inspection.common.Result;
import com.inspection.dto.InspectionQueryDTO;
import com.inspection.dto.InspectionReportDTO;
import com.inspection.service.ExportService;
import com.inspection.service.InspectionRecordService;
import com.inspection.vo.InspectionRecordVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 巡检记录控制器
 */
@RestController
@RequestMapping("/api/v1")
public class InspectionRecordController {

    private final InspectionRecordService service;
    private final ExportService exportService;

    public InspectionRecordController(InspectionRecordService service,
                                       ExportService exportService) {
        this.service = service;
        this.exportService = exportService;
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
        InspectionRecordVO vo = service.getByIdVO(id);
        return Result.success(vo);
    }

    @GetMapping("/report/export")
    public void exportReport(InspectionQueryDTO dto, HttpServletResponse response) throws IOException {
        // 设置响应头
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = "inspection_report_" + timestamp + ".xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

        // 生成 Excel 并写入响应流
        exportService.exportToExcel(dto, response.getOutputStream());
    }
}