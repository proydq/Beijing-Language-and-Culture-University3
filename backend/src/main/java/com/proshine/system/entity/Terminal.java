package com.proshine.system.entity;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 终端实体类
 */
@Entity
@Table(name = "tb_terminal",
        uniqueConstraints = {
                @UniqueConstraint(name = "cstm_ter_index", columnNames = {"cstm_id", "terminal_id"})
        })
@IdClass(Terminal.TerminalId.class)
@Data
public class Terminal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "varchar(40) comment '主键ID'")
    private String id;

    @Id
    @Column(name = "cstm_id", length = 40, nullable = false)
    private String cstmId;

    @Column(name = "terminal_id", length = 64)
    private String terminalId;

    @Column(name = "extra_id1", length = 64)
    private String extraId1;

    // 扩展字段2 绑定的房间id
    @Column(name = "extra_id2", length = 64)
    private String extraId2;

    @Column(name = "terminal_name", length = 64)
    private String terminalName;

    @Column(name = "terminal_desc", length = 255)
    private String terminalDesc;

    @Column(name = "cat_id", length = 40)
    private String catId;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "create_user", length = 64)
    private String createUser;

    @Column(name = "program_config", length = 64)
    private String programConfig;

    @Column(name = "terminal_config", length = 64)
    private String terminalConfig;

    @Column(name = "online_state")
    private Integer onlineState; // 0:离线 1:开机 2:关机

    @Column(name = "logon_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logonTime;

    @Column(name = "logoff_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logoffTime;

    @Column(name = "last_alive_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAliveTime;

    @Column(name = "board_type", length = 64)
    private String boardType;

    @Column(length = 15)
    private String ipaddr;

    @Column(name = "display_width")
    private Integer displayWidth;

    @Column(name = "display_height")
    private Integer displayHeight;

    @Column
    private Integer volume;

    @Column(name = "network_type")
    private Integer networkType;

    @Column(name = "app_version", length = 64)
    private String appVersion; // 1-2-3 ； 1表示daemon 2表示touch程序 3第三方程序

    @Column(name = "firmware_version", length = 255)
    private String firmwareVersion;

    @Column(name = "total_space")
    private Long totalSpace;

    @Column(name = "free_space")
    private Long freeSpace;

    @Column(name = "program_version", length = 255)
    private String programVersion; // 节目的版本

    @Column(name = "device_model_id", length = 40)
    private String deviceModelId;

    @Column(name = "classroom_name", length = 64)
    private String classroomName;

    @Column(name = "class_name", length = 255)
    private String className;

    @Column(name = "entrance_open_status")
    private Integer entranceOpenStatus; // 终端门禁常闭常开状态 1常开 2常闭

    @Column(name = "entrance_type")
    private Integer entranceType; // 进出类型 1进门 2出门

    /**
     * 复合主键类
     */
    @Data
    public static class TerminalId implements Serializable {
        private String id;
        private String cstmId;
    }
}