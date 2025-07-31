package com.proshine.system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 教室课表控制器集成测试
 * 验证新增的教学周接口功能
 */
@SpringBootTest
@ActiveProfiles("test")
public class RoomScheduleControllerIntegrationTest {

    @Test
    public void contextLoads() {
        // 验证Spring上下文能够正常加载
        // 确保新增的RoomScheduleController及其依赖能够正常注入
    }

    // 注意：实际的HTTP测试需要配置测试数据库和认证机制
    // 这里只是验证基本的Bean加载和依赖注入
}