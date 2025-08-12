package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.BookingTimeRuleDto;
import com.proshine.system.dto.SearchBookingTimeRuleCondition;
import com.proshine.system.service.BookingTimeRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约时间规则控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/booking-time-rules")
@Slf4j
public class BookingTimeRuleController {

    @Autowired
    private BookingTimeRuleService bookingTimeRuleService;

    /**
     * 获取预约时间规则列表
     * 
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param ruleName 规则名称（可选）
     * @return 分页结果
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('BOOKING_TIME_RULE_VIEW') or hasAuthority('BOOKING_TIME_RULE_MANAGE')")
    public ResponseEntity<ResponsePageDataEntity<BookingTimeRuleDto>> getBookingTimeRuleList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String ruleName) {
        try {
            SearchBookingTimeRuleCondition condition = new SearchBookingTimeRuleCondition();
            condition.setPageNumber(pageNum);
            condition.setPageSize(pageSize);
            condition.setRuleName(ruleName);
            
            ResponsePageDataEntity<BookingTimeRuleDto> result = bookingTimeRuleService.getBookingTimeRuleList(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取预约时间规则列表失败", e);
            return ResponseEntity.fail("获取预约时间规则列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取预约时间规则详情
     * 
     * @param id 规则ID
     * @return 规则详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOKING_TIME_RULE_VIEW') or hasAuthority('BOOKING_TIME_RULE_MANAGE')")
    public ResponseEntity<BookingTimeRuleDto> getBookingTimeRuleById(@PathVariable String id) {
        try {
            BookingTimeRuleDto result = bookingTimeRuleService.getBookingTimeRuleById(id);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取预约时间规则详情失败, id: {}", id, e);
            return ResponseEntity.fail("获取预约时间规则详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增预约时间规则
     * 
     * @param dto 规则信息
     * @return 创建的规则
     */
    @PostMapping
    @PreAuthorize("hasAuthority('BOOKING_TIME_RULE_ADD') or hasAuthority('BOOKING_TIME_RULE_MANAGE')")
    public ResponseEntity<BookingTimeRuleDto> createBookingTimeRule(@RequestBody BookingTimeRuleDto dto) {
        try {
            BookingTimeRuleDto result = bookingTimeRuleService.createBookingTimeRule(dto);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("新增预约时间规则失败", e);
            return ResponseEntity.fail("新增预约时间规则失败: " + e.getMessage());
        }
    }

    /**
     * 编辑预约时间规则
     * 
     * @param id 规则ID
     * @param dto 规则信息
     * @return 更新后的规则
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOKING_TIME_RULE_EDIT') or hasAuthority('BOOKING_TIME_RULE_MANAGE')")
    public ResponseEntity<BookingTimeRuleDto> updateBookingTimeRule(
            @PathVariable String id, 
            @RequestBody BookingTimeRuleDto dto) {
        try {
            BookingTimeRuleDto result = bookingTimeRuleService.updateBookingTimeRule(id, dto);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("编辑预约时间规则失败, id: {}", id, e);
            return ResponseEntity.fail("编辑预约时间规则失败: " + e.getMessage());
        }
    }

    /**
     * 删除预约时间规则
     * 
     * @param id 规则ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOKING_TIME_RULE_DELETE') or hasAuthority('BOOKING_TIME_RULE_MANAGE')")
    public ResponseEntity<String> deleteBookingTimeRule(@PathVariable String id) {
        try {
            bookingTimeRuleService.deleteBookingTimeRule(id);
            return ResponseEntity.success("删除成功");
        } catch (Exception e) {
            log.error("删除预约时间规则失败, id: {}", id, e);
            return ResponseEntity.fail("删除预约时间规则失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取适用的预约时间规则
     * 
     * @param userId 用户ID
     * @return 适用的规则列表
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BookingTimeRuleDto>> getBookingTimeRulesByUserId(@PathVariable String userId) {
        try {
            List<BookingTimeRuleDto> result = bookingTimeRuleService.getBookingTimeRulesByUserId(userId);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取用户适用的预约时间规则失败, userId: {}", userId, e);
            return ResponseEntity.fail("获取用户适用的预约时间规则失败: " + e.getMessage());
        }
    }
}