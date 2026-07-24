/**
 * 网络请求封装
 * 统一处理 baseUrl 拼接、Promise 包装、错误传递
 * 注意：此工具不处理 UI（Toast/Loading），由调用方自行处理
 */
import config from '../config/env';

interface RequestOptions {
    url: string;
    method?: 'GET' | 'POST';
    data?: any;
    header?: Record<string, string>;
}

interface ApiResponse<T = any> {
    code: number;
    message: string;
    data: T;
    timestamp: string;
}

function request<T = any>(options: RequestOptions): Promise<T> {
    return new Promise((resolve, reject) => {
        wx.request({
            url: config.baseUrl + options.url,
            method: options.method || 'GET',
            data: options.data || {},
            header: {
                'Content-Type': 'application/json',
                ...options.header
            },
            success(res) {
                const body = res.data as ApiResponse<T>;
                if (res.statusCode === 200 && body.code === 200) {
                    resolve(body.data);
                } else {
                    reject(body);
                }
            },
            fail(err) {
                reject(err);
            }
        });
    });
}

export function get<T = any>(url: string, data?: any): Promise<T> {
    return request<T>({ url, method: 'GET', data });
}

export function post<T = any>(url: string, data?: any): Promise<T> {
    return request<T>({ url, method: 'POST', data });
}

export default { get, post };