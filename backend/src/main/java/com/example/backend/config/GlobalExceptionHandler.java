package com.example.backend.config;

import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("[BusinessException] {} - {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, Object> response = buildErrorResponse(ex.getCode(), ex.getMessage(), request);
        return ResponseEntity.status(ex.getCode()).body(response);
    }

    /**
     * 资源不存在异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("[ResourceNotFoundException] {} - {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, Object> response = buildErrorResponse(404, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 参数校验失败 - @RequestBody 校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "",
                        (existing, replacement) -> existing + "; " + replacement
                ));
        
        String message = errors.values().stream().findFirst().orElse("参数校验失败");
        log.warn("[Validation Failed] {} - {}", request.getRequestURI(), errors);
        
        Map<String, Object> response = buildErrorResponse(400, message, request);
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 参数校验失败 - @RequestParam / @PathVariable 校验
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("参数校验失败");
        
        log.warn("[ConstraintViolation] {} - {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, Object> response = buildErrorResponse(400, message, request);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 缺少必要参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String message = String.format("缺少必要参数: %s", ex.getParameterName());
        log.warn("[MissingParam] {} - {}", request.getRequestURI(), message);
        
        Map<String, Object> response = buildErrorResponse(400, message, request);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        String message = String.format("参数类型错误: %s 应为 %s类型", 
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知");
        log.warn("[TypeMismatch] {} - {}", request.getRequestURI(), message);
        
        Map<String, Object> response = buildErrorResponse(400, message, request);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * JSON 解析错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        log.warn("[JSON Parse Error] {} - {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, Object> response = buildErrorResponse(400, "请求体格式错误，请检查JSON格式", request);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        String message = String.format("不支持的请求方法: %s", ex.getMethod());
        log.warn("[MethodNotSupported] {} - {}", request.getRequestURI(), message);
        
        Map<String, Object> response = buildErrorResponse(405, message, request);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 404 未找到处理器
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        String message = String.format("请求路径不存在: %s %s", ex.getHttpMethod(), ex.getRequestURL());
        log.warn("[NotFound] {}", message);
        
        Map<String, Object> response = buildErrorResponse(404, message, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        log.error("[RuntimeException] {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        
        Map<String, Object> response = buildErrorResponse(500, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 访问被拒绝（权限不足）
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        
        log.warn("[AccessDenied] {} - {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, Object> response = buildErrorResponse(403, "权限不足，无法访问此资源", request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            org.springframework.security.core.AuthenticationException ex, HttpServletRequest request) {
        
        log.warn("[AuthenticationFailed] {} - {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, Object> response = buildErrorResponse(401, "认证失败，请重新登录", request);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception ex, HttpServletRequest request) {
        
        log.error("[UnknownException] {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        
        Map<String, Object> response = buildErrorResponse(500, "服务器内部错误，请稍后重试", request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 构建错误响应
     */
    private Map<String, Object> buildErrorResponse(int code, String message, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        response.put("path", request.getRequestURI());
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
}
