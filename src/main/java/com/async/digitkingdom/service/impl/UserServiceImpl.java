package com.async.digitkingdom.service.impl;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.common.exception.BadRequestException;
import com.async.digitkingdom.entity.dto.RegisterUserDTO;
import com.async.digitkingdom.mapper.UserMapper;
import com.async.digitkingdom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result register(RegisterUserDTO registerUserDTO) {
        registerUserDTO.setDateJoined(LocalDate.now());
        registerUserDTO.setLastLogin(LocalDate.now());
        if(registerUserDTO.getUsername() == null){
            throw new BadRequestException("传入的用户名为空！");
        }
        if(userMapper.getUserByName(registerUserDTO.getUsername()) != null){
            throw new BadRequestException("用户名已存在！");
        }

        if(registerUserDTO.getEmail() == null){
            throw new BadRequestException("传入的邮箱为空！");
        }
        if(registerUserDTO.getFirstName() == null){
            throw new BadRequestException("传入的名字为空！");
        }
        if(registerUserDTO.getLastName() == null){
            throw new BadRequestException("传入的姓氏为空！");
        }
        String password = registerUserDTO.getPassword();
        if(password == null){
            throw new BadRequestException("传入的密码为空！");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(password);
        registerUserDTO.setIsActive(true);
        registerUserDTO.setPassword(encode);
        userMapper.insert(registerUserDTO);

        return Result.ok("注册成功！");
    }
}
