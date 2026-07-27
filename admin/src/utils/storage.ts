/**
 * 管理后台本地存储工具（使用 localStorage，与小程序 storage 独立）
 */

const TOKEN_KEY = 'inspection_admin_token';

export function setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
}

export function getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
}

export function removeToken(): void {
    localStorage.removeItem(TOKEN_KEY);
}

export function clear(): void {
    localStorage.clear();
}

export default { setToken, getToken, removeToken, clear };