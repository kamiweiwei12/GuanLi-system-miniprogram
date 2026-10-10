package com.inspection.mapper;

import com.inspection.dto.InspectionQueryDTO;
import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.entity.InspectionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

    /** 按区域+结果分组统计（用于区域异常率计算） */
    List<Map<String, Object>> countByArea(StatisticsQueryDTO dto);

    /** 按设备+结果分组统计（用于设备故障/隐患率计算） */
    List<Map<String, Object>> countByDevice(StatisticsQueryDTO dto);

    /** 按日期+结果分组统计（用于趋势图多线展示） */
    List<Map<String, Object>> countByDateWithResult(StatisticsQueryDTO dto);

    // ===== 导出方法 =====

    /**
     * 条件查询（不分页，用于导出）
     * 复用 selectByCondition 的 WHERE 逻辑，去掉 LIMIT
     */
    List<InspectionRecord> selectForExport(InspectionQueryDTO dto);

    // ===== Dashboard 方法（Phase 14） =====

    /** 今日巡检总数（使用范围查询，保证索引有效） */
    long countToday(Long userId, String department, LocalDateTime todayStart, LocalDateTime todayEnd);

    /** 今日异常数（故障+隐患） */
    long countTodayAnomalies(Long userId, String department, LocalDateTime todayStart, LocalDateTime todayEnd);

    /** 最近故障记录 */
    List<Map<String, Object>> selectRecentFaults(Long userId, String department, int limit);

    /** 最近隐患记录 */
    List<Map<String, Object>> selectRecentHiddens(Long userId, String department, int limit);

    // ===== 筛选选项方法 =====

    /**
     * 查询所有不重复的区域（支持权限过滤）
     */
    List<String> selectDistinctAreas(InspectionQueryDTO dto);

    /**
     * 查询所有不重复的设备名称（支持权限过滤）
     */
    List<String> selectDistinctDevices(InspectionQueryDTO dto);
}