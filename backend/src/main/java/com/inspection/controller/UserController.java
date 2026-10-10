package com.inspection.controller;

import com.inspection.common.PageResult;
import com.inspection.common.Result;
import com.inspection.common.UserContext;
import com.inspection.dto.UserQueryDTO;
import com.inspection.dto.UserUpdateRoleDTO;
import com.inspection.dto.UserUpdateStatusDTO;
import com.inspection.service.OperationLogService;
import com.inspection.service.UserService;
import com.inspection.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器（仅 ADMIN）
 * Phase 14: 新增角色修改操作日志
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final OperationLogService logService;

    public UserController(UserService userService, OperationLogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    @GetMapping("/users")
    public Result<PageResult<UserVO>> listUsers(UserQueryDTO dto) {
        PageResult<UserVO> page = userService.listUsers(dto);
        return Result.success(page);
    }

    @PutMapping("/users/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody UserUpdateRoleDTO dto,
                                    HttpServletRequest request) {
        userService.updateRole(id, dto.getRoleId());
        logService.log(UserContext.getUserId(), "UPDATE_ROLE",
                "修改用户角色: userId=" + id + ", newRoleId=" + dto.getRoleId(), request);
        return Result.success(null);
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody UserUpdateStatusDTO dto) {
        userService.updateStatus(id, dto.getStatus());
        return Result.success(null);
    }
}