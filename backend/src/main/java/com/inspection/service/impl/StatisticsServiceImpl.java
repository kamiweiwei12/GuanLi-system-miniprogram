package com.inspection.service.impl;

import com.inspection.common.UserContext;
import com.inspection.dto.StatisticsQueryDTO;
import com.inspection.entity.Department;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.DepartmentMapper;
import com.inspection.mapper.InspectionRecordMapper;
import com.inspection.service.StatisticsService;
import com.inspection.vo.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    // ==================== 已有方法（保持不变） ====================

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

    // ==================== 新增统计方法 ====================

    @Override
    public OverviewVO getOverview(StatisticsQueryDTO dto) {
        applyPermissionFilter(dto);

        long total = recordMapper.countForStats(dto);
        List<Map<String, Object>> resultRows = recordMapper.countByResult(dto);

        long normal = 0, fault = 0, hidden = 0;
        for (Map<String, Object> row : resultRows) {
            int r = ((Number) row.get("result")).intValue();
            long c = ((Number) row.get("cnt")).longValue();
            if (r == 0) normal = c;
            else if (r == 1) fault = c;
            else if (r == 2) hidden = c;
        }
        return new OverviewVO(total, normal, fault, hidden);
    }

    @Override
    public List<AreaStatVO> getAreaStats(StatisticsQueryDTO dto) {
        applyPermissionFilter(dto);

        List<Map<String, Object>> rows = recordMapper.countByArea(dto);

        // 按 area 聚合 result -> cnt
        Map<String, Map<Integer, Long>> areaMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String area = (String) row.get("area");
            int result = ((Number) row.get("result")).intValue();
            long cnt = ((Number) row.get("cnt")).longValue();

            areaMap.computeIfAbsent(area, k -> new HashMap<>())
                   .put(result, cnt);
        }

        return areaMap.entrySet().stream().map(entry -> {
            String area = entry.getKey();
            Map<Integer, Long> resultCounts = entry.getValue();
            long normal = resultCounts.getOrDefault(0, 0L);
            long fault = resultCounts.getOrDefault(1, 0L);
            long hidden = resultCounts.getOrDefault(2, 0L);
            long total = normal + fault + hidden;
            return new AreaStatVO(area, total, normal, fault, hidden);
        }).sorted((a, b) -> Double.compare(b.getAnomalyRate(), a.getAnomalyRate()))
          .collect(Collectors.toList());
    }

    @Override
    public DeviceStatVO getDeviceStats(StatisticsQueryDTO dto) {
        applyPermissionFilter(dto);

        List<Map<String, Object>> rows = recordMapper.countByDevice(dto);

        // 按 device_name 聚合 result -> cnt
        Map<String, Map<Integer, Long>> deviceMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String device = (String) row.get("device_name");
            int result = ((Number) row.get("result")).intValue();
            long cnt = ((Number) row.get("cnt")).longValue();

            deviceMap.computeIfAbsent(device, k -> new HashMap<>())
                     .put(result, cnt);
        }

        DeviceStatVO vo = new DeviceStatVO();

        // 高频设备 TOP10
        vo.setHighFrequencyDevices(
            deviceMap.entrySet().stream()
                .map(e -> {
                    long total = e.getValue().values().stream().mapToLong(Long::longValue).sum();
                    return new DeviceStatVO.DeviceItem(e.getKey(), total);
                })
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .limit(10)
                .collect(Collectors.toList())
        );

        // 故障设备 TOP10（按故障率降序）
        vo.setFaultDevices(
            deviceMap.entrySet().stream()
                .map(e -> {
                    Map<Integer, Long> rc = e.getValue();
                    long total = rc.values().stream().mapToLong(Long::longValue).sum();
                    long fault = rc.getOrDefault(1, 0L);
                    return new DeviceStatVO.FaultDeviceItem(e.getKey(), fault, total);
                })
                .filter(d -> d.getFaultCount() > 0)
                .sorted((a, b) -> Double.compare(b.getFaultRate(), a.getFaultRate()))
                .limit(10)
                .collect(Collectors.toList())
        );

        // 风险设备（隐患）TOP10（按隐患率降序）
        vo.setRiskDevices(
            deviceMap.entrySet().stream()
                .map(e -> {
                    Map<Integer, Long> rc = e.getValue();
                    long total = rc.values().stream().mapToLong(Long::longValue).sum();
                    long hidden = rc.getOrDefault(2, 0L);
                    return new DeviceStatVO.HiddenDeviceItem(e.getKey(), hidden, total);
                })
                .filter(d -> d.getHiddenCount() > 0)
                .sorted((a, b) -> Double.compare(b.getHiddenRate(), a.getHiddenRate()))
                .limit(10)
                .collect(Collectors.toList())
        );

        return vo;
    }

    @Override
    public List<TrendStatVO> getTrendStats(StatisticsQueryDTO dto) {
        applyPermissionFilter(dto);

        List<Map<String, Object>> rows = recordMapper.countByDateWithResult(dto);

        // 按 date 聚合 result -> cnt
        Map<String, Map<Integer, Long>> dateMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String date = row.get("date").toString();
            int result = ((Number) row.get("result")).intValue();
            long cnt = ((Number) row.get("cnt")).longValue();

            dateMap.computeIfAbsent(date, k -> new HashMap<>())
                   .put(result, cnt);
        }

        return dateMap.entrySet().stream().map(entry -> {
            String date = entry.getKey();
            Map<Integer, Long> rc = entry.getValue();
            long normal = rc.getOrDefault(0, 0L);
            long fault = rc.getOrDefault(1, 0L);
            long hidden = rc.getOrDefault(2, 0L);
            long total = normal + fault + hidden;
            return new TrendStatVO(date, total, normal, fault, hidden);
        }).sorted((a, b) -> b.getDate().compareTo(a.getDate()))
          .collect(Collectors.toList());
    }

    // ==================== 私有方法 ====================

    /**
     * 根据当前用户角色设置权限过滤参数
     * ADMIN → 无过滤
     * DEPT_ADMIN → 过滤 department
     * EMPLOYEE → 过滤 user_id
     */
    private void applyPermissionFilter(StatisticsQueryDTO dto) {
        String roleCode = UserContext.getRoleCode();

        if ("ADMIN".equals(roleCode)) {
            return;
        } else if ("DEPT_ADMIN".equals(roleCode)) {
            Long deptId = UserContext.getDepartmentId();
            if (deptId != null) {
                Department dept = departmentMapper.selectById(deptId);
                if (dept != null) {
                    dto.setDepartment(dept.getDeptName());
                }
            }
        } else if ("EMPLOYEE".equals(roleCode)) {
            dto.setUserId(UserContext.getUserId());
        } else {
            throw new BusinessException(403, "无权限查看统计数据");
        }
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