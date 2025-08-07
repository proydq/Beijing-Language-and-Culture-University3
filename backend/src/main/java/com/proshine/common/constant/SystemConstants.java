package com.proshine.common.constant;

/**
 * 系统常量类
 * 
 * @author system
 * @since 1.0.0
 */
public final class SystemConstants {
    
    private SystemConstants() {
        // 私有构造函数，防止实例化
    }
    
    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "Admin@123";
    
    /**
     * 验证码有效期（分钟）
     */
    public static final int VERIFY_CODE_EXPIRE_MINUTES = 15;
    
    /**
     * 验证码长度
     */
    public static final int VERIFY_CODE_LENGTH = 6;
    
    /**
     * JWT Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * 请求头中的Token字段名
     */
    public static final String TOKEN_HEADER = "Authorization";
    
    /**
     * 分页默认页码
     */
    public static final int DEFAULT_PAGE_NUMBER = 1;
    
    /**
     * 分页默认大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 文件上传最大大小（MB）
     */
    public static final int MAX_FILE_SIZE_MB = 10;
    
    /**
     * 允许上传的文件类型
     */
    public static final String[] ALLOWED_FILE_TYPES = {
        "jpg", "jpeg", "png", "gif", "bmp",
        "doc", "docx", "xls", "xlsx", "pdf"
    };
    
    /**
     * 日期时间格式
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * 时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    /**
     * 系统管理员角色代码
     */
    public static final String ROLE_ADMIN = "ADMIN";
    
    /**
     * 普通用户角色代码
     */
    public static final String ROLE_USER = "USER";
    
    /**
     * 审批状态
     */
    public static final class ApprovalStatus {
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
        public static final String CANCELLED = "CANCELLED";
    }
    
    /**
     * 使用状态
     */
    public static final class UsageStatus {
        public static final String NOT_STARTED = "NOT_STARTED";
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELLED = "CANCELLED";
    }
    
    /**
     * 缓存键前缀
     */
    public static final class CacheKey {
        public static final String USER_PREFIX = "user:";
        public static final String ROLE_PREFIX = "role:";
        public static final String PERMISSION_PREFIX = "permission:";
        public static final String VERIFY_CODE_PREFIX = "verify_code:";
        public static final String PASSWORD_RESET_PREFIX = "pwd_reset:";
    }
}