package com.inspection.service;

import com.inspection.common.PageResult;
import com.inspection.dto.UserQueryDTO;
import com.inspection.entity.User;
import com.inspection.vo.UserVO;

/**
 * 系统用户服务接口
 */
public interface UserService {

    /** 根据 openid 查询用户 */
    User getByOpenid(String openid);

    /** 创建默认用户（首次微信登录时调用） */
    User createDefaultUser(String openid);

    /** 更新用户信息 */
    void update(User user);

    /** 分页查询用户列表（仅 ADMIN） */
    PageResult<UserVO> listUsers(UserQueryDTO dto);

    /** 修改用户角色（仅 ADMIN，roleId 变更后删除 token 强制重新登录） */
    void updateRole(Long userId, Long roleId);

    /** 修改用户状态（仅 ADMIN，0=禁用, 1=正常） */
    void updateStatus(Long userId, Integer status);
}