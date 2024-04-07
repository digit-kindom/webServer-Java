package com.async.digitkingdom.service.impl;

import com.async.digitkingdom.common.JwtUtils;
import com.async.digitkingdom.common.RedisCache;
import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.common.exception.ForbiddenException;
import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.entity.dto.LoginDTO;
import com.async.digitkingdom.properties.JwtProperties;
import com.async.digitkingdom.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Result<HashMap<String,String>> login(LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),loginDTO.getPassword());
        Authentication authentication =authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authentication)){
            throw new ForbiddenException("用户名或密码错误！");
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String username = loginUser.getUser().getUsername();
        Map<String,Object> claim = new HashMap<>();
        claim.put("username",username);
        String jwt = JwtUtils.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claim);
        // 将 authenticate 存入redis
        redisCache.setCacheObject("login:" + username, loginUser);
        // 把token传回前端
        HashMap<String,String> map = new HashMap<>();
        map.put("token", jwt);
        return Result.ok(map);
    }

    @Override
    public Result logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser =(LoginUser) authentication.getPrincipal();
        String username = loginUser.getUser().getUsername();
        redisCache.deleteObject("login:" + username);
        return Result.ok("退出成功！");
    }
}
