/**
 * 巡检记录 API
 */
import http from './request';

export interface InspectionRecordVO {
    id: number;
    department: string;
    employeeName: string;
    phoneNumber: string;
    area: string;
    inspectionTime: string;
    deviceName: string;
    result: number;
    description: string;
    imageUrls: string[];
    createdAt: string;
}

export interface PageResult<T> {
    list: T[];
    total: number;
    pageNum: number;
    pageSize: number;
}

export interface ReportQueryParams {
    pageNum?: number;
    pageSize?: number;
    area?: string;
    result?: number;
    deviceName?: string;
    startTime?: string;
    endTime?: string;
}

export function getReportList(params?: ReportQueryParams): Promise<PageResult<InspectionRecordVO>> {
    return http.get('/report', { params });
}

export function getReportDetail(id: number): Promise<InspectionRecordVO> {
    return http.get(`/report/${id}`);
}