package com.inspection.service;

import com.inspection.dto.InspectionQueryDTO;

import java.io.OutputStream;

/**
 * 报表导出服务接口
 */
public interface ExportService {

    /**
     * 导出巡检记录为 Excel
     *
     * @param dto 查询条件（与列表查询复用相同参数，不分页）
     * @param outputStream 输出流（由 Controller 管理关闭）
     */
    void exportToExcel(InspectionQueryDTO dto, OutputStream outputStream);
}