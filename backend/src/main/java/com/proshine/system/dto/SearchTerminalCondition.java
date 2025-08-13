package com.proshine.system.dto;

import lombok.Data;

/**
 * 终端查询条件DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class SearchTerminalCondition {

    /**
     * 客户域ID
     */
    private String cstmId;

    /**
     * 终端名称（模糊查询）
     */
    private String terminalName;

    /**
     * 终端ID
     */
    private String terminalId;

    /**
     * 在线状态：0-离线，1-开机，2-关机
     */
    private Integer onlineState;

    /**
     * 绑定的房间ID
     */
    private String roomId;

    /**
     * 房间名称（模糊查询）
     */
    private String roomName;

    /**
     * 分类ID
     */
    private String catId;

    /**
     * 主板类型
     */
    private String boardType;

    /**
     * IP地址
     */
    private String ipaddr;

    /**
     * 网络类型
     */
    private Integer networkType;

    /**
     * 应用版本
     */
    private String appVersion;

    /**
     * 固件版本
     */
    private String firmwareVersion;

    /**
     * 设备型号ID
     */
    private String deviceModelId;

    /**
     * 教室名称（模糊查询）
     */
    private String classroomName;

    /**
     * 班级名称（模糊查询）
     */
    private String className;

    /**
     * 门禁开放状态：1-常开，2-常闭
     */
    private Integer entranceOpenStatus;

    /**
     * 进出类型：1-进门，2-出门
     */
    private Integer entranceType;

    /**
     * 是否已绑定房间：true-已绑定，false-未绑定
     */
    private Boolean isBound;

    /**
     * 是否为门禁终端：true-是，false-否
     */
    private Boolean isEntranceTerminal;

    /**
     * 最小存储空间（字节）
     */
    private Long minTotalSpace;

    /**
     * 最大存储空间（字节）
     */
    private Long maxTotalSpace;

    /**
     * 最小可用空间百分比
     */
    private Double minFreeSpaceRate;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 页码（从0开始）
     */
    private Integer page = 0;

    /**
     * 每页大小
     */
    private Integer size = 20;

    /**
     * 排序字段
     */
    private String sortField = "createTime";

    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortDirection = "desc";
}