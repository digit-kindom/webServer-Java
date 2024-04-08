package com.async.digitkingdom.mapper;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.User;
import com.async.digitkingdom.entity.dto.RegisterUserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserByName(String username);

    void insert(RegisterUserDTO registerUserDTO);
}
