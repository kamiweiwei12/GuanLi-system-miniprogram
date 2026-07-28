/**
 * 巡检记录 API
 */
import http from './request';
import axios from 'axios';
import { getToken } from '@/utils/storage';

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
    area?: string | null;
    result?: number | null;
    deviceName?: string | null;
    startTime?: string;
    endTime?: string;
}

export interface FilterOptions {
    areas: string[];
    devices: string[];
}

export function getReportList(params?: ReportQueryParams): Promise<PageResult<InspectionRecordVO>> {
    return http.get('/report', { params });
}

export function getReportDetail(id: number): Promise<InspectionRecordVO> {
    return http.get(`/report/${id}`);
}

/**
 * 获取筛选选项（区域和设备列表）
 */
export function getFilterOptions(): Promise<FilterOptions> {
    return http.get('/report/options');
}

/**
 * 导出巡检记录（返回 blob 文件下载）
 * 需要单独处理：设置 responseType: 'blob'，绕过 http 拦截器的 JSON 解包
 */
export async function exportReport(params?: ReportQueryParams): Promise<void> {
    const token = getToken();
    const response = await axios.get(
        `${import.meta.env.VITE_API_BASE_URL}/report/export`,
        {
            params: {
                area: params?.area || undefined,
                deviceName: params?.deviceName || undefined,
                result: params?.result ?? undefined,
                startTime: params?.startTime,
                endTime: params?.endTime,
            },
            responseType: 'blob',
            headers: token ? { Authorization: `Bearer ${token}` } : {},
        }
    );

    const contentDisposition = response.headers['content-disposition'];
    let filename = 'inspection_report.xlsx';
    if (contentDisposition) {
        const match = contentDisposition.match(/filename\*=UTF-8''(.+)/);
        if (match) {
            filename = decodeURIComponent(match[1]);
        } else {
            const match2 = contentDisposition.match(/filename="(.+)"/);
            if (match2) {
                filename = match2[1];
            }
        }
    }

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
}