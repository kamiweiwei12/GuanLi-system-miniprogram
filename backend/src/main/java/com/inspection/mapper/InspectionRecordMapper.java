package com.inspection.mapper;

import com.inspection.entity.InspectionRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 巡检记录 Mapper
 */
@Mapper
public interface InspectionRecordMapper {

    int insert(InspectionRecord record);

    InspectionRecord selectById(Long id);

    List<InspectionRecord> selectList();

    int update(InspectionRecord record);

    int deleteById(Long id);
}