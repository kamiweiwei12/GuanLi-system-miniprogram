package com.inspection.service;

/**
 * 认证令牌服务接口
 */
public interface TokenService {

    /**
     * 为用户创建认证令牌
     * @param userId 用户 ID
     * @return token 字符串（UUID）
     */
    String createToken(Long userId);

    /**
     * 根据 token 查询用户 ID
     * @param tokenValue token 字符串
     * @return userId，过期或不存在返回 null
     */
    Long getUserIdByToken(String tokenValue);

    /**
     * 失效指定用户的 token（用于登出或重新登录）
     * @param userId 用户 ID
     */
    void invalidate(Long userId);
}