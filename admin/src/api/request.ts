/**
 * Axios 请求封装
 * - 自动注入 Token
 * - 统一解包 Result<T> 返回 data
 * - 不直接依赖 Vue Router 或 UI 组件
 */
import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { getToken, removeToken } from '@/utils/storage';

interface ApiResponse<T = any> {
    code: number;
    message: string;
    data: T;
}

const http: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000,
});

// 请求拦截器：注入 Token
http.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// 响应拦截器：解包 Result<T>
http.interceptors.response.use(
    (response: AxiosResponse<ApiResponse>) => {
        const body = response.data;
        if (body.code === 200) {
            return body.data as any;
        }
        // 401: Token 无效或过期，清除 Token
        if (body.code === 401) {
            removeToken();
        }
        // 403: 权限不足，保留 Token（用户仍处于登录状态）
        return Promise.reject(new Error(body.message || '请求失败'));
    },
    (error) => {
        // 401: Token 无效或过期，清除 Token
        if (error.response?.status === 401) {
            removeToken();
        }
        // 403: 权限不足，保留 Token
        return Promise.reject(error);
    }
);

export default http;