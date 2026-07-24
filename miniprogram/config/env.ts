/**
 * 环境配置
 * 统一管理后端接口地址，避免硬编码到各页面。
 *
 * 切换环境：修改 IS_DEV 变量。
 *   - true  = 开发环境（localhost）
 *   - false = 生产环境（修改 PROD_HOST 为实际部署地址）
 */
const IS_DEV = true;
const DEV_HOST = 'http://localhost:8080';
const PROD_HOST = 'https://your-domain.com';

const HOST = IS_DEV ? DEV_HOST : PROD_HOST;

export default {
    baseUrl: HOST + '/api/v1',
    uploadUrl: HOST + '/api/v1/upload/image'
};