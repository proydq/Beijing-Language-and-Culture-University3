package com.proshine.system.repository;

import com.proshine.system.entity.SchoolCalendar2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 学校日历Repository
 *
 * @author proshine
 */
@Repository
public interface SchoolCalendar2Repository extends JpaRepository<SchoolCalendar2, String> {

    /**
     * 根据日期范围和客户ID查询学校日历
     *
     * @param targetDate 目标日期
     * @param targetDate2 目标日期
     * @param customerId 客户ID
     * @return 学校日历
     */
    Optional<SchoolCalendar2> findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(
            Date targetDate, Date targetDate2, String customerId);

    /**
     * 根据客户ID查询所有教学周，按学年、学期、周次排序
     *
     * @param customerId 客户ID
     * @return 教学周列表
     */
    List<SchoolCalendar2> findByCustomerIdOrderByXnDescXqAscWeekAsc(String customerId);
    
    /**
     * 根据客户域ID、学年和学期查询所有教学周，按周次排序
     *
     * @param customerId 客户ID
     * @param xn 学年
     * @param xq 学期
     * @return 教学周列表
     */
    List<SchoolCalendar2> findByCustomerIdAndXnAndXqOrderByWeek(String customerId, String xn, Integer xq);
    
    /**
     * 根据客户域ID查询所有教学周，按开始日期倒序
     *
     * @param customerId 客户ID
     * @return 教学周列表
     */
    List<SchoolCalendar2> findByCustomerIdOrderByStartDateDesc(String customerId);
}