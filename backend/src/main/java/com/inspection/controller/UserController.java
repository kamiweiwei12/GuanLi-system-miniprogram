package com.inspection.controller;

import com.inspection.common.PageResult;
import com.inspection.common.Result;
import com.inspection.dto.UserQueryDTO;
import com.inspection.dto.UserUpdateRoleDTO;
import com.inspection.dto.UserUpdateStatusDTO;
import com.inspection.service.UserService;
import com.inspection.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器（仅 ADMIN）
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 用户列表（仅 ADMIN） */
    @GetMapping("/users")
    public Result<PageResult<UserVO>> listUsers(UserQueryDTO dto) {
        PageResult<UserVO> page = userService.listUsers(dto);
        return Result.success(page);
    }

    /** 修改用户角色（仅 ADMIN） */
    @PutMapping("/users/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody UserUpdateRoleDTO dto) {
        userService.updateRole(id, dto.getRoleId());
        return Result.success(null);
    }

    /** 修改用户状态（仅 ADMIN，0=禁用, 1=正常） */
    @PutMapping("/users/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody UserUpdateStatusDTO dto) {
        userService.updateStatus(id, dto.getStatus());
        return Result.success(null);
    }
}