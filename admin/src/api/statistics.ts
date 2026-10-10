/**
 * 统计 API
 */
import http from './request';

// ==================== 已有类型（Phase 10.3） ====================

export interface DepartmentStat {
    department: string;
    count: number;
}

export interface DateStat {
    date: string;
    count: number;
}

export interface StatisticsVO {
    totalRecords: number;
    byResult: {
        normal: number;
        fault: number;
        hidden: number;
    };
    byDepartment: DepartmentStat[];
    byDate: DateStat[];
}

export function getStatistics(params?: { startDate?: string; endDate?: string }): Promise<StatisticsVO> {
    return http.get('/statistics', { params });
}

// ==================== 新增类型（Phase 13） ====================

/** 统计总览 */
export interface OverviewVO {
    totalRecords: number;
    normalCount: number;
    faultCount: number;
    hiddenCount: number;
    anomalyRate: number;
}

/** 区域统计项 */
export interface AreaStatVO {
    area: string;
    totalCount: number;
    normalCount: number;
    faultCount: number;
    hiddenCount: number;
    anomalyRate: number;
}

/** 设备统计 — 高频设备 */
export interface DeviceItem {
    deviceName: string;
    count: number;
}

/** 设备统计 — 故障设备 */
export interface FaultDeviceItem {
    deviceName: string;
    faultCount: number;
    totalCount: number;
    faultRate: number;
}

/** 设备统计 — 隐患设备 */
export interface HiddenDeviceItem {
    deviceName: string;
    hiddenCount: number;
    totalCount: number;
    hiddenRate: number;
}

/** 设备统计 */
export interface DeviceStatVO {
    highFrequencyDevices: DeviceItem[];
    faultDevices: FaultDeviceItem[];
    riskDevices: HiddenDeviceItem[];
}

/** 趋势统计项 */
export interface TrendStatVO {
    date: string;
    totalCount: number;
    normalCount: number;
    faultCount: number;
    hiddenCount: number;
    anomalyRate: number;
}

// ==================== 查询参数 ====================

export interface StatisticsQueryParams {
    startDate?: string;
    endDate?: string;
    area?: string;
    deviceName?: string;
}

// ==================== 新增 API 函数 ====================

export function getOverview(params?: StatisticsQueryParams): Promise<OverviewVO> {
    return http.get('/statistics/overview', { params });
}

export function getAreaStats(params?: StatisticsQueryParams): Promise<AreaStatVO[]> {
    return http.get('/statistics/area', { params });
}

export function getDeviceStats(params?: StatisticsQueryParams): Promise<DeviceStatVO> {
    return http.get('/statistics/device', { params });
}

export function getTrendStats(params?: StatisticsQueryParams): Promise<TrendStatVO[]> {
    return http.get('/statistics/trend', { params });
}