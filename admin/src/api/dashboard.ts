/**
 * Dashboard 首页 API — GET /api/v1/dashboard
 * Phase 14: 今日实时统计 + 最近异常 + 高风险区域
 */
import http from './request';

export interface RecentRecord {
    id: number;
    deviceName: string;
    area: string;
    description: string;
    inspectionTime: string;
}

export interface DashboardVO {
    todayCount: number;
    todayAnomalies: number;
    anomalyRate: number;
    totalCount: number;
    recentFaults: RecentRecord[];
    recentHiddens: RecentRecord[];
    topRiskAreas: { area: string; anomalyRate: number }[];
}

export function getDashboard(): Promise<DashboardVO> {
    return http.get('/dashboard');
}