package com.inspection.dto;

/**
 * 修改用户状态请求
 */
public class UserUpdateStatusDTO {

    /** 状态：1=正常, 0=禁用 */
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}