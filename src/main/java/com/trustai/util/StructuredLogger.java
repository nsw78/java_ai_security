package com.trustai.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Structured logging utility for consistent error logging with low verbosity
 * but high-value insights for troubleshooting.
 */
@Slf4j
@Component
public class StructuredLogger {

    private static final String CORRELATION_ID = "correlationId";
    private static final String USER_ID = "userId";
    private static final String ENDPOINT = "endpoint";
    private static final String ERROR_TYPE = "errorType";
    private static final String ERROR_CODE = "errorCode";
    private static final String RISK_LEVEL = "riskLevel";

    /**
     * Log error with structured context - minimal verbosity, maximum insight
     */
    public void logError(String errorType, String errorCode, String message, Throwable throwable, Map<String, String> context) {
        try {
            // Set MDC context for structured logging
            if (context != null) {
                context.forEach(MDC::put);
            }
            
            MDC.put(ERROR_TYPE, errorType);
            MDC.put(ERROR_CODE, errorCode);
            
            // Log with insight message
            String insight = buildInsight(errorType, errorCode, message, throwable);
            log.error(insight, throwable);
            
        } finally {
            // Clean up MDC
            MDC.clear();
        }
    }

    /**
     * Log error with minimal context
     */
    public void logError(String errorType, String errorCode, String message, Throwable throwable) {
        logError(errorType, errorCode, message, throwable, null);
    }

    /**
     * Log warning with structured context
     */
    public void logWarning(String warningType, String message, Map<String, String> context) {
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            
            String insight = String.format("[%s] %s", warningType, message);
            log.warn(insight);
            
        } finally {
            MDC.clear();
        }
    }

    /**
     * Log info with structured context
     */
    public void logInfo(String infoType, String message, Map<String, String> context) {
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            
            String insight = String.format("[%s] %s", infoType, message);
            log.info(insight);
            
        } finally {
            MDC.clear();
        }
    }

    /**
     * Set correlation ID for request tracking
     */
    public String setCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, correlationId);
        return correlationId;
    }

    /**
     * Set user context
     */
    public void setUserContext(String userId) {
        MDC.put(USER_ID, userId);
    }

    /**
     * Set endpoint context
     */
    public void setEndpointContext(String endpoint) {
        MDC.put(ENDPOINT, endpoint);
    }

    /**
     * Build insight message - concise but informative
     */
    private String buildInsight(String errorType, String errorCode, String message, Throwable throwable) {
        StringBuilder insight = new StringBuilder();
        insight.append("[").append(errorType).append(":").append(errorCode).append("] ");
        insight.append(message);
        
        if (throwable != null) {
            String rootCause = getRootCause(throwable);
            if (!rootCause.equals(throwable.getClass().getSimpleName())) {
                insight.append(" | Root cause: ").append(rootCause);
            }
        }
        
        return insight.toString();
    }

    /**
     * Get root cause class name for insight
     */
    private String getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getClass().getSimpleName();
    }

    /**
     * Clear all MDC context
     */
    public void clearContext() {
        MDC.clear();
    }
}

