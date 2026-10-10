package com.inspection.mapper;

import com.inspection.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper — sys_operation_log
 */
@Mapper
public interface OperationLogMapper {

    int insert(OperationLog log);
}