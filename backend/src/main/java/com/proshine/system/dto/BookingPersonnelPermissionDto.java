package com.proshine.system.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约人员权限配置DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class BookingPersonnelPermissionDto {

    private String id;
    
    private String subject;
    
    private String authorizedPersonnel;
    
    private List<AuthorizedUserDto> authorizedUsers;
    
    private String bookingRooms;
    
    private List<RoomDto> roomList;
    
    private String creator;
    
    private LocalDateTime createTime;

    /**
     * 授权用户DTO
     */
    @Data
    public static class AuthorizedUserDto {
        private String id;
        private String name;
        private String jobNumber;
        private String department;
    }

    /**
     * 房间DTO
     */
    @Data
    public static class RoomDto {
        private String id;
        private String roomName;
        private String roomCode;
        private String building;
    }
}