package com.proshine.system.controller;

import com.proshine.system.dto.RoomWeeklyScheduleRequest;
import com.proshine.system.dto.RoomWeeklyScheduleVO;
import com.proshine.system.service.RoomScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 教室课表控制器测试类
 */
public class RoomScheduleControllerTest {

    @Mock
    private RoomScheduleService roomScheduleService;

    @InjectMocks
    private RoomScheduleController roomScheduleController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomScheduleController).build();
    }

    @Test
    public void testGetRoomWeeklyDetail() throws Exception {
        // 准备测试数据
        RoomWeeklyScheduleVO mockResponse = new RoomWeeklyScheduleVO(
                1, "第1周", "2025-01-01", "2025-01-07",
                new ArrayList<>(), new ArrayList<>()
        );

        // 模拟服务层返回
        when(roomScheduleService.getRoomWeeklyDetail(any(RoomWeeklyScheduleRequest.class)))
                .thenReturn(mockResponse);

        // 执行测试
        String requestJson = "{\"classRoomId\":\"test-room\",\"timestamp\":1640995200000}";

        mockMvc.perform(post("/api/room-schedule/week-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.week").value(1))
                .andExpect(jsonPath("$.data.weekName").value("第1周"));
    }
}