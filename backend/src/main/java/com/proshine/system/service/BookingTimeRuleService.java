package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.BookingTimeRuleDto;
import com.proshine.system.dto.SearchBookingTimeRuleCondition;

import java.util.List;

/**
 * 预约时间规则Service接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface BookingTimeRuleService {

    /**
     * 分页查询预约时间规则列表
     * 
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<BookingTimeRuleDto> getBookingTimeRuleList(SearchBookingTimeRuleCondition condition);

    /**
     * 根据ID查询预约时间规则详情
     * 
     * @param id 规则ID
     * @return 规则详情
     */
    BookingTimeRuleDto getBookingTimeRuleById(String id);

    /**
     * 新增预约时间规则
     * 
     * @param dto 规则信息
     * @return 创建的规则
     */
    BookingTimeRuleDto createBookingTimeRule(BookingTimeRuleDto dto);

    /**
     * 更新预约时间规则
     * 
     * @param id 规则ID
     * @param dto 规则信息
     * @return 更新后的规则
     */
    BookingTimeRuleDto updateBookingTimeRule(String id, BookingTimeRuleDto dto);

    /**
     * 删除预约时间规则
     * 
     * @param id 规则ID
     */
    void deleteBookingTimeRule(String id);

    /**
     * 根据用户ID查询适用的预约时间规则
     * 
     * @param userId 用户ID
     * @return 适用的规则列表
     */
    List<BookingTimeRuleDto> getBookingTimeRulesByUserId(String userId);
}