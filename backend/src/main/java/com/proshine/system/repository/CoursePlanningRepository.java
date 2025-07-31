package com.proshine.system.repository;

import com.proshine.system.entity.CoursePlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 课程排课Repository
 *
 * @author proshine
 */
@Repository
public interface CoursePlanningRepository extends JpaRepository<CoursePlanning, String> {

    /**
     * 根据教室ID和课程日期范围查询排课信息
     *
     * @param classRoomId 教室ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排课信息列表
     */
    List<CoursePlanning> findByClassRoomIdAndCourseDateBetween(String classRoomId, Date startDate, Date endDate);
}