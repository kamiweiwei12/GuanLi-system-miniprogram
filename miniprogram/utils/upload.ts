/**
 * 文件上传封装
 * 统一处理上传地址拼接、Promise 包装、错误传递
 * 注意：此工具不处理 UI（Toast/Loading），由调用方自行处理
 */
import config from '../config/env';
import storage from './storage';

interface ApiResponse<T = any> {
    code: number;
    message: string;
    data: T;
    timestamp: string;
}

/**
 * 获取 Authorization header 值
 */
function getAuthHeader(): string {
    const token = storage.getToken();
    return token ? 'Bearer ' + token : '';
}

export function upload(filePath: string): Promise<string> {
    return new Promise((resolve, reject) => {
        wx.uploadFile({
            url: config.uploadUrl,
            filePath,
            name: 'file',
            header: {
                'Authorization': getAuthHeader()
            },
            success(res) {
                try {
                    const body: ApiResponse<string> = JSON.parse(res.data);
                    if (body.code === 200) {
                        resolve(body.data);
                    } else {
                        reject(body);
                    }
                } catch (e) {
                    reject({ code: 500, message: '解析上传响应失败' });
                }
            },
            fail(err) {
                reject(err);
            }
        });
    });
}

export default { upload };