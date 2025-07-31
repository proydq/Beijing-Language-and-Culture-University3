package com.proshine.system.dto;

import javax.validation.constraints.NotNull;

/**
 * 教室周课表查询请求DTO
 *
 * @author proshine
 */
public class RoomWeeklyScheduleRequest {

    /**
     * 教室ID
     */
    @NotNull(message = "教室ID不能为空")
    private String classRoomId;

    /**
     * 时间戳
     */
    @NotNull(message = "时间戳不能为空")
    private Long timestamp;

    public RoomWeeklyScheduleRequest() {
    }

    public RoomWeeklyScheduleRequest(String classRoomId, Long timestamp) {
        this.classRoomId = classRoomId;
        this.timestamp = timestamp;
    }

    public String getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(String classRoomId) {
        this.classRoomId = classRoomId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RoomWeeklyScheduleRequest{" +
                "classRoomId='" + classRoomId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}