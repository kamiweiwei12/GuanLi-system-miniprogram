package com.inspection.service;

import com.inspection.common.PageResult;
import com.inspection.dto.InspectionQueryDTO;
import com.inspection.dto.InspectionReportDTO;
import com.inspection.entity.InspectionRecord;
import com.inspection.vo.InspectionRecordVO;

/**
 * 巡检记录服务接口
 */
public interface InspectionRecordService {

    /**
     * 提交巡检记录
     */
    Long submitReport(InspectionReportDTO dto);

    /**
     * 按 ID 查询巡检记录
     */
    InspectionRecord getById(Long id);

    /**
     * 分页条件查询巡检记录
     */
    PageResult<InspectionRecordVO> queryList(InspectionQueryDTO dto);
}
