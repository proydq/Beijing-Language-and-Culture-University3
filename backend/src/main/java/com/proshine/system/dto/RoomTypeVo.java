package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 房间类型VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeVo {

    /**
     * 类型ID
     */
    private String id;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 创建预定义房间类型
     */
    public static RoomTypeVo of(String id, String typeName) {
        return new RoomTypeVo(id, typeName);
    }
}