package com.proshine.system.repository;

import com.proshine.system.entity.BookingCoursePlanningRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预约与排课关联关系Repository
 */
@Repository
public interface BookingCoursePlanningRelationRepository extends JpaRepository<BookingCoursePlanningRelation, String> {

    /**
     * 根据预约ID查询所有关联记录
     * @param bookingId 预约ID
     * @return 关联记录列表
     */
    List<BookingCoursePlanningRelation> findByBookingId(String bookingId);

    /**
     * 根据预约ID删除所有关联记录
     * @param bookingId 预约ID
     */
    void deleteByBookingId(String bookingId);

    /**
     * 根据排课ID查询关联记录
     * @param coursePlanningId 排课ID
     * @return 关联记录列表
     */
    List<BookingCoursePlanningRelation> findByCoursePlanningId(String coursePlanningId);
}