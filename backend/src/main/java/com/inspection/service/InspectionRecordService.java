package com.inspection.service;

import com.inspection.dto.InspectionReportDTO;
import com.inspection.entity.InspectionRecord;

/**
 * 巡检记录服务接口
 */
public interface InspectionRecordService {

    /**
     * 提交巡检记录
     *
     * @param dto 巡检报告 DTO
     * @return 新创建的记录 ID
     */
    Long submitReport(InspectionReportDTO dto);

    /**
     * 按 ID 查询巡检记录
     *
     * @param id 记录 ID
     * @return 巡检记录实体
     */
    InspectionRecord getById(Long id);
}