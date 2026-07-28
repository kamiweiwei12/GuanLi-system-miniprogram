package com.inspection.service.impl;

import com.inspection.common.UserContext;
import com.inspection.dto.InspectionQueryDTO;
import com.inspection.entity.Department;
import com.inspection.entity.InspectionImage;
import com.inspection.entity.InspectionRecord;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.DepartmentMapper;
import com.inspection.mapper.InspectionImageMapper;
import com.inspection.mapper.InspectionRecordMapper;
import com.inspection.service.ExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报表导出服务实现
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final InspectionRecordMapper recordMapper;
    private final InspectionImageMapper imageMapper;
    private final DepartmentMapper departmentMapper;

    @Value("${file.base-url}")
    private String fileBaseUrl;

    public ExportServiceImpl(InspectionRecordMapper recordMapper,
                              InspectionImageMapper imageMapper,
                              DepartmentMapper departmentMapper) {
        this.recordMapper = recordMapper;
        this.imageMapper = imageMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public void exportToExcel(InspectionQueryDTO dto, OutputStream outputStream) {
        // TODO Phase 11: 抽取公共 PermissionService，避免与 InspectionRecordServiceImpl.queryList() 的权限逻辑重复
        Long userId = UserContext.getUserId();
        String roleCode = UserContext.getRoleCode();

        // 创建副本，不修改原始 DTO
        InspectionQueryDTO filteredDto = new InspectionQueryDTO();
        filteredDto.setArea(dto.getArea());
        filteredDto.setResult(dto.getResult());
        filteredDto.setDeviceName(dto.getDeviceName());
        filteredDto.setStartTime(dto.getStartTime());
        filteredDto.setEndTime(dto.getEndTime());

        // 权限过滤：ADMIN > DEPT_ADMIN > EMPLOYEE
        if ("ADMIN".equals(roleCode)) {
            // ADMIN：不过滤，导出全部
        } else if ("DEPT_ADMIN".equals(roleCode)) {
            // DEPT_ADMIN：只导出本部门
            Long deptId = UserContext.getDepartmentId();
            if (deptId != null) {
                Department dept = departmentMapper.selectById(deptId);
                if (dept != null) {
                    filteredDto.setDepartment(dept.getDeptName());
                }
            }
        } else {
            // EMPLOYEE：只导出自己的
            if (userId == null) {
                throw new BusinessException(401, "未认证");
            }
            filteredDto.setUserId(userId);
        }

        // 查询全量数据（不分页）
        List<InspectionRecord> records = recordMapper.selectForExport(filteredDto);

        // 生成 Excel
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("巡检记录");

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 数据样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setWrapText(true);
            dataStyle.setVerticalAlignment(VerticalAlignment.TOP);

            // 表头
            String[] headers = {"序号", "部门", "姓名", "手机号", "区域", "设备名称", "巡检日期", "巡检结果", "详细描述", "图片"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 数据行
            int rowIndex = 1;
            for (int seq = 0; seq < records.size(); seq++) {
                InspectionRecord record = records.get(seq);
                Row row = sheet.createRow(rowIndex++);

                // 序号
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(seq + 1);
                cell0.setCellStyle(dataStyle);

                // 部门
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(nullToEmpty(record.getDepartment()));
                cell1.setCellStyle(dataStyle);

                // 姓名
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(nullToEmpty(record.getEmployeeName()));
                cell2.setCellStyle(dataStyle);

                // 手机号
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(nullToEmpty(record.getPhoneNumber()));
                cell3.setCellStyle(dataStyle);

                // 区域
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(nullToEmpty(record.getArea()));
                cell4.setCellStyle(dataStyle);

                // 设备名称
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(nullToEmpty(record.getDeviceName()));
                cell5.setCellStyle(dataStyle);

                // 巡检日期
                Cell cell6 = row.createCell(6);
                if (record.getInspectionTime() != null) {
                    cell6.setCellValue(record.getInspectionTime().format(DATE_FORMATTER));
                }
                cell6.setCellStyle(dataStyle);

                // 巡检结果
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(resultToText(record.getResult()));
                cell7.setCellStyle(dataStyle);

                // 详细描述
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(nullToEmpty(record.getDescription()));
                cell8.setCellStyle(dataStyle);

                // 图片 URL（拼接完整地址）
                List<InspectionImage> images = imageMapper.selectByRecordId(record.getId());
                String imageUrls = "";
                if (images != null && !images.isEmpty()) {
                    imageUrls = images.stream()
                            .map(img -> fileBaseUrl + img.getImageUrl())
                            .collect(Collectors.joining("; "));
                }
                Cell cell9 = row.createCell(9);
                cell9.setCellValue(imageUrls);
                cell9.setCellStyle(dataStyle);
            }

            // 自动调整列宽（根据内容）
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 设置最小列宽和最大列宽
                int width = Math.max(sheet.getColumnWidth(i), 8 * 256);
                width = Math.min(width, 50 * 256);
                sheet.setColumnWidth(i, width);
            }

            // 冻结第一行
            sheet.createFreezePane(0, 1);

            // 写入输出流
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException("导出 Excel 失败: " + e.getMessage(), e);
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String resultToText(Integer result) {
        if (result == null) return "未知";
        return switch (result) {
            case 0 -> "正常";
            case 1 -> "故障";
            case 2 -> "隐患";
            default -> "未知";
        };
    }
}