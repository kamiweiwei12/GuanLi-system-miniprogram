package com.inspection.service.impl;

import com.inspection.common.UserContext;
import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.entity.Department;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.DepartmentMapper;
import com.inspection.mapper.InspectionRecordMapper;
import com.inspection.service.StatisticsService;
import com.inspection.vo.StatisticsVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计服务实现
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final InspectionRecordMapper recordMapper;
    private final DepartmentMapper departmentMapper;

    public StatisticsServiceImpl(InspectionRecordMapper recordMapper,
                                 DepartmentMapper departmentMapper) {
        this.recordMapper = recordMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public StatisticsVO getStatistics(StatisticsQueryDTO dto) {
        String roleCode = UserContext.getRoleCode();

        // 权限校验
        if ("ADMIN".equals(roleCode)) {
            // ADMIN：不过滤
        } else if ("DEPT_ADMIN".equals(roleCode)) {
            Long deptId = UserContext.getDepartmentId();
            if (deptId != null) {
                Department dept = departmentMapper.selectById(deptId);
                if (dept != null) {
                    dto.setDepartment(dept.getDeptName());
                }
            }
        } else {
            throw new BusinessException(403, "无权限查看统计数据");
        }

        // 查询统计
        long totalRecords = recordMapper.countForStats(dto);
        List<Map<String, Object>> resultRows = recordMapper.countByResult(dto);
        List<Map<String, Object>> deptRows = recordMapper.countByDepartment(dto);
        List<Map<String, Object>> dateRows = recordMapper.countByDate(dto);

        // 组装 VO
        StatisticsVO vo = new StatisticsVO();
        vo.setTotalRecords(totalRecords);
        vo.setByResult(buildResultMap(resultRows));
        vo.setByDepartment(buildDepartmentList(deptRows));
        vo.setByDate(buildDateList(dateRows));

        return vo;
    }

    private Map<String, Long> buildResultMap(List<Map<String, Object>> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("normal", 0L);
        map.put("fault", 0L);
        map.put("hidden", 0L);

        for (Map<String, Object> row : rows) {
            Object r = row.get("result");
            Object c = row.get("cnt");
            if (r == null || c == null) continue;

            int result = ((Number) r).intValue();
            long cnt = ((Number) c).longValue();

            if (result == 0) map.put("normal", cnt);
            else if (result == 1) map.put("fault", cnt);
            else if (result == 2) map.put("hidden", cnt);
        }
        return map;
    }

    private List<StatisticsVO.DepartmentStat> buildDepartmentList(List<Map<String, Object>> rows) {
        List<StatisticsVO.DepartmentStat> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String dept = (String) row.get("department");
            long cnt = ((Number) row.get("cnt")).longValue();
            list.add(new StatisticsVO.DepartmentStat(dept, cnt));
        }
        return list;
    }

    private List<StatisticsVO.DateStat> buildDateList(List<Map<String, Object>> rows) {
        List<StatisticsVO.DateStat> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String date = row.get("date").toString();
            long cnt = ((Number) row.get("cnt")).longValue();
            list.add(new StatisticsVO.DateStat(date, cnt));
        }
        return list;
    }
}