package com.inspection.config;

import com.inspection.common.UserContext;
import com.inspection.entity.Role;
import com.inspection.entity.User;
import com.inspection.mapper.RoleMapper;
import com.inspection.mapper.UserMapper;
import com.inspection.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 校验 Authorization: Bearer {token}，解析 userId/roleId/roleCode 存入 UserContext
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public AuthInterceptor(TokenService tokenService, UserMapper userMapper, RoleMapper roleMapper) {
        this.tokenService = tokenService;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response, "缺少认证令牌");
            return false;
        }

        String tokenValue = authorization.substring(7).trim();
        Long userId = tokenService.getUserIdByToken(tokenValue);

        if (userId == null) {
            writeUnauthorized(response, "令牌无效或已过期");
            return false;
        }

        // 查询用户信息获取角色
        User user = userMapper.selectById(userId);
        if (user == null) {
            writeUnauthorized(response, "用户不存在");
            return false;
        }
        Role role = roleMapper.selectById(user.getRoleId());
        String roleCode = role != null ? role.getRoleCode() : "EMPLOYEE";

        UserContext.setUser(userId, user.getRoleId(), roleCode);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}");
    }
}