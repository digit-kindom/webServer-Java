package com.async.digitkingdom.mapper;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.User;
import com.async.digitkingdom.entity.dto.RegisterUserDTO;
import com.async.digitkingdom.entity.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 通过用户名查找用户
     * @param username
     * @return
     */
    User getUserByName(String username);

    /**
     * 用户注册
     * @param registerUserDTO
     */
    void insert(RegisterUserDTO registerUserDTO);

    /**
     * 管理员获取用户信息
     * @return
     */
    List<UserVO> getAllUser();
}
