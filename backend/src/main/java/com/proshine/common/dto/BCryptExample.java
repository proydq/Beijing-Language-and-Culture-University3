package com.proshine.common.dto;
public class BCryptExample {
    public static void main(String[] args) {
        // 加密密码
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$mvue83gPcqnT39Jfcyn2eOjh3QdoZ8yTtC3YRJ/K8ipKXfCG7ljpq";
        System.out.println("加密后的密码: " + encodedPassword);

        // 验证密码
        boolean isMatch = BCryptUtil.matches(rawPassword, encodedPassword);
        System.out.println("密码匹配结果: " + isMatch);

        // 验证错误密码
        boolean isNotMatch = BCryptUtil.matches("wrongPassword", encodedPassword);
        System.out.println("错误密码匹配结果: " + isNotMatch);
    }
}
