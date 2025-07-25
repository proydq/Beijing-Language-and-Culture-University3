package com.proshine.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 */
@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKey}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private Long expiration;
    
    /**
     * 从令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * 从令牌中获取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 从令牌中获取指定声明
     *
     * @param token JWT令牌
     * @param claimsResolver 声明解析器
     * @param <T> 返回类型
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从令牌中获取所有声明
     *
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    
    /**
     * 检查令牌是否过期
     *
     * @param token JWT令牌
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 为用户生成令牌
     *
     * @param username 用户名
     * @param userId 用户ID
     * @param customerId 客户域ID
     * @return JWT令牌
     */
    public String generateToken(String username, String userId, String customerId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("customerId", customerId);
        return createToken(claims, username);
    }
    
    /**
     * 创建令牌
     *
     * @param claims 声明
     * @param subject 主题（用户名）
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    /**
     * 验证令牌
     *
     * @param token JWT令牌
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("JWT令牌验证失败：", e);
            return false;
        }
    }
    
    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("userId", String.class));
    }
    
    /**
     * 从令牌中获取客户域ID
     *
     * @param token JWT令牌
     * @return 客户域ID
     */
    public String getCustomerIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("customerId", String.class));
    }
    
    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            claims.setIssuedAt(new Date());
            claims.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000));
            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
        } catch (Exception e) {
            log.error("JWT令牌刷新失败：", e);
            return null;
        }
    }
}