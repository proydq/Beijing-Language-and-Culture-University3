package com.proshine.common.dto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCrypt工具类，用于密码加密和验证
 */
public class BCryptUtil {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 对密码进行加密
     * 
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return passwordEncoder.encode(password);
    }
    
    /**
     * 验证密码是否匹配
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 生成指定强度的BCryptPasswordEncoder
     * 
     * @param strength 加密强度(默认为-1，使用默认强度)
     * @return PasswordEncoder实例
     */
    public static PasswordEncoder getPasswordEncoder(int strength) {
        if (strength < 4 || strength > 31) {
            return new BCryptPasswordEncoder();
        }
        return new BCryptPasswordEncoder(strength);
    }
}
