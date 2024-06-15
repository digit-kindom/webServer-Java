package com.async.digitkingdom.controller;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.dto.LoginDTO;
import com.async.digitkingdom.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController()
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<HashMap<String,String>> login(@RequestBody LoginDTO loginDTO){
        return loginService.login(loginDTO);
    }

    @GetMapping("/logout")
    public Result logout(){
        return loginService.logout();
    }
}
