package com.inspection.mapper;

import com.inspection.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统角色 Mapper
 */
@Mapper
public interface RoleMapper {

    Role selectById(Long id);

    List<Role> selectAll();
}