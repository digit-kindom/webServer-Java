package com.async.digitkingdom.service;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.dto.LoginDTO;

import java.util.HashMap;

public interface LoginService {
    Result<HashMap<String,String>> login(LoginDTO loginDTO);

    Result logout();
}
