package com.inspection.mapper;

import com.inspection.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper
 */
@Mapper
public interface UserMapper {

    int insert(User user);

    User selectById(Long id);

    User selectByOpenid(String openid);

    int update(User user);
}