package com.inspection.mapper;

import com.inspection.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门 Mapper
 */
@Mapper
public interface DepartmentMapper {

    /** 根据 ID 查询部门 */
    Department selectById(Long id);
}