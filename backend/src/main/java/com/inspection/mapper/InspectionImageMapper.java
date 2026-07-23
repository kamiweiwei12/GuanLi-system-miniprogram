package com.inspection.mapper;

import com.inspection.entity.InspectionImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 巡检图片 Mapper
 */
@Mapper
public interface InspectionImageMapper {

    int insert(InspectionImage image);

    List<InspectionImage> selectByRecordId(Long recordId);
}