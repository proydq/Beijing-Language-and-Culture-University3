package com.proshine.system.service.impl;

import com.proshine.system.dto.RoomWeeklyScheduleRequest;
import com.proshine.system.dto.RoomWeeklyScheduleVO;
import com.proshine.system.dto.SchoolWeekVO;
import com.proshine.system.entity.CoursePlanning;
import com.proshine.system.entity.SchoolCalendar2;
import com.proshine.system.entity.TbAttendanceAdministrativeCourseTimeConfig;
import com.proshine.system.repository.CoursePlanningRepository;
import com.proshine.system.repository.SchoolCalendar2Repository;
import com.proshine.system.repository.TbAttendanceAdministrativeCourseTimeConfigRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.RoomScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 教室课表服务实现类
 *
 * @author proshine
 */
@Service
public class RoomScheduleServiceImpl implements RoomScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(RoomScheduleServiceImpl.class);

    @Autowired
    private SchoolCalendar2Repository schoolCalendar2Repository;

    @Autowired
    private CoursePlanningRepository coursePlanningRepository;

    @Autowired
    private TbAttendanceAdministrativeCourseTimeConfigRepository courseTimeConfigRepository;

    @Override
    public RoomWeeklyScheduleVO getRoomWeeklyDetail(RoomWeeklyScheduleRequest request) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            logger.info("查询教室周课表详情，教室ID: {}, 时间戳: {}, 客户ID: {}", 
                       request.getClassRoomId(), request.getTimestamp(), customerId);

            // 将时间戳转换为Date
            Date targetDate = new Date(request.getTimestamp());

            // 查询当前时间所属的教学周
            Optional<SchoolCalendar2> schoolCalendarOpt = schoolCalendar2Repository
                    .findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(
                            targetDate, targetDate, customerId);

            if (!schoolCalendarOpt.isPresent()) {
                logger.warn("未找到对应的教学周信息，日期: {}, 客户ID: {}", targetDate, customerId);
                return new RoomWeeklyScheduleVO();
            }

            SchoolCalendar2 schoolCalendar = schoolCalendarOpt.get();
            logger.info("找到教学周: {}, 开始日期: {}, 结束日期: {}", 
                       schoolCalendar.getWeek(), schoolCalendar.getStartDate(), schoolCalendar.getEndDate());

            // 查询课程时间配置
            List<TbAttendanceAdministrativeCourseTimeConfig> courseTimeConfigList = 
                    courseTimeConfigRepository.findByCstmIdOrderByCourseSort(customerId);

            // 查询该教室在该教学周内的排课信息
            Date startDate = schoolCalendar.getStartDate();
            Date endDate = schoolCalendar.getEndDate();
            
            List<CoursePlanning> coursePlanningList = coursePlanningRepository
                    .findByClassRoomIdAndCourseDateBetween(request.getClassRoomId(), startDate, endDate);

            logger.info("查询到排课信息数量: {}, 时间配置数量: {}", 
                       coursePlanningList.size(), courseTimeConfigList.size());

            // 封装返回结果
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String weekName = "第" + schoolCalendar.getWeek() + "周";
            return new RoomWeeklyScheduleVO(
                    schoolCalendar.getWeek(),
                    weekName,
                    formatter.format(schoolCalendar.getStartDate()),
                    formatter.format(schoolCalendar.getEndDate()),
                    courseTimeConfigList,
                    coursePlanningList
            );

        } catch (Exception e) {
            logger.error("查询教室周课表详情失败", e);
            throw new RuntimeException("查询教室周课表详情失败: " + e.getMessage());
        }
    }

    @Override
    public List<SchoolWeekVO> getAllSchoolWeeks() {
        try {
            String customerId = SecurityUtil.getCustomerId();
            logger.info("获取所有教学周列表，客户ID: {}", customerId);

            List<SchoolCalendar2> schoolCalendars = schoolCalendar2Repository
                    .findByCustomerIdOrderByXnDescXqAscWeekAsc(customerId);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            List<SchoolWeekVO> result = schoolCalendars.stream()
                    .map(calendar -> {
                        String weekName = "第" + calendar.getWeek() + "周";
                        return new SchoolWeekVO(
                                calendar.getId(),
                                calendar.getWeek(),
                                weekName,
                                calendar.getXn(),
                                calendar.getXq(),
                                formatter.format(calendar.getStartDate()),
                                formatter.format(calendar.getEndDate())
                        );
                    })
                    .collect(Collectors.toList());

            logger.info("获取到教学周数量: {}", result.size());
            return result;

        } catch (Exception e) {
            logger.error("获取教学周列表失败", e);
            throw new RuntimeException("获取教学周列表失败: " + e.getMessage());
        }
    }
}