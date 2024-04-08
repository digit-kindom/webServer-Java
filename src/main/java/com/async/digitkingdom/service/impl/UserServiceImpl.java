package com.async.digitkingdom.service.impl;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.common.exception.BadRequestException;
import com.async.digitkingdom.entity.User;
import com.async.digitkingdom.entity.dto.RegisterUserDTO;
import com.async.digitkingdom.entity.vo.UserVO;
import com.async.digitkingdom.mapper.UserMapper;
import com.async.digitkingdom.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     * @param registerUserDTO
     * @return
     */
    @Override
    public Result register(RegisterUserDTO registerUserDTO) {
        registerUserDTO.setDateJoined(LocalDate.now());
        registerUserDTO.setLastLogin(LocalDate.now());

        // TODO 待优化
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
        User user = new User();
        BeanUtils.copyProperties(registerUserDTO,user);
        user.setIsStaff(false);
        user.setIsSuperuser(false);
        userMapper.insert(user);

        return Result.ok("注册成功！");
    }

    /**
     * 分页查询用户
     * @return
     */
    @Override
    public Result<PageInfo<UserVO>> getAllUser(Integer pageNumber,Integer pageSize) {

        PageHelper.startPage(pageNumber, pageSize);

        List<UserVO> list = userMapper.getAllUser();

        PageInfo<UserVO> pageInfo = new PageInfo<>(list);

        return Result.ok(pageInfo);
    }

    @Override
    public Result updateUser(User user) {
        if(user.getId() == null){
            throw new BadRequestException("传入的用户id不得为空！");
        }

        String password = user.getPassword();
        if(password != null){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encode = encoder.encode(password);
            user.setPassword(encode);
        }

        userMapper.update(user);
        return Result.ok("修改成功！");
    }

    @Override
    public Result deleteById(Integer id) {
        if(id == null){
            throw new BadRequestException("传入的用户id不得为空！");
        }

        userMapper.deleteById(id);
        return Result.ok();
    }

    /**
     * 管理员添加用户
     * @param user
     * @return
     */
    @Override
    public Result addUser(User user) {
        if(user.getUsername() == null){
            throw new BadRequestException("传入的用户名为空！");
        }
        if(userMapper.getUserByName(user.getUsername()) != null){
            throw new BadRequestException("用户名已存在！");
        }

        if(user.getEmail() == null){
            throw new BadRequestException("传入的邮箱为空！");
        }
        if(user.getFirstName() == null){
            throw new BadRequestException("传入的名字为空！");
        }
        if(user.getLastName() == null){
            throw new BadRequestException("传入的姓氏为空！");
        }
        String password = user.getPassword();
        if(password == null){
            throw new BadRequestException("传入的密码为空！");
        }
        userMapper.insert(user);
        return Result.ok("添加成功！");
    }
}
