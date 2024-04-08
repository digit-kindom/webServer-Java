package com.async.digitkingdom.service;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.dto.RegisterUserDTO;

public interface UserService {
    Result register(RegisterUserDTO registerUserDTO);
}
