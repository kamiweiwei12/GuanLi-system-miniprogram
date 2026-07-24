/**
 * 本地存储工具
 * 统一封装 wx.storage，方便后续切换存储方案
 */

function set(key: string, value: any): void {
    try {
        wx.setStorageSync(key, value);
    } catch (e) {
        console.error('storage.set 失败', e);
    }
}

function get(key: string): any {
    try {
        return wx.getStorageSync(key);
    } catch (e) {
        console.error('storage.get 失败', e);
        return null;
    }
}

function remove(key: string): void {
    try {
        wx.removeStorageSync(key);
    } catch (e) {
        console.error('storage.remove 失败', e);
    }
}

function clear(): void {
    try {
        wx.clearStorageSync();
    } catch (e) {
        console.error('storage.clear 失败', e);
    }
}

export default { set, get, remove, clear };