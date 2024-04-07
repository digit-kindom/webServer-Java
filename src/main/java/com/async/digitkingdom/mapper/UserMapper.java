package com.async.digitkingdom.mapper;

import com.async.digitkingdom.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserByName(String username);
}
