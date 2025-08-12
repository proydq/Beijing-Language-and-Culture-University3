package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.RoomWeeklyScheduleRequest;
import com.proshine.system.dto.RoomWeeklyScheduleVO;
import com.proshine.system.dto.SchoolWeekVO;

import java.util.List;
import com.proshine.system.service.RoomScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 教室课表控制器
 *
 * @author proshine
 */
@RestController
@RequestMapping("/api/room-schedule")
@CrossOrigin(origins = "*")
public class RoomScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(RoomScheduleController.class);

    @Autowired
    private RoomScheduleService roomScheduleService;

    /**
     * 获取教室周课表详情
     * 用于前端课表矩阵渲染，根据时间戳查询某教室在指定时间所属教学周的课程排课信息和节次时间配置
     *
     * @param request 查询请求参数
     * @return 教室周课表信息
     */
    @PostMapping("/week-detail")
    @PreAuthorize("hasAuthority('ROOM_SCHEDULE_VIEW') or hasAuthority('ROOM_SCHEDULE_MANAGE')")
    public ResponseEntity<RoomWeeklyScheduleVO> getRoomWeeklyDetail(
            @Valid @RequestBody RoomWeeklyScheduleRequest request) {
        
        logger.info("接收到获取教室周课表详情请求: {}", request);
        
        try {
            RoomWeeklyScheduleVO result = roomScheduleService.getRoomWeeklyDetail(request);
            
            logger.info("教室周课表详情查询成功，教室ID: {}", request.getClassRoomId());
            return ResponseEntity.success(result);
            
        } catch (Exception e) {
            logger.error("查询教室周课表详情失败", e);
            return ResponseEntity.fail("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有教学周列表
     * 用于前端周选择器展示所有可用的教学周
     *
     * @return 教学周列表
     */
    @GetMapping("/weeks")
    @PreAuthorize("hasAuthority('ROOM_SCHEDULE_VIEW') or hasAuthority('ROOM_SCHEDULE_MANAGE')")
    public ResponseEntity<List<SchoolWeekVO>> getAllSchoolWeeks() {
        
        logger.info("接收到获取教学周列表请求");
        
        try {
            List<SchoolWeekVO> result = roomScheduleService.getAllSchoolWeeks();
            
            logger.info("教学周列表查询成功，数量: {}", result.size());
            return ResponseEntity.success(result);
            
        } catch (Exception e) {
            logger.error("获取教学周列表失败", e);
            return ResponseEntity.fail("获取教学周列表失败: " + e.getMessage());
        }
    }
}