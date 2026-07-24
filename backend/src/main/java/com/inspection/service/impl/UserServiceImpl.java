package com.inspection.service.impl;

import com.inspection.entity.User;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.UserMapper;
import com.inspection.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    /** EMPLOYEE 角色 ID（与 init.sql 预置数据对应） */
    private static final long ROLE_EMPLOYEE_ID = 1L;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getByOpenid(String openid) {
        return userMapper.selectByOpenid(openid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createDefaultUser(String openid) {
        User user = new User();
        user.setOpenid(openid);
        user.setUsername("微信用户");
        user.setRoleId(ROLE_EMPLOYEE_ID);
        user.setStatus(1); // 正常
        userMapper.insert(user);
        return user;
    }

    @Override
    public void update(User user) {
        if (user.getId() == null) {
            throw new BusinessException("用户 ID 不能为空");
        }
        userMapper.update(user);
    }
}