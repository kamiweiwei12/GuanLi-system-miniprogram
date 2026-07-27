/**
 * 角色权限工具
 * 封装角色判断逻辑，避免散落在各页面中
 */
import storage from './storage';

/** 移除登录信息（token + role） */
export function clearAuth(): void {
    storage.removeToken();
    storage.removeRole();
}

/** 当前是否为管理员 */
export function isAdmin(): boolean {
    return storage.getRole() === 'ADMIN';
}

export default { isAdmin, clearAuth };