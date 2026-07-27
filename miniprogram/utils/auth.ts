/**
 * 角色权限工具
 * 封装角色判断逻辑，避免散落在各页面中
 */
import storage from './storage';

/** 移除登录信息（token + role + userId） */
export function clearAuth(): void {
    storage.removeToken();
    storage.removeRole();
    storage.removeUserId();
}

/** 当前是否为管理员 */
export function isAdmin(): boolean {
    return storage.getRole() === 'ADMIN';
}

/** 退出登录：清理本地认证状态，不调用后端接口 */
export function logout(): void {
    clearAuth();
    wx.showToast({ title: '已退出登录', icon: 'success', duration: 1500 });
}

export default { isAdmin, clearAuth, logout };