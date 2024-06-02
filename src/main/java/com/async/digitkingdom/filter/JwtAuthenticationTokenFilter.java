package com.async.digitkingdom.filter;

import com.async.digitkingdom.common.utils.JwtUtils;
import com.async.digitkingdom.common.utils.RedisCache;
import com.async.digitkingdom.common.exception.ForbiddenException;
import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.entity.User;
import com.async.digitkingdom.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisCache redisCache;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(!StringUtils.hasText(token)){
            filterChain.doFilter(request,response);
            return;
        }

        // 解析token
        String username;
        try {
            Claims claims = JwtUtils.parseJWT(jwtProperties.getAdminSecretKey(),token);
            username = claims.get("username",String.class);
        }catch (Exception e){
            resolver.resolveException(request,response,null,new ForbiddenException("token非法"));
            return;
        }

        //从redis获取信息
        String redisKey = "login:" + username;
        LinkedHashMap map =redisCache.getCacheObject(redisKey);
        if(Objects.isNull(map)){
            resolver.resolveException(request,response,null,new ForbiddenException("用户未登录！"));
        }
        LoginUser loginUser = new LoginUser();
        LinkedHashMap userMap = (LinkedHashMap) map.get("user");
        User user = new User();
        user.setId((Integer) userMap.get("id"));
        user.setPassword((String) userMap.get("password"));
        user.setUsername((String) userMap.get("username"));
        user.setFirstName((String) userMap.get("firstName"));
        user.setLastName((String) userMap.get("lastName"));
        user.setEmail((String) userMap.get("email"));
        user.setIsSuperuser((Boolean) userMap.get("isSuperuser"));
        user.setIsStaff((Boolean) userMap.get("isStaff"));
        user.setIsActive((Boolean) userMap.get("isActive"));
        ArrayList dateJoinedList = (ArrayList) userMap.get("dateJoined");
        ArrayList lastLoginList = (ArrayList) userMap.get("lastLogin");
        LocalDate today = LocalDate.now();
        LocalDate dateJoined = today.withYear((Integer) dateJoinedList.get(0)).withMonth((Integer) dateJoinedList.get(1)).withDayOfMonth((Integer) dateJoinedList.get(2));
        LocalDate lastLogin = today.withYear((Integer) lastLoginList.get(0)).withMonth((Integer) lastLoginList.get(1)).withDayOfMonth((Integer) lastLoginList.get(2));
        user.setDateJoined(dateJoined);
        user.setLastLogin(lastLogin);
        loginUser.setUser(user);
        List<String> permissions = (ArrayList) map.get("permissions");
        loginUser.setPermissions(permissions);
        loginUser.setAuthorities(permissions.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));

        //存入SecurityContextHolder
        // 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        try {
            filterChain.doFilter(request, response);
        }catch (Exception e){
            resolver.resolveException(request,response,null,e);
        }
    }
}
