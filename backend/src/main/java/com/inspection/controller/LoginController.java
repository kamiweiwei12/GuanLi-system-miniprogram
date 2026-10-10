package com.inspection.controller;

import com.inspection.common.Result;
import com.inspection.dto.LoginDTO;
import com.inspection.entity.Role;
import com.inspection.entity.User;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.RoleMapper;
import com.inspection.service.OperationLogService;
import com.inspection.service.TokenService;
import com.inspection.service.UserService;
import com.inspection.service.WechatService;
import com.inspection.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 登录控制器 — 负责登录流程编排
 * Phase 14: 新增操作日志记录
 */
@RestController
@RequestMapping("/api/v1")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final WechatService wechatService;
    private final UserService userService;
    private final TokenService tokenService;
    private final RoleMapper roleMapper;
    private final OperationLogService logService;

    public LoginController(WechatService wechatService,
                           UserService userService,
                           TokenService tokenService,
                           RoleMapper roleMapper,
                           OperationLogService logService) {
        this.wechatService = wechatService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.roleMapper = roleMapper;
        this.logService = logService;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new BusinessException(400, "登录凭证 code 不能为空");
        }

        String openid = wechatService.getOpenid(dto.getCode());
        log.info("微信登录: openid={}", openid.substring(0, Math.min(8, openid.length())) + "***");

        User user = userService.getByOpenid(openid);
        if (user == null) {
            user = userService.createDefaultUser(openid);
            log.info("新用户注册: userId={}", user.getId());
        }

        String token = tokenService.createToken(user.getId());

        Role role = roleMapper.selectById(user.getRoleId());
        String roleCode = role != null ? role.getRoleCode() : "EMPLOYEE";

        // Phase 14: 记录登录操作日志
        logService.log(user.getId(), "LOGIN", "用户登录: " + user.getUsername(), request);

        LoginVO vo = new LoginVO(token, user.getId(), roleCode);
        return Result.success(vo);
    }
}