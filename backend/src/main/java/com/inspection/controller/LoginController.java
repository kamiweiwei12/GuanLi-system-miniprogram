package com.inspection.controller;

import com.inspection.common.Result;
import com.inspection.dto.LoginDTO;
import com.inspection.entity.Role;
import com.inspection.entity.User;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.RoleMapper;
import com.inspection.service.TokenService;
import com.inspection.service.UserService;
import com.inspection.service.WechatService;
import com.inspection.vo.LoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信小程序登录控制器
 * 负责登录流程编排：code → openid → 查/创用户 → 生成 token
 */
@RestController
@RequestMapping("/api/v1")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final WechatService wechatService;
    private final UserService userService;
    private final TokenService tokenService;
    private final RoleMapper roleMapper;

    public LoginController(WechatService wechatService,
                           UserService userService,
                           TokenService tokenService,
                           RoleMapper roleMapper) {
        this.wechatService = wechatService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.roleMapper = roleMapper;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        // 1. 参数校验
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new BusinessException(400, "登录凭证 code 不能为空");
        }

        // 2. code → openid
        String openid = wechatService.getOpenid(dto.getCode());
        log.info("微信登录: openid={}", openid.substring(0, Math.min(8, openid.length())) + "***");

        // 3. 查询或创建用户
        User user = userService.getByOpenid(openid);
        if (user == null) {
            user = userService.createDefaultUser(openid);
            log.info("新用户注册: userId={}, openid={}", user.getId(), openid.substring(0, Math.min(8, openid.length())) + "***");
        }

        // 4. 生成 token
        String token = tokenService.createToken(user.getId());

        // 5. 查询角色编码
        Role role = roleMapper.selectById(user.getRoleId());
        String roleCode = role != null ? role.getRoleCode() : "EMPLOYEE";

        // 6. 构造响应
        LoginVO vo = new LoginVO(token, user.getId(), roleCode);
        return Result.success(vo);
    }
}