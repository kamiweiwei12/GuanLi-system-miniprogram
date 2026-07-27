/**
 * 用户管理 API
 */
import http from './request';

export interface UserVO {
    id: number;
    nickname: string;
    phone: string;
    departmentName: string;
    roleCode: string;
    roleName: string;
    status: number;
    createdAt: string;
}

export interface PageResult<T> {
    list: T[];
    total: number;
    pageNum: number;
    pageSize: number;
}

export interface UserQueryParams {
    pageNum?: number;
    pageSize?: number;
    nickname?: string;
    phone?: string;
    roleCode?: string;
    departmentId?: number;
}

const ROLE_OPTIONS = ['ADMIN', 'DEPT_ADMIN', 'EMPLOYEE'];

export function getRoleOptions() {
    return ROLE_OPTIONS;
}

export function getUsers(params?: UserQueryParams): Promise<PageResult<UserVO>> {
    return http.get('/users', { params });
}

export function updateUserRole(userId: number, roleId: number): Promise<void> {
    return http.put(`/users/${userId}/role`, { roleId });
}

export function updateUserStatus(userId: number, status: number): Promise<void> {
    return http.put(`/users/${userId}/status`, { status });
}