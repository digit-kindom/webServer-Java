package com.async.digitkingdom.controller;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.vo.UserVO;
import com.async.digitkingdom.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
//@PreAuthorize("hasAnyAuthority('Superuser')")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/userlist")
    public Result<PageInfo<UserVO>> getAllUser(Integer pageNumber,Integer pageSize){
        return userService.getAllUser(pageNumber,pageSize);
    }
}
