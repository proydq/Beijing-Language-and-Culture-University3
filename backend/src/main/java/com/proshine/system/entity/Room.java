package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 房间实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_room")
public class Room {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "room_name", columnDefinition = "VARCHAR(100) COMMENT '房屋名称'")
    private String roomName;

    @Column(name = "room_area_id", columnDefinition = "VARCHAR(36) COMMENT '房间区域ID'")
    private String roomAreaId;

    @Column(name = "room_area_name", columnDefinition = "VARCHAR(100) COMMENT '房间区域名称'")
    private String roomAreaName;

    @Column(name = "room_no", columnDefinition = "VARCHAR(50) COMMENT '房间号码'")
    private String roomNo;

    @Column(name = "room_type_id", columnDefinition = "VARCHAR(36) COMMENT '房屋类型ID'")
    private String roomTypeId;

    @Column(name = "room_type_name", columnDefinition = "VARCHAR(50) COMMENT '房屋类型名称'")
    private String roomTypeName;

    @Column(name = "room_volume", columnDefinition = "INT COMMENT '容纳人数'")
    private Integer roomVolume;

    @Column(name = "room_area", columnDefinition = "DECIMAL(10,2) COMMENT '房间面积(平方米)'")
    private Double roomArea;

    @Column(name = "remark", columnDefinition = "TEXT COMMENT '备注'")
    private String remark;

    @Column(name = "create_time", columnDefinition = "BIGINT COMMENT '创建时间'")
    private Long createTime;

    @Column(name = "last_update_time", columnDefinition = "BIGINT COMMENT '最后更新时间'")
    private Long lastUpdateTime;

    @Column(name = "extend1", columnDefinition = "VARCHAR(255) COMMENT '扩展字段1-房间编码'")
    private String extend1;

    @Column(name = "extend2", columnDefinition = "VARCHAR(255) COMMENT '扩展字段2'")
    private String extend2;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否已删除(0:否 1:是)'")
    private Boolean isDeleted = false;

    @Column(name = "delete_time", columnDefinition = "BIGINT COMMENT '删除时间'")
    private Long deleteTime;

    /**
     * 房屋类型枚举
     */
    public enum RoomType {
        CLASSROOM("教室"),
        MEETING_ROOM("会议室"),
        LABORATORY("实验室"),
        OFFICE("办公室");

        private final String displayName;

        RoomType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}