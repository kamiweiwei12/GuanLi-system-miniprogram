package com.inspection.service.impl;

import com.inspection.entity.Token;
import com.inspection.mapper.TokenMapper;
import com.inspection.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 认证令牌服务实现
 */
@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);
    private static final int TOKEN_EXPIRE_DAYS = 7;

    private final TokenMapper tokenMapper;

    public TokenServiceImpl(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createToken(Long userId) {
        // 先失效旧 token
        tokenMapper.deleteByUserId(userId);

        Token token = new Token();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setExpireTime(LocalDateTime.now().plusDays(TOKEN_EXPIRE_DAYS));
        tokenMapper.insert(token);

        log.info("Token created: userId={}, expireTime={}", userId, token.getExpireTime());
        return token.getToken();
    }

    @Override
    public Long getUserIdByToken(String tokenValue) {
        Token token = tokenMapper.selectByToken(tokenValue);
        if (token == null) {
            log.warn("Token not found: {}", tokenValue);
            return null;
        }
        if (token.getExpireTime().isBefore(LocalDateTime.now())) {
            log.warn("Token expired: userId={}, expireTime={}", token.getUserId(), token.getExpireTime());
            return null;
        }
        return token.getUserId();
    }

    @Override
    public void invalidate(Long userId) {
        tokenMapper.deleteByUserId(userId);
        log.info("Token invalidated: userId={}", userId);
    }
}