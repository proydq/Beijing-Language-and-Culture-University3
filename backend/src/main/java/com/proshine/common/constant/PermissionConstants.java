package com.proshine.common.constant;

/**
 * 权限常量定义
 * 定义系统中所有的权限编码常量，方便统一管理和使用
 */
public class PermissionConstants {
    
    /**
     * 用户管理权限
     */
    public static final class User {
        public static final String MANAGE = "USER_MANAGE";        // 用户管理总权限
        public static final String SEARCH = "USER_SEARCH";        // 用户查询
        public static final String VIEW = "USER_VIEW";            // 用户查看
        public static final String ADD = "USER_ADD";              // 用户新增
        public static final String EDIT = "USER_EDIT";            // 用户编辑
        public static final String DELETE = "USER_DELETE";        // 用户删除
        public static final String RESET_PWD = "USER_RESET_PWD";  // 重置密码
        public static final String ASSIGN_ROLE = "USER_ASSIGN_ROLE"; // 分配角色
        public static final String EXPORT = "USER_EXPORT";        // 用户导出
        public static final String IMPORT = "USER_IMPORT";        // 用户导入
    }
    
    /**
     * 角色管理权限
     */
    public static final class Role {
        public static final String MANAGE = "ROLE_MANAGE";        // 角色管理总权限
        public static final String SEARCH = "ROLE_SEARCH";        // 角色查询
        public static final String ADD = "ROLE_ADD";              // 角色新增
        public static final String EDIT = "ROLE_EDIT";            // 角色编辑
        public static final String DELETE = "ROLE_DELETE";        // 角色删除
        public static final String ASSIGN_PERM = "ROLE_ASSIGN_PERM"; // 分配权限
    }
    
    /**
     * 权限管理权限
     */
    public static final class Permission {
        public static final String MANAGE = "PERMISSION_MANAGE";   // 权限管理总权限
        public static final String SEARCH = "PERMISSION_SEARCH";   // 权限查询
        public static final String ADD = "PERMISSION_ADD";         // 权限新增
        public static final String EDIT = "PERMISSION_EDIT";       // 权限编辑
        public static final String DELETE = "PERMISSION_DELETE";   // 权限删除
    }
    
    /**
     * 教室管理权限
     */
    public static final class Room {
        public static final String MANAGE = "ROOM_MANAGE";        // 教室管理总权限
        public static final String SEARCH = "ROOM_SEARCH";        // 教室查询
        public static final String VIEW = "ROOM_VIEW";            // 教室查看
        public static final String ADD = "ROOM_ADD";              // 教室新增
        public static final String EDIT = "ROOM_EDIT";            // 教室编辑
        public static final String DELETE = "ROOM_DELETE";        // 教室删除
        public static final String BOOK = "ROOM_BOOK";            // 教室预订
        public static final String CANCEL_BOOK = "ROOM_CANCEL_BOOK"; // 取消预订
        public static final String APPROVE = "ROOM_APPROVE";      // 预订审批
    }
    
    /**
     * 预订管理权限
     */
    public static final class Booking {
        public static final String MANAGE = "BOOKING_MANAGE";     // 预订管理总权限
        public static final String SEARCH = "BOOKING_SEARCH";     // 预订查询
        public static final String VIEW = "BOOKING_VIEW";         // 预订查看
        public static final String CREATE = "BOOKING_CREATE";     // 创建预订
        public static final String EDIT = "BOOKING_EDIT";         // 编辑预订
        public static final String CANCEL = "BOOKING_CANCEL";     // 取消预订
        public static final String APPROVE = "BOOKING_APPROVE";   // 审批预订
        public static final String REJECT = "BOOKING_REJECT";     // 拒绝预订
    }
    
    /**
     * 系统设置权限
     */
    public static final class System {
        public static final String MANAGE = "SYSTEM_MANAGE";      // 系统管理总权限
        public static final String CONFIG = "SYSTEM_CONFIG";      // 系统配置
        public static final String LOG_VIEW = "SYSTEM_LOG_VIEW";  // 日志查看
        public static final String BACKUP = "SYSTEM_BACKUP";      // 系统备份
    }
    
    /**
     * 报表统计权限
     */
    public static final class Report {
        public static final String VIEW = "REPORT_VIEW";          // 报表查看
        public static final String EXPORT = "REPORT_EXPORT";      // 报表导出
    }
    
    /**
     * 文件管理权限
     */
    public static final class File {
        public static final String MANAGE = "FILE_MANAGE";        // 文件管理总权限
        public static final String UPLOAD = "FILE_UPLOAD";        // 文件上传
        public static final String VIEW = "FILE_VIEW";            // 文件查看
    }
    
    /**
     * 组织机构权限
     */
    public static final class Organization {
        public static final String MANAGE = "ORG_MANAGE";         // 组织机构管理
        public static final String VIEW = "ORG_VIEW";             // 组织机构查看
        public static final String ADD = "ORG_ADD";               // 组织机构新增
        public static final String EDIT = "ORG_EDIT";             // 组织机构编辑
        public static final String DELETE = "ORG_DELETE";         // 组织机构删除
    }
    
    /**
     * 职位管理权限
     */
    public static final class Position {
        public static final String MANAGE = "POSITION_MANAGE";    // 职位管理总权限
        public static final String VIEW = "POSITION_VIEW";        // 职位查看
        public static final String ADD = "POSITION_ADD";          // 职位新增
        public static final String EDIT = "POSITION_EDIT";        // 职位编辑
        public static final String DELETE = "POSITION_DELETE";    // 职位删除
    }
    
    /**
     * 职称管理权限
     */
    public static final class Title {
        public static final String MANAGE = "TITLE_MANAGE";       // 职称管理总权限
        public static final String VIEW = "TITLE_VIEW";           // 职称查看
        public static final String ADD = "TITLE_ADD";             // 职称新增
        public static final String EDIT = "TITLE_EDIT";           // 职称编辑
        public static final String DELETE = "TITLE_DELETE";       // 职称删除
    }
    
    /**
     * 黑名单管理权限
     */
    public static final class Blacklist {
        public static final String MANAGE = "BLACKLIST_MANAGE";   // 黑名单管理总权限
        public static final String VIEW = "BLACKLIST_VIEW";       // 黑名单查看
        public static final String ADD = "BLACKLIST_ADD";         // 黑名单新增
        public static final String DELETE = "BLACKLIST_DELETE";   // 黑名单删除
    }
    
    /**
     * 违规管理权限
     */
    public static final class Violation {
        public static final String MANAGE = "VIOLATION_MANAGE";   // 违规管理总权限
    }
    
    /**
     * 区域管理权限
     */
    public static final class Area {
        public static final String MANAGE = "AREA_MANAGE";        // 区域管理总权限
        public static final String VIEW = "AREA_VIEW";            // 区域查看
        public static final String ADD = "AREA_ADD";              // 区域新增
        public static final String EDIT = "AREA_EDIT";            // 区域编辑
        public static final String DELETE = "AREA_DELETE";        // 区域删除
    }
    
    /**
     * 预订人员权限管理
     */
    public static final class BookingPersonnel {
        public static final String MANAGE = "BOOKING_PERSONNEL_MANAGE";     // 预订人员权限管理
        public static final String VIEW = "BOOKING_PERSONNEL_VIEW";         // 预订人员权限查看
        public static final String ADD = "BOOKING_PERSONNEL_ADD";           // 预订人员权限新增
        public static final String EDIT = "BOOKING_PERSONNEL_EDIT";         // 预订人员权限编辑
        public static final String DELETE = "BOOKING_PERSONNEL_DELETE";     // 预订人员权限删除
    }
    
    /**
     * 连续预订设置权限
     */
    public static final class ContinuousBooking {
        public static final String MANAGE = "CONTINUOUS_BOOKING_MANAGE";    // 连续预订管理
        public static final String VIEW = "CONTINUOUS_BOOKING_VIEW";        // 连续预订查看
        public static final String EDIT = "CONTINUOUS_BOOKING_EDIT";        // 连续预订编辑
        public static final String DELETE = "CONTINUOUS_BOOKING_DELETE";    // 连续预订删除
    }
    
    /**
     * 用户预订限制权限
     */
    public static final class UserBookingLimit {
        public static final String MANAGE = "USER_BOOKING_LIMIT_MANAGE";    // 用户预订限制管理
        public static final String VIEW = "USER_BOOKING_LIMIT_VIEW";        // 用户预订限制查看
        public static final String EDIT = "USER_BOOKING_LIMIT_EDIT";        // 用户预订限制编辑
        public static final String DELETE = "USER_BOOKING_LIMIT_DELETE";    // 用户预订限制删除
    }
    
    /**
     * 预订时间规则权限
     */
    public static final class BookingTimeRule {
        public static final String MANAGE = "BOOKING_TIME_RULE_MANAGE";     // 预订时间规则管理
        public static final String VIEW = "BOOKING_TIME_RULE_VIEW";         // 预订时间规则查看
        public static final String ADD = "BOOKING_TIME_RULE_ADD";           // 预订时间规则新增
        public static final String EDIT = "BOOKING_TIME_RULE_EDIT";         // 预订时间规则编辑
        public static final String DELETE = "BOOKING_TIME_RULE_DELETE";     // 预订时间规则删除
    }
    
    /**
     * 回收站管理权限
     */
    public static final class RecycleBin {
        public static final String MANAGE = "RECYCLE_BIN_MANAGE";   // 回收站管理
        public static final String VIEW = "RECYCLE_BIN_VIEW";       // 回收站查看
        public static final String RESTORE = "RECYCLE_BIN_RESTORE"; // 回收站恢复
        public static final String DELETE = "RECYCLE_BIN_DELETE";   // 回收站删除
    }
    
    /**
     * 方案管理权限
     */
    public static final class Scheme {
        public static final String MANAGE = "SCHEME_MANAGE";        // 方案管理总权限
        public static final String VIEW = "SCHEME_VIEW";            // 方案查看
        public static final String ADD = "SCHEME_ADD";              // 方案新增
        public static final String EDIT = "SCHEME_EDIT";            // 方案编辑
        public static final String DELETE = "SCHEME_DELETE";        // 方案删除
    }
    
    /**
     * 教室日程权限
     */
    public static final class RoomSchedule {
        public static final String MANAGE = "ROOM_SCHEDULE_MANAGE"; // 教室日程管理
        public static final String VIEW = "ROOM_SCHEDULE_VIEW";     // 教室日程查看
    }
    
    /**
     * 预订审批权限
     */
    public static final class BookingApproval {
        public static final String MANAGE = "BOOKING_APPROVAL_MANAGE";    // 预订审批管理
        public static final String VIEW = "BOOKING_APPROVAL_VIEW";        // 预订审批查看
        public static final String APPROVE = "BOOKING_APPROVAL_APPROVE";  // 预订审批通过
    }
    
    /**
     * 判断是否为管理权限
     * @param permission 权限编码
     * @return 是否为管理权限
     */
    public static boolean isManagePermission(String permission) {
        return permission != null && permission.endsWith("_MANAGE");
    }
    
    /**
     * 获取权限的模块名
     * @param permission 权限编码
     * @return 模块名
     */
    public static String getModule(String permission) {
        if (permission == null || !permission.contains("_")) {
            return null;
        }
        return permission.substring(0, permission.indexOf("_"));
    }
}