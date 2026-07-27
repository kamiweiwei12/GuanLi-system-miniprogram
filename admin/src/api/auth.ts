/**
 * 认证相关 API
 * 当前使用 Token 登录模式，预留 username/password 扩展
 */
import http from './request';

export interface LoginParams {
    token?: string;
    username?: string;
    password?: string;
}

/**
 * 验证当前 Token 是否有效
 * 通过请求统计接口判断（401/403 表示无效）
 */
export async function validateToken(): Promise<boolean> {
    try {
        await http.get('/statistics');
        return true;
    } catch {
        return false;
    }
}