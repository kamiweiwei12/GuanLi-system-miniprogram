/**
 * 统计 API
 */
import http from './request';

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