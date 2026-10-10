package com.inspection.service.impl;

import com.inspection.common.UserContext;
import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.entity.Department;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.DepartmentMapper;
import com.inspection.mapper.InspectionRecordMapper;
import com.inspection.service.DashboardService;
import com.inspection.service.StatisticsService;
import com.inspection.vo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dashboard 首页服务实现
 * Phase 14: 管理员/DEPT_ADMIN 可见，EMPLOYEE 禁止访问
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final InspectionRecordMapper recordMapper;
    private final DepartmentMapper departmentMapper;
    private final StatisticsService statisticsService;

    public DashboardServiceImpl(InspectionRecordMapper recordMapper,
                                 DepartmentMapper departmentMapper,
                                 StatisticsService statisticsService) {
        this.recordMapper = recordMapper;
        this.departmentMapper = departmentMapper;
        this.statisticsService = statisticsService;
    }

    @Override
    public DashboardVO getDashboard() {
        String roleCode = UserContext.getRoleCode();
        Long userId = null;
        String deptName = null;

        if ("ADMIN".equals(roleCode)) {
            // 无过滤
        } else if ("DEPT_ADMIN".equals(roleCode)) {
            Long deptId = UserContext.getDepartmentId();
            if (deptId != null) {
                Department dept = departmentMapper.selectById(deptId);
                if (dept != null) {
                    deptName = dept.getDeptName();
                }
            }
        } else {
            throw new BusinessException(403, "EMPLOYEE 角色无权访问管理后台 Dashboard");
        }

        DashboardVO vo = new DashboardVO();

        // 今日统计（使用范围查询，避免 DATE() 函数）
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        long todayTotal = recordMapper.countToday(userId, deptName, todayStart, todayEnd);
        long todayAnomalies = recordMapper.countTodayAnomalies(userId, deptName, todayStart, todayEnd);

        vo.setTodayCount(todayTotal);
        vo.setTodayAnomalies(todayAnomalies);
        vo.setAnomalyRate(todayTotal > 0
                ? Math.round(((double) todayAnomalies / todayTotal) * 10000.0) / 100.0
                : 0.0);

        // 总巡检数（调用 Phase 13 统计）
        StatisticsQueryDTO statsQuery = new StatisticsQueryDTO();
        if (deptName != null) statsQuery.setDepartment(deptName);
        OverviewVO overview = statisticsService.getOverview(statsQuery);
        vo.setTotalCount(overview.getTotalRecords());

        // 最近故障记录（最多5条）
        List<Map<String, Object>> faultRows = recordMapper.selectRecentFaults(userId, deptName, 5);
        vo.setRecentFaults(buildRecentRecords(faultRows));

        // 最近隐患记录（最多5条）
        List<Map<String, Object>> hiddenRows = recordMapper.selectRecentHiddens(userId, deptName, 5);
        vo.setRecentHiddens(buildRecentRecords(hiddenRows));

        // 高风险区域 TOP3（调用 Phase 13 区域统计）
        List<AreaStatVO> areaStats = statisticsService.getAreaStats(statsQuery);
        vo.setTopRiskAreas(areaStats.size() > 3 ? areaStats.subList(0, 3) : areaStats);

        return vo;
    }

    private List<DashboardVO.RecentRecord> buildRecentRecords(List<Map<String, Object>> rows) {
        List<DashboardVO.RecentRecord> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            DashboardVO.RecentRecord r = new DashboardVO.RecentRecord();
            r.setId(((Number) row.get("id")).longValue());
            r.setDeviceName((String) row.get("device_name"));
            r.setArea((String) row.get("area"));
            r.setDescription((String) row.get("description"));
            Object timeObj = row.get("inspection_time");
            if (timeObj instanceof LocalDateTime) {
                r.setInspectionTime((LocalDateTime) timeObj);
            } else if (timeObj != null) {
                r.setInspectionTime(LocalDateTime.parse(timeObj.toString().replace(" ", "T")));
            }
            list.add(r);
        }
        return list;
    }
}