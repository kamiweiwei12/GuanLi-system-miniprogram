/**
 * 巡检记录 API 模块
 * 封装后端接口调用，页面禁止直接写 URL 或 wx.request
 */
import { get, post } from '../utils/request';
import { upload } from '../utils/upload';

interface InspectionReportDTO {
    department: string;
    employeeName: string;
    phoneNumber: string;
    area: string;
    inspectionTime: string;
    deviceName: string;
    result: number;
    description?: string;
    imageUrls?: string[];
}

interface InspectionQueryDTO {
    pageNum?: number;
    pageSize?: number;
    area?: string;
    result?: number;
    deviceName?: string;
    startTime?: string;
    endTime?: string;
}

export function submitReport(data: InspectionReportDTO): Promise<number> {
    return post<number>('/report', data);
}

export function queryReport(params: InspectionQueryDTO): Promise<any> {
    return get('/report', params);
}

export function getReportDetail(id: number): Promise<any> {
    return get(`/report/${id}`);
}

export function login(code: string): Promise<LoginVO> {
    return post<LoginVO>('/login', { code });
}

export function uploadImage(filePath: string): Promise<string> {
    return upload(filePath);
}

interface LoginVO {
    token: string;
    userId: number;
    role: string;
}
