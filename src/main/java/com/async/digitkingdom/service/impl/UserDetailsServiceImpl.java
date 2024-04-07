package com.async.digitkingdom.service.impl;

import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.entity.User;
import com.async.digitkingdom.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询
        User user = userMapper.getUserByName(username);
        if(Objects.isNull(user)){
            throw new RuntimeException("用户名或密码错误");
        }

        //根据用户查询权限信息 添加到LoginUser中
        List<String> permissions = new ArrayList<>();
        if(user.getIsSuperuser()){
            permissions.add("Superuser");
        }
        if(user.getIsStaff()){
            permissions.add("Staff");
        }
        if(permissions.isEmpty()){
            permissions.add("user");
        }

        return new LoginUser(user, permissions);
    }
}
