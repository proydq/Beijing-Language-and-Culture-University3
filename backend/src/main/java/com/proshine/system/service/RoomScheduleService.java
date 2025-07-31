package com.proshine.system.service;

import com.proshine.system.dto.RoomWeeklyScheduleRequest;
import com.proshine.system.dto.RoomWeeklyScheduleVO;
import com.proshine.system.dto.SchoolWeekVO;

import java.util.List;

/**
 * 教室课表服务接口
 *
 * @author proshine
 */
public interface RoomScheduleService {

    /**
     * 获取教室周课表详情
     * 根据时间戳计算当前教学周，查询该房间在该教学周内的排课信息与节次配置，封装 RoomWeeklyScheduleVO 返回
     *
     * @param request 查询请求
     * @return 教室周课表信息
     */
    RoomWeeklyScheduleVO getRoomWeeklyDetail(RoomWeeklyScheduleRequest request);

    /**
     * 获取所有教学周列表
     * 查询当前客户的所有教学周信息，用于前端周选择器
     *
     * @return 教学周列表
     */
    List<SchoolWeekVO> getAllSchoolWeeks();
}