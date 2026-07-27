package com.inspection.mapper;

import com.inspection.dto.InspectionQueryDTO;
import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.entity.InspectionRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 巡检记录 Mapper
 */
@Mapper
public interface InspectionRecordMapper {

    int insert(InspectionRecord record);

    InspectionRecord selectById(Long id);

    List<InspectionRecord> selectList();

    /**
     * 条件查询
     */
    List<InspectionRecord> selectByCondition(InspectionQueryDTO dto);

    /**
     * 条件计数
     */
    long count(InspectionQueryDTO dto);

    int update(InspectionRecord record);

    int deleteById(Long id);

    // ===== 统计方法 =====

    /** 统计总数 */
    long countForStats(StatisticsQueryDTO dto);

    /** 按巡检结果分组统计 */
    List<Map<String, Object>> countByResult(StatisticsQueryDTO dto);

    /** 按部门分组统计 */
    List<Map<String, Object>> countByDepartment(StatisticsQueryDTO dto);

    /** 按日期分组统计 */
    List<Map<String, Object>> countByDate(StatisticsQueryDTO dto);
}