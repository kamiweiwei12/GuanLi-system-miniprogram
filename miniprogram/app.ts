import { login } from './api/report';
import storage from './utils/storage';

// 全局数据类型
interface IAppOption {
    globalData: {
        role?: string;
    };
}

// app.ts
App<IAppOption>({
    globalData: {
        role: undefined,
    },
    onLaunch() {
        // 微信小程序登录
        wx.login({
            success: async (res) => {
                if (res.code) {
                    try {
                        const data = await login(res.code);
                        storage.setToken(data.token);
                        storage.setRole(data.role);
                        // 同步更新 globalData
                        const app = getApp<IAppOption>();
                        app.globalData.role = data.role;
                        console.log('登录成功: userId=', data.userId, 'role=', data.role);
                    } catch (err) {
                        console.error('登录失败', err);
                    }
                } else {
                    console.error('wx.login 未返回 code');
                }
            },
            fail(err) {
                console.error('wx.login 调用失败', err);
            }
        });
    },
})