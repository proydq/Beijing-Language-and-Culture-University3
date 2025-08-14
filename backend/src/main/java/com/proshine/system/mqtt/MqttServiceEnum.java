package com.proshine.system.mqtt;

/**
 * MQTT服务枚举
 */
public class MqttServiceEnum {
    
    /**
     * 会议信息服务
     */
    public static final String SERVICE_MEETING_INFO = "EnumMeetingInfos";

    /**
     * 节目控制类服务
     */
    public static final String SERVICE_PROGRAM  = "SERVICE_PROGRAM";

    /**
     * 课程更新
     */
    public static final String SERVICE_SERVICEUPDATEWEEKCOURSE = "ServiceUpdateWeekCourse";
    
    /**
     * 会议信息相关操作
     */
    public static class MeetingOperation {
        /**
         * 刷新会议列表
         */
        public static final String REFRESH_MEETING_LIST = "GET_MEETING_INFO_LIST";
        
        /**
         * 会议创建
         */
        public static final String MEETING_CREATED = "MEETING_CREATED";
        
        /**
         * 会议更新
         */
        public static final String MEETING_UPDATED = "MEETING_UPDATED";
        
        /**
         * 会议取消
         */
        public static final String MEETING_CANCELLED = "MEETING_CANCELLED";
        
        /**
         * 会议删除
         */
        public static final String MEETING_DELETED = "MEETING_DELETED";
    }

    /**
     * 节目操作类型
     */
    public static class EnumProgramOps {
        /**
         * 下发节目, 有参数
         * 参数:
         *  1. 节目ID
         */
        public static final String ISSUE_PROGRAM    = "ISSUE_PROGRAM";
        /**
         * 更新节目, 有参数
         * 参数:
         *  1. 节目ID
         */
        public static final String UPDATE_PROGRAM    = "UPDATE_PROGRAM";
        /**
         * 清空节目, 有参数
         * 参数:
         *  1. 节目ID
         */
        public static final String CLEAN_PROGRAM    = "CLEAN_PROGRAM";

        /**
         * 更新艺屏节目单
         */
        public static final String DOWNLOAD_PLAY_LIST      = "DOWNLOAD_PLAY_LIST";
        /**
         * 更新会议列表
         */
        public static final String GET_MEETING_INFO_LIST      = "GET_MEETING_INFO_LIST";

        /**
         * 更新获取门控权限人员
         */
        public static final String GET_PEOPLE_ROOM_RIGHT_LIST      = "GET_PEOPLE_ROOM_RIGHT_LIST";

        /**
         * 更新获取一键呼叫权限人员
         */
        public static final String GET_TERMINAL_SEND_MESSAGE_RIGHT = "GET_TERMINAL_SEND_MESSAGE_RIGHT";

        /**
         * 新内容管理控件
         */
        public static final String GET_TERMINAL_GET_WIDGET_CONTENT = "GET_WIDGET_CONTENT";
        /**
         * 视频列表更新
         */
        public static final String GET_VIDEO_LIST = "GET_VIDEO_LIST";

        /**
         * 文件列表更新
         */
        public static final String GET_OPERATION_DEMONSTRATION_VIDEO_LIST = "GET_OPERATION_DEMONSTRATION_VIDEO_LIST";

        public static final String GET_TERMINAL_SET_ENTRANCE_OPEN = "SET_ENTRANCE_OPEN";

        public static final String GET_TERMINAL_SET_ENTRANCE_CANCEL_OPEN = "SET_ENTRANCE_CANCEL_OPEN";

        public static final String GET_TERMINAL_LONG_RANGE_OPEN = "LONG_RANGE_OPEN";
        /**
         * 上报终端日志
         */
        public static final String GET_TERMINAL_UPLOAD_LOG= "UPLOAD_LOG";
        /**
         * 更新课表模块数据
         */
        public static final String UPDATE_COURSE_PLANNING      = "UPDATE_COURSE_PLANNING";
        /**
         * 更新课表模块数据
         */
        public static final String UPDATE_WEEK_COURSE      = "UPDATE_WEEK_COURSE";
        /**
         * 更新获取门禁管理人员
         */
        public static final String GET_PEOPLE_ENTRANCE_RIGHT = "GET_PEOPLE_ENTRANCE_RIGHT";

        /**
         * 时间轴更新
         */
        public static final String DOWNLOAD_TIMELINE = "DOWNLOAD_TIMELINE";

    }
    
    /**
     * MQTT主题前缀
     */
    public static final String TOPIC_PREFIX = "bt_client/";
    
    /**
     * 广播主题
     */
    public static final String BROADCAST_TOPIC = "broadcast";
}