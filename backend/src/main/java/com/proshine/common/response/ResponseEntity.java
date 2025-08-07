package com.proshine.common.response;

import com.proshine.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一响应实体
 * 
 * @author system
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntity<T> {
    
    /**
     * 业务状态码
     */
    private String code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间
     */
    private String timestamp;
    
    /**
     * 构造成功响应
     */
    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<>(
            ErrorCode.SUCCESS.getCode(),
            ErrorCode.SUCCESS.getMessage(),
            data,
            getCurrentTimestamp()
        );
    }
    
    /**
     * 构造成功响应（无数据）
     */
    public static <T> ResponseEntity<T> success() {
        return success(null);
    }
    
    /**
     * 构造失败响应
     */
    public static <T> ResponseEntity<T> fail(String message) {
        return new ResponseEntity<>(
            ErrorCode.SYSTEM_ERROR.getCode(),
            message,
            null,
            getCurrentTimestamp()
        );
    }
    
    /**
     * 构造失败响应（带错误码）
     */
    public static <T> ResponseEntity<T> fail(String code, String message) {
        return new ResponseEntity<>(
            code,
            message,
            null,
            getCurrentTimestamp()
        );
    }
    
    /**
     * 构造失败响应（带错误码和数据）
     */
    public static <T> ResponseEntity<T> fail(String code, String message, T data) {
        return new ResponseEntity<>(
            code,
            message,
            data,
            getCurrentTimestamp()
        );
    }
    
    /**
     * 构造失败响应（使用错误码枚举）
     */
    public static <T> ResponseEntity<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }
    
    /**
     * 构造失败响应（使用错误码枚举和数据）
     */
    public static <T> ResponseEntity<T> fail(ErrorCode errorCode, T data) {
        return fail(errorCode.getCode(), errorCode.getMessage(), data);
    }
    
    /**
     * 获取当前时间戳
     */
    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    /**
     * 为了兼容旧代码，保留int类型的code构造方法
     */
    @Deprecated
    public ResponseEntity(int code, String message, T data) {
        this(String.valueOf(code), message, data, getCurrentTimestamp());
    }
}
