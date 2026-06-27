package com.clou.common.util;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // 过期时间：24小时（单位：毫秒）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    // 直接定义密钥（按需求使用，无需从配置文件读取）
    // 注意：HS256算法需要至少256位(32字节)的密钥，这里确保密钥长度足够
    private static final String SECRET_KEY = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz";

    // 获取签名密钥
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * 生成token（包含id和username）
     * @param id 用户ID
     * @param username 用户名
     * @return 生成的token字符串
     */
    public static String generateToken(Long id, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析token，返回包含id和username的Map
     * @param token 待解析的token
     * @return 包含id(Long)和username(String)的Map
     */
    public static Map<String, Object> parseToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Map<String, Object> result = new HashMap<>();
        result.put("id", claims.get("id", Long.class));
        result.put("username", claims.get("username", String.class));
        return result;
    }
}