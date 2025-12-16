package com.trustai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Structured logging with minimal verbosity
        Map<String, String> context = new HashMap<>();
        context.put("endpoint", request.getRequestURI());
        context.put("method", request.getMethod());
        context.put("fieldCount", String.valueOf(errors.size()));
        
        structuredLogger.logError(
            "VALIDATION_ERROR",
            ErrorCode.VAL_INVALID_INPUT.getCode(),
            String.format("Validation failed: %d field(s) invalid", errors.size()),
            ex,
            context
        );
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid input parameters")
                .details(errors)
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        // Structured logging - no sensitive data
        Map<String, String> context = new HashMap<>();
        context.put("endpoint", request.getRequestURI());
        context.put("ip", getClientIp(request));
        
        structuredLogger.logError(
            "AUTH_ERROR",
            ErrorCode.AUTH_INVALID_CREDENTIALS.getCode(),
            "Authentication failed - invalid credentials",
            ex,
            context
        );
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .message("Invalid email or password")
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        // Structured logging with context
        Map<String, String> context = new HashMap<>();
        context.put("endpoint", request.getRequestURI());
        context.put("method", request.getMethod());
        context.put("exceptionClass", ex.getClass().getSimpleName());
        
        structuredLogger.logError(
            "RUNTIME_ERROR",
            ErrorCode.INT_UNEXPECTED_ERROR.getCode(),
            String.format("Runtime error: %s", ex.getClass().getSimpleName()),
            ex,
            context
        );
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage() != null ? ex.getMessage() : "An error occurred")
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        // Structured logging for unexpected errors
        Map<String, String> context = new HashMap<>();
        context.put("endpoint", request.getRequestURI());
        context.put("method", request.getMethod());
        context.put("exceptionClass", ex.getClass().getSimpleName());
        context.put("ip", getClientIp(request));
        
        structuredLogger.logError(
            "UNEXPECTED_ERROR",
            ErrorCode.INT_UNEXPECTED_ERROR.getCode(),
            String.format("Unexpected error: %s", ex.getClass().getSimpleName()),
            ex,
            context
        );
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Extract client IP from request
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "unknown";
    }

    @lombok.Data
    @lombok.Builder
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> details;
    }
}

