package com.inspection.mapper;

import com.inspection.entity.Token;
import org.apache.ibatis.annotations.Mapper;

/**
 * 认证令牌 Mapper
 */
@Mapper
public interface TokenMapper {

    int insert(Token token);

    Token selectByToken(String tokenValue);

    Token selectByUserId(Long userId);

    int deleteByUserId(Long userId);
}