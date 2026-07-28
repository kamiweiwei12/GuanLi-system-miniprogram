package com.inspection.dto;

/**
 * 微信小程序登录请求参数
 */
public class LoginDTO {

    /** 微信登录 code（wx.login() 获取） */
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}