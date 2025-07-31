package com.proshine.system.repository;

import com.proshine.system.entity.TbAttendanceAdministrativeCourseTimeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程时间配置Repository
 *
 * @author proshine
 */
@Repository
public interface TbAttendanceAdministrativeCourseTimeConfigRepository extends JpaRepository<TbAttendanceAdministrativeCourseTimeConfig, String> {

    /**
     * 根据客户ID查询课程时间配置，按课程序号排序
     *
     * @param cstmId 客户ID
     * @return 课程时间配置列表
     */
    List<TbAttendanceAdministrativeCourseTimeConfig> findByCstmIdOrderByCourseSort(String cstmId);
}