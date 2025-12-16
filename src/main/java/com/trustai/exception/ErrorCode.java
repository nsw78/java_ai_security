package com.trustai.exception;

/**
 * Centralized error codes for consistent error tracking and insights
 */
public enum ErrorCode {
    // Authentication & Authorization (AUTH_*)
    AUTH_INVALID_CREDENTIALS("AUTH_001", "Invalid credentials provided"),
    AUTH_TOKEN_EXPIRED("AUTH_002", "JWT token has expired"),
    AUTH_TOKEN_INVALID("AUTH_003", "JWT token is invalid"),
    AUTH_UNAUTHORIZED("AUTH_004", "Unauthorized access attempt"),
    
    // Validation (VAL_*)
    VAL_INVALID_INPUT("VAL_001", "Invalid input parameters"),
    VAL_MISSING_REQUIRED("VAL_002", "Required field is missing"),
    VAL_INVALID_FORMAT("VAL_003", "Invalid data format"),
    
    // Prompt Security (PROMPT_*)
    PROMPT_INJECTION_DETECTED("PROMPT_001", "Prompt injection pattern detected"),
    PROMPT_TOO_LONG("PROMPT_002", "Prompt exceeds maximum length"),
    PROMPT_BLOCKED("PROMPT_003", "Prompt blocked by policy engine"),
    PROMPT_SANITIZATION_FAILED("PROMPT_004", "Prompt sanitization failed"),
    
    // Rate Limiting (RATE_*)
    RATE_LIMIT_EXCEEDED("RATE_001", "Rate limit exceeded"),
    RATE_LIMIT_CONFIG_ERROR("RATE_002", "Rate limit configuration error"),
    
    // Database (DB_*)
    DB_CONNECTION_ERROR("DB_001", "Database connection error"),
    DB_QUERY_ERROR("DB_002", "Database query execution error"),
    DB_CONSTRAINT_VIOLATION("DB_003", "Database constraint violation"),
    
    // Redis (REDIS_*)
    REDIS_CONNECTION_ERROR("REDIS_001", "Redis connection error"),
    REDIS_OPERATION_ERROR("REDIS_002", "Redis operation failed"),
    
    // External Services (EXT_*)
    EXT_LLM_SERVICE_ERROR("EXT_001", "External LLM service error"),
    EXT_SERVICE_TIMEOUT("EXT_002", "External service timeout"),
    EXT_SERVICE_UNAVAILABLE("EXT_003", "External service unavailable"),
    
    // Internal (INT_*)
    INT_UNEXPECTED_ERROR("INT_001", "Unexpected internal error"),
    INT_CONFIGURATION_ERROR("INT_002", "Configuration error"),
    INT_SERIALIZATION_ERROR("INT_003", "Data serialization error"),
    
    // Audit (AUDIT_*)
    AUDIT_LOG_FAILED("AUDIT_001", "Audit log creation failed"),
    AUDIT_RETRIEVAL_ERROR("AUDIT_002", "Audit log retrieval error");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

