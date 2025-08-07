package com.proshine.common.handler;

import com.proshine.common.enums.ErrorCode;
import com.proshine.common.exception.BusinessException;
import com.proshine.common.response.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * 统一处理系统中的各种异常，返回标准化的错误响应
 * 
 * @author system
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常 - URL: {}, Code: {}, Message: {}", 
            request.getRequestURI(), e.getCode(), e.getMessage());
        return ResponseEntity.fail(e.getCode(), e.getMessage(), e.getData());
    }
    
    /**
     * 处理参数校验异常 - @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        log.error("参数校验失败 - URL: {}, Errors: {}", request.getRequestURI(), errors);
        return ResponseEntity.fail(ErrorCode.PARAM_ERROR.getCode(), 
            "参数校验失败", errors);
    }
    
    /**
     * 处理参数校验异常 - @ModelAttribute
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleBindException(BindException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        log.error("参数绑定失败 - URL: {}, Errors: {}", request.getRequestURI(), errors);
        return ResponseEntity.fail(ErrorCode.PARAM_ERROR.getCode(), 
            "参数绑定失败", errors);
    }
    
    /**
     * 处理参数校验异常 - @RequestParam/@PathVariable
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        
        log.error("约束校验失败 - URL: {}, Message: {}", request.getRequestURI(), message);
        return ResponseEntity.fail(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        String message = String.format("缺少必需的参数: %s", e.getParameterName());
        log.error("缺少请求参数 - URL: {}, Parameter: {}", 
            request.getRequestURI(), e.getParameterName());
        return ResponseEntity.fail(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String message = String.format("参数类型不匹配: %s", e.getName());
        log.error("参数类型不匹配 - URL: {}, Parameter: {}, RequiredType: {}", 
            request.getRequestURI(), e.getName(), e.getRequiredType());
        return ResponseEntity.fail(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 - URL: {}, Message: {}", request.getRequestURI(), e.getMessage(), e);
        
        // 生产环境不应该返回具体的异常信息
        String message = "系统异常，请稍后重试";
        if (log.isDebugEnabled()) {
            message = e.getMessage();
        }
        
        return ResponseEntity.fail(ErrorCode.SYSTEM_ERROR.getCode(), message);
    }
}