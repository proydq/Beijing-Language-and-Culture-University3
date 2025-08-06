package com.proshine.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 远程开门操作响应DTO
 * 
 * @author system
 * @date 2024-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteOpenDoorResponse {

    /**
     * 操作记录ID
     */
    private String recordId;

    /**
     * 开门时间
     */
    private LocalDateTime openTime;

    /**
     * 操作是否成功
     */
    private Boolean success;
}