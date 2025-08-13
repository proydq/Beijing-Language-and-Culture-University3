package com.proshine.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 终端设备实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_terminal")
public class Terminal {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "varchar(40) comment '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "varchar(40) not null comment '客户域ID'")
    private String cstmId;

    @Column(name = "terminal_id", columnDefinition = "varchar(64) comment '终端ID'")
    private String terminalId;

    @Column(name = "extra_id1", columnDefinition = "varchar(64) comment '扩展ID1'")
    private String extraId1;

    @Column(name = "extra_id2", columnDefinition = "varchar(64) comment '扩展ID2-绑定房间ID'")
    private String extraId2; // 绑定tb_room表的id

    @Column(name = "terminal_name", columnDefinition = "varchar(64) comment '终端名称'")
    private String terminalName;

    @Column(name = "terminal_desc", columnDefinition = "varchar(255) comment '终端描述'")
    private String terminalDesc;

    @Column(name = "cat_id", columnDefinition = "varchar(40) comment '分类ID'")
    private String catId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time", columnDefinition = "datetime comment '创建时间'")
    private Date createTime;

    @Column(name = "create_user", columnDefinition = "varchar(64) comment '创建用户'")
    private String createUser;

    @Column(name = "program_config", columnDefinition = "varchar(64) comment '程序配置'")
    private String programConfig;

    @Column(name = "terminal_config", columnDefinition = "varchar(64) comment '终端配置'")
    private String terminalConfig;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "online_state", columnDefinition = "int comment '在线状态：0-离线，1-开机，2-关机'")
    private OnlineState onlineState = OnlineState.OFFLINE;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "logon_time", columnDefinition = "datetime comment '登录时间'")
    private Date logonTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "logoff_time", columnDefinition = "datetime comment '登出时间'")
    private Date logoffTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "last_alive_time", columnDefinition = "datetime comment '最后存活时间'")
    private Date lastAliveTime;

    @Column(name = "board_type", columnDefinition = "varchar(64) comment '主板类型'")
    private String boardType;

    @Column(name = "ipaddr", columnDefinition = "varchar(15) comment 'IP地址'")
    private String ipaddr;

    @Column(name = "display_width", columnDefinition = "int comment '显示宽度'")
    private Integer displayWidth;

    @Column(name = "display_height", columnDefinition = "int comment '显示高度'")
    private Integer displayHeight;

    @Column(name = "volume", columnDefinition = "int comment '音量'")
    private Integer volume;

    @Column(name = "network_type", columnDefinition = "int comment '网络类型'")
    private Integer networkType;

    @Column(name = "app_version", columnDefinition = "varchar(64) comment '应用版本：1-2-3，1表示daemon 2表示touch程序 3第三方程序'")
    private String appVersion;

    @Column(name = "firmware_version", columnDefinition = "varchar(255) comment '固件版本'")
    private String firmwareVersion;

    @Column(name = "total_space", columnDefinition = "bigint comment '总存储空间'")
    private Long totalSpace;

    @Column(name = "free_space", columnDefinition = "bigint comment '可用存储空间'")
    private Long freeSpace;

    @Column(name = "program_version", columnDefinition = "varchar(255) comment '节目的版本'")
    private String programVersion;

    @Column(name = "device_model_id", columnDefinition = "varchar(40) comment '设备型号ID'")
    private String deviceModelId;

    @Column(name = "classroom_name", columnDefinition = "varchar(64) comment '教室名称'")
    private String classroomName;

    @Column(name = "class_name", columnDefinition = "varchar(255) comment '班级名称'")
    private String className;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "entrance_open_status", columnDefinition = "int(1) comment '终端门禁常闭常开状态：1-常开，2-常闭'")
    private EntranceOpenStatus entranceOpenStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "entrance_type", columnDefinition = "int(1) comment '进出类型：1-进门，2-出门'")
    private EntranceType entranceType;

    /**
     * 在线状态枚举
     */
    public enum OnlineState {
        OFFLINE(0, "离线"),
        ONLINE(1, "开机"),
        SHUTDOWN(2, "关机");

        private final int code;
        private final String description;

        OnlineState(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static OnlineState fromCode(int code) {
            for (OnlineState state : values()) {
                if (state.code == code) {
                    return state;
                }
            }
            return OFFLINE;
        }
    }

    /**
     * 门禁开放状态枚举
     */
    public enum EntranceOpenStatus {
        UNKNOWN(0, "未知"),
        ALWAYS_OPEN(1, "常开"),
        ALWAYS_CLOSED(2, "常闭");

        private final int code;
        private final String description;

        EntranceOpenStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static EntranceOpenStatus fromCode(int code) {
            for (EntranceOpenStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * 进出类型枚举
     */
    public enum EntranceType {
        UNKNOWN(0, "未知"),
        ENTRANCE(1, "进门"),
        EXIT(2, "出门");

        private final int code;
        private final String description;

        EntranceType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static EntranceType fromCode(int code) {
            for (EntranceType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return UNKNOWN;
        }
    }

    // 便捷方法：获取绑定的房间ID
    public String getRoomId() {
        return this.extraId2;
    }

    // 便捷方法：设置绑定的房间ID
    public void setRoomId(String roomId) {
        this.extraId2 = roomId;
    }

    // 便捷方法：检查是否在线
    public boolean isOnline() {
        return OnlineState.ONLINE.equals(this.onlineState);
    }

    // 便捷方法：检查是否离线
    public boolean isOffline() {
        return OnlineState.OFFLINE.equals(this.onlineState);
    }

    // 便捷方法：计算存储使用率
    public double getStorageUsageRate() {
        if (totalSpace == null || totalSpace <= 0) {
            return 0.0;
        }
        long usedSpace = freeSpace != null ? totalSpace - freeSpace : totalSpace;
        return (double) usedSpace / totalSpace * 100;
    }

    // 便捷方法：获取可用存储空间百分比
    public double getFreeSpaceRate() {
        if (totalSpace == null || totalSpace <= 0) {
            return 0.0;
        }
        long actualFreeSpace = freeSpace != null ? freeSpace : 0;
        return (double) actualFreeSpace / totalSpace * 100;
    }

    // 便捷方法：格式化存储空间大小
    public String getFormattedTotalSpace() {
        return formatBytes(totalSpace);
    }

    public String getFormattedFreeSpace() {
        return formatBytes(freeSpace);
    }

    private String formatBytes(Long bytes) {
        if (bytes == null || bytes <= 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        digitGroups = Math.min(digitGroups, units.length - 1);
        
        double size = bytes / Math.pow(1024, digitGroups);
        return String.format("%.1f %s", size, units[digitGroups]);
    }

    // 便捷方法：检查是否为门禁终端
    public boolean isEntranceTerminal() {
        return entranceType != null && !EntranceType.UNKNOWN.equals(entranceType);
    }

    // 便捷方法：检查门禁是否常开
    public boolean isEntranceAlwaysOpen() {
        return EntranceOpenStatus.ALWAYS_OPEN.equals(entranceOpenStatus);
    }
}