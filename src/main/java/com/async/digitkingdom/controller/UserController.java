package com.async.digitkingdom.controller;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.dto.RegisterUserDTO;
import com.async.digitkingdom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterUserDTO registerUserDTO){
        return userService.register(registerUserDTO);
    }
}
