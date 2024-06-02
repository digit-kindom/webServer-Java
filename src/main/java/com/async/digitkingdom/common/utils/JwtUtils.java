package com.async.digitkingdom.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    /**
     * 生成JWT
     * @param secretKey 密钥
     * @param ttlMillis jwt有效时间
     * @param claims    信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String,Object> claims){
        //指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //设置过期时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);
        //设置jwt的body部分
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                //设置签名算法和签名密钥
                .signWith(signatureAlgorithm,secretKey.getBytes(StandardCharsets.UTF_8))
                //设置到期时间
                .setExpiration(exp);

        return builder.compact();
    }

    public static Claims parseJWT(String secretKey, String token){
        Claims claims = Jwts.parser()
                //设置签名的密钥
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                //设置需要被解析的jwt
                .parseClaimsJws(token).getBody();

        return claims;
    }
}
