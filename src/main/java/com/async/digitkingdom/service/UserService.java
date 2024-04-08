package com.async.digitkingdom.service;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.dto.RegisterUserDTO;
import com.async.digitkingdom.entity.vo.UserVO;
import com.github.pagehelper.PageInfo;

public interface UserService {
    Result register(RegisterUserDTO registerUserDTO);

    Result<PageInfo<UserVO>> getAllUser(Integer pageNumber,Integer pageSize);
}
