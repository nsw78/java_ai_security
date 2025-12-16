package com.trustai.model.dto.response;

import com.trustai.model.entity.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurePromptResponse {

    private String requestId;
    private String sanitizedPrompt;
    private String response;
    private Integer riskScore;
    private AuditLog.RiskLevel riskLevel;
    private Boolean blocked;
    private String blockReason;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}

