package com.async.digitkingdom.controller;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.User;
import com.async.digitkingdom.entity.dto.RegisterUserDTO;
import com.async.digitkingdom.entity.vo.UserVO;
import com.async.digitkingdom.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
//@PreAuthorize("hasAnyAuthority('Superuser')")
public class AdminController {
    @Autowired
    private UserService userService;

    /**
     * 分页查询用户
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping("/user/list")
    public Result<PageInfo<UserVO>> getAllUser(Integer pageNumber,Integer pageSize){
        return userService.getAllUser(pageNumber,pageSize);
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/user/list")
    public Result updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    /**
     * 删除用户信息
     */
    @DeleteMapping("/user/list/{id}")
    public Result deleteUser(@PathVariable("id") Integer id){
        return userService.deleteById(id);
    }

    /**
     * 注册新用户
     * @param user
     * @return
     */
    @PostMapping("/user/list")
    public Result addUser(User user){
        return userService.addUser(user);
    }
}
