package com.proshine.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 终端设备DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class TerminalDto {

    private String id;

    @NotBlank(message = "客户域ID不能为空")
    private String cstmId;

    @NotBlank(message = "终端ID不能为空")
    @Size(max = 64, message = "终端ID长度不能超过64个字符")
    private String terminalId;

    @Size(max = 64, message = "扩展ID1长度不能超过64个字符")
    private String extraId1;

    @Size(max = 64, message = "绑定房间ID长度不能超过64个字符")
    private String extraId2; // 绑定房间ID

    @NotBlank(message = "终端名称不能为空")
    @Size(max = 64, message = "终端名称长度不能超过64个字符")
    private String terminalName;

    @Size(max = 255, message = "终端描述长度不能超过255个字符")
    private String terminalDesc;

    @Size(max = 40, message = "分类ID长度不能超过40个字符")
    private String catId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Size(max = 64, message = "创建用户长度不能超过64个字符")
    private String createUser;

    @Size(max = 64, message = "程序配置长度不能超过64个字符")
    private String programConfig;

    @Size(max = 64, message = "终端配置长度不能超过64个字符")
    private String terminalConfig;

    private Integer onlineState; // 0:离线 1:开机 2:关机

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date logonTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date logoffTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastAliveTime;

    @Size(max = 64, message = "主板类型长度不能超过64个字符")
    private String boardType;

    @Size(max = 15, message = "IP地址长度不能超过15个字符")
    private String ipaddr;

    private Integer displayWidth;

    private Integer displayHeight;

    private Integer volume;

    private Integer networkType;

    @Size(max = 64, message = "应用版本长度不能超过64个字符")
    private String appVersion;

    @Size(max = 255, message = "固件版本长度不能超过255个字符")
    private String firmwareVersion;

    private Long totalSpace;

    private Long freeSpace;

    @Size(max = 255, message = "节目版本长度不能超过255个字符")
    private String programVersion;

    @Size(max = 40, message = "设备型号ID长度不能超过40个字符")
    private String deviceModelId;

    @Size(max = 64, message = "教室名称长度不能超过64个字符")
    private String classroomName;

    @Size(max = 255, message = "班级名称长度不能超过255个字符")
    private String className;

    private Integer entranceOpenStatus; // 1:常开 2:常闭

    private Integer entranceType; // 1:进门 2:出门

    // 扩展字段：房间名称（用于显示）
    private String roomName;
    
    // 扩展字段：在线状态描述
    private String onlineStateDesc;
    
    // 扩展字段：是否在线
    private Boolean isOnline;

    // 扩展字段：门禁开放状态描述
    private String entranceOpenStatusDesc;

    // 扩展字段：进出类型描述
    private String entranceTypeDesc;

    // 扩展字段：存储使用率
    private Double storageUsageRate;

    // 扩展字段：可用存储空间百分比
    private Double freeSpaceRate;

    // 扩展字段：格式化的总存储空间
    private String formattedTotalSpace;

    // 扩展字段：格式化的可用存储空间
    private String formattedFreeSpace;

    // 扩展字段：是否为门禁终端
    private Boolean isEntranceTerminal;

    // 扩展字段：门禁是否常开
    private Boolean isEntranceAlwaysOpen;
    
    // 便捷方法：获取绑定的房间ID
    public String getRoomId() {
        return this.extraId2;
    }

    // 便捷方法：设置绑定的房间ID
    public void setRoomId(String roomId) {
        this.extraId2 = roomId;
    }
}