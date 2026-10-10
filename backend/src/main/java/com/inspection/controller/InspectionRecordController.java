package com.inspection.controller;

import com.inspection.common.PageResult;
import com.inspection.common.Result;
import com.inspection.common.UserContext;
import com.inspection.dto.InspectionQueryDTO;
import com.inspection.dto.InspectionReportDTO;
import com.inspection.entity.Department;
import com.inspection.mapper.DepartmentMapper;
import com.inspection.mapper.InspectionRecordMapper;
import com.inspection.service.ExportService;
import com.inspection.service.InspectionRecordService;
import com.inspection.vo.FilterOptionsVO;
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
import java.util.List;

// TODO Phase 14: 巡检记录删除为 Mapper 软删除，暂无 Controller DELETE 端点
// 后续如增加 DELETE /api/v1/report/{id}，需在此注入 OperationLogService 记录日志

/**
 * 巡检记录控制器
 */
@RestController
@RequestMapping("/api/v1")
public class InspectionRecordController {

    private final InspectionRecordService service;
    private final ExportService exportService;
    private final InspectionRecordMapper recordMapper;
    private final DepartmentMapper departmentMapper;

    public InspectionRecordController(InspectionRecordService service,
                                       ExportService exportService,
                                       InspectionRecordMapper recordMapper,
                                       DepartmentMapper departmentMapper) {
        this.service = service;
        this.exportService = exportService;
        this.recordMapper = recordMapper;
        this.departmentMapper = departmentMapper;
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
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = "inspection_report_" + timestamp + ".xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

        exportService.exportToExcel(dto, response.getOutputStream());
    }

    @GetMapping("/report/options")
    public Result<FilterOptionsVO> getFilterOptions() {
        InspectionQueryDTO dto = new InspectionQueryDTO();
        long userId = UserContext.getUserId();
        String roleCode = UserContext.getRoleCode();

        // 权限过滤
        if ("ADMIN".equals(roleCode)) {
            // 不过滤
        } else if ("DEPT_ADMIN".equals(roleCode)) {
            Long deptId = UserContext.getDepartmentId();
            if (deptId != null) {
                Department dept = departmentMapper.selectById(deptId);
                if (dept != null) {
                    dto.setDepartment(dept.getDeptName());
                }
            }
        } else {
            dto.setUserId(userId);
        }

        List<String> areas = recordMapper.selectDistinctAreas(dto);
        List<String> devices = recordMapper.selectDistinctDevices(dto);
        return Result.success(new FilterOptionsVO(areas, devices));
    }
}