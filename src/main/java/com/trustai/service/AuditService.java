package com.trustai.service;

import com.trustai.model.entity.AuditLog;
import com.trustai.model.entity.User;
import com.trustai.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional
    public void logRequest(
            User user,
            String endpoint,
            String method,
            String prompt,
            String response,
            Integer riskScore,
            AuditLog.RiskLevel riskLevel,
            Boolean blocked,
            String blockReason,
            String sanitizedPrompt,
            HttpServletRequest request,
            Map<String, Object> metadata
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(user.getEmail())
                    .endpoint(endpoint)
                    .method(method)
                    .prompt(prompt)
                    .response(response)
                    .riskScore(riskScore)
                    .riskLevel(riskLevel)
                    .blocked(blocked)
                    .blockReason(blockReason)
                    .sanitizedPrompt(sanitizedPrompt)
                    .ipAddress(getClientIpAddress(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .metadata(metadata)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}

