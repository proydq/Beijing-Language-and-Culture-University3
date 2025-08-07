package com.proshine.common.exception;

import com.proshine.common.enums.ErrorCode;
import lombok.Getter;

/**
 * 业务异常类
 * 
 * 用于封装业务逻辑中的异常情况，包含错误码和错误信息
 * 
 * @author system
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final String code;
    private final String message;
    private final Object data;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = null;
    }
    
    public BusinessException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = data;
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = null;
    }
    
    public BusinessException(String code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = null;
    }
}