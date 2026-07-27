// pages/index/index.ts
// 巡检系统首页 — 根据角色显示不同功能入口
import auth from '../../utils/auth';

Page({
    data: {
        isAdmin: auth.isAdmin(),
        userInfo: {} as WechatMiniprogram.UserInfo | null,
    },

    onLoad() {
        // 页面加载
    },

    onShow() {
        // 每次显示时刷新角色状态（支持角色切换）
        this.setData({ isAdmin: auth.isAdmin() });
    },

    /** 跳转到巡检记录列表（非管理员查看自己的，管理员查看全部） */
    goToRecord() {
        wx.navigateTo({ url: '/pages/record/record' });
    },

    /** 跳转到巡检上报页面 */
    goToReport() {
        wx.navigateTo({ url: '/pages/report/report' });
    },

    /** 跳转到管理功能页面（预留，未来扩展） */
    goToAdmin() {
        wx.navigateTo({ url: '/pages/record/record' });
    },
});