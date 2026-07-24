package com.inspection.service;

import com.inspection.entity.User;

/**
 * 系统用户服务接口
 */
public interface UserService {

    /**
     * 根据 openid 查询用户
     */
    User getByOpenid(String openid);

    /**
     * 创建默认用户（首次微信登录时调用）
     * 自动设置默认角色 EMPLOYEE、状态正常、用户名为默认值
     */
    User createDefaultUser(String openid);

    /**
     * 更新用户信息
     */
    void update(User user);
}