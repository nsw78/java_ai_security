package com.trustai.config;

import com.trustai.util.StructuredLogger;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Configuration for request correlation and MDC context
 */
@Configuration
public class LoggingConfig {

    @Bean
    public Filter correlationIdFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                
                try {
                    // Generate or use existing correlation ID
                    String correlationId = request.getHeader("X-Correlation-ID");
                    if (correlationId == null || correlationId.isEmpty()) {
                        correlationId = UUID.randomUUID().toString();
                    }
                    
                    // Set in MDC for structured logging
                    MDC.put("correlationId", correlationId);
                    MDC.put("endpoint", request.getRequestURI());
                    MDC.put("method", request.getMethod());
                    MDC.put("ip", getClientIp(request));
                    
                    // Add to response header
                    response.setHeader("X-Correlation-ID", correlationId);
                    
                    filterChain.doFilter(request, response);
                    
                } finally {
                    // Clean up MDC after request
                    MDC.clear();
                }
            }
            
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
        };
    }
}

