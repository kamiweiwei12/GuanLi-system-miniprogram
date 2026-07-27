package com.inspection.service.impl;

import com.inspection.common.PageResult;
import com.inspection.common.UserContext;
import com.inspection.dto.UserQueryDTO;
import com.inspection.entity.User;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.RoleMapper;
import com.inspection.mapper.TokenMapper;
import com.inspection.mapper.UserMapper;
import com.inspection.service.UserService;
import com.inspection.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final TokenMapper tokenMapper;

    /** EMPLOYEE 角色 ID（与 init.sql 预置数据对应） */
    private static final long ROLE_EMPLOYEE_ID = 1L;

    public UserServiceImpl(UserMapper userMapper, RoleMapper roleMapper, TokenMapper tokenMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.tokenMapper = tokenMapper;
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

    @Override
    public PageResult<UserVO> listUsers(UserQueryDTO dto) {
        // 权限校验：仅 ADMIN
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限查看用户列表");
        }

        if (dto.getPageNum() == null || dto.getPageNum() < 1) dto.setPageNum(1);
        if (dto.getPageSize() == null || dto.getPageSize() < 1) dto.setPageSize(10);
        if (dto.getPageSize() > 100) dto.setPageSize(100);

        List<UserVO> list = userMapper.selectPage(dto);
        long total = userMapper.countUsers(dto);
        return new PageResult<>(list, total, dto.getPageNum(), dto.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long userId, Long roleId) {
        // 权限校验：仅 ADMIN
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限修改用户角色");
        }

        // 禁止修改自己的角色
        Long currentUserId = UserContext.getUserId();
        if (userId.equals(currentUserId)) {
            throw new BusinessException(400, "不能修改自己的角色");
        }

        // 目标用户存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 角色存在
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException(400, "角色不存在");
        }

        // 更新角色
        user.setRoleId(roleId);
        userMapper.update(user);

        // 删除 token 强制重新登录
        tokenMapper.deleteByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long userId, Integer status) {
        // 权限校验：仅 ADMIN
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限修改用户状态");
        }

        // 校验状态值
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(400, "状态值非法，允许值：0=禁用, 1=正常");
        }

        // 目标用户存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 更新状态
        user.setStatus(status);
        userMapper.update(user);

        // 如果禁用，删除 token
        if (status == 0) {
            tokenMapper.deleteByUserId(userId);
        }
    }
}