package com.proshine.system.service;

import com.proshine.system.dto.BookingTimeConfigDTO;
import com.proshine.system.dto.request.BookingTimeValidationRequest;
import com.proshine.system.dto.response.BookingTimeValidationResponse;

/**
 * 房间预约时间服务接口
 */
public interface RoomBookingTimeService {
    
    /**
     * 获取教室预约时间配置
     * @param roomId 教室ID
     * @return 预约时间配置信息
     */
    BookingTimeConfigDTO getBookingTimeConfig(String roomId);
    
    /**
     * 验证预约时间合法性
     * @param request 验证请求
     * @return 验证结果
     */
    BookingTimeValidationResponse validateBookingTime(BookingTimeValidationRequest request);
}