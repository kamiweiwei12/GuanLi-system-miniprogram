package com.inspection.vo;

/**
 * 微信小程序登录响应
 */
public class LoginVO {

    /** 认证令牌 */
    private String token;

    /** 用户 ID */
    private Long userId;

    /** 角色编码（EMPLOYEE / DEPT_ADMIN / ADMIN） */
    private String role;

    public LoginVO() {
    }

    public LoginVO(String token, Long userId, String role) {
        this.token = token;
        this.userId = userId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}