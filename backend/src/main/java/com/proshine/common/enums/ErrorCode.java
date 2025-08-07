package com.proshine.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统错误码枚举
 * 
 * 错误码规范：
 * - AUTH_xxx: 认证相关错误 (1000-1999)
 * - USER_xxx: 用户相关错误 (2000-2999)
 * - ROOM_xxx: 房间相关错误 (3000-3999)
 * - BOOKING_xxx: 预约相关错误 (4000-4999)
 * - SYSTEM_xxx: 系统错误 (9000-9999)
 * 
 * @author system
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // ========== 通用错误码 ==========
    SUCCESS("0", "成功"),
    SYSTEM_ERROR("9999", "系统异常，请稍后重试"),
    PARAM_ERROR("9998", "参数错误"),
    DATA_NOT_FOUND("9997", "数据不存在"),
    
    // ========== 认证相关 1000-1999 ==========
    AUTH_USERNAME_PASSWORD_ERROR("1001", "用户名或密码错误"),
    AUTH_USER_NOT_FOUND("1002", "用户不存在"),
    AUTH_USER_DISABLED("1003", "用户已被禁用"),
    AUTH_TOKEN_INVALID("1004", "令牌无效"),
    AUTH_TOKEN_EXPIRED("1005", "令牌已过期"),
    AUTH_NO_PERMISSION("1006", "无权限访问"),
    
    // ========== 用户相关 2000-2999 ==========
    USER_NOT_FOUND("2001", "用户不存在"),
    USER_ALREADY_EXISTS("2002", "用户已存在"),
    USER_PHONE_INVALID("2003", "手机号格式不正确"),
    USER_PASSWORD_WEAK("2004", "密码强度不符合要求"),
    USER_OLD_PASSWORD_ERROR("2005", "原密码错误"),
    
    // ========== 房间相关 3000-3999 ==========
    ROOM_NOT_FOUND("3001", "房间不存在"),
    ROOM_ALREADY_EXISTS("3002", "房间已存在"),
    ROOM_NAME_DUPLICATE("3003", "房间名称重复"),
    ROOM_IN_USE("3004", "房间正在使用中"),
    ROOM_IMPORT_ERROR("3005", "房间数据导入失败"),
    ROOM_EXPORT_ERROR("3006", "房间数据导出失败"),
    
    // ========== 预约相关 4000-4999 ==========
    BOOKING_NOT_FOUND("4001", "预约不存在"),
    BOOKING_TIME_CONFLICT("4002", "预约时间冲突"),
    BOOKING_ALREADY_APPROVED("4003", "预约已审批"),
    BOOKING_ALREADY_CANCELLED("4004", "预约已取消"),
    BOOKING_TIME_INVALID("4005", "预约时间无效"),
    BOOKING_NO_PERMISSION("4006", "无预约权限"),
    BOOKING_EXCEED_LIMIT("4007", "超过预约限制"),
    
    // ========== 审批相关 5000-5999 ==========
    APPROVAL_NOT_FOUND("5001", "审批记录不存在"),
    APPROVAL_ALREADY_PROCESSED("5002", "审批已处理"),
    APPROVAL_NO_PERMISSION("5003", "无审批权限"),
    
    // ========== 文件相关 6000-6999 ==========
    FILE_UPLOAD_ERROR("6001", "文件上传失败"),
    FILE_NOT_FOUND("6002", "文件不存在"),
    FILE_TYPE_NOT_ALLOWED("6003", "文件类型不允许"),
    FILE_SIZE_EXCEED("6004", "文件大小超过限制"),
    
    // ========== 业务逻辑相关 7000-7999 ==========
    BLACKLIST_USER_FORBIDDEN("7001", "用户已被列入黑名单"),
    VIOLATION_RECORD_EXISTS("7002", "存在违规记录"),
    CONTINUOUS_BOOKING_EXCEED("7003", "超过连续预约限制"),
    
    // ========== 数据验证相关 8000-8999 ==========
    VALIDATION_ERROR("8001", "数据验证失败"),
    DUPLICATE_DATA("8002", "数据重复"),
    DATA_INTEGRITY_ERROR("8003", "数据完整性错误");
    
    private final String code;
    private final String message;
    
    /**
     * 根据错误码获取错误信息
     * 
     * @param code 错误码
     * @return 错误码枚举
     */
    public static ErrorCode getByCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }
}