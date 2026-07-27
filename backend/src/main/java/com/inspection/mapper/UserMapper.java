package com.inspection.mapper;

import com.inspection.dto.UserQueryDTO;
import com.inspection.entity.User;
import com.inspection.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统用户 Mapper
 */
@Mapper
public interface UserMapper {

    int insert(User user);

    User selectById(Long id);

    User selectByOpenid(String openid);

    int update(User user);

    /** 分页查询用户列表（JOIN sys_role + sys_department） */
    List<UserVO> selectPage(UserQueryDTO dto);

    /** 用户总数（带筛选条件） */
    long countUsers(UserQueryDTO dto);
}