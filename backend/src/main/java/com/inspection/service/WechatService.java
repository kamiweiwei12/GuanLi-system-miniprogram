package com.inspection.service;

/**
 * 微信服务接口
 * 封装微信小程序 API 调用
 */
public interface WechatService {

    /**
     * 通过登录 code 换取微信 openid
     * @param code wx.login() 获取的临时登录凭证
     * @return openid（微信用户唯一标识）
     * @throws com.inspection.exception.BusinessException 当 code 无效或微信 API 返回错误时
     */
    String getOpenid(String code);
}