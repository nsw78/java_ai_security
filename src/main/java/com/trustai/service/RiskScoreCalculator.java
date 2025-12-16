package com.trustai.service;

import com.trustai.model.entity.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiskScoreCalculator {

    private final PromptSanitizationService sanitizationService;

    @Value("${app.security.risk.thresholds.low:30}")
    private int lowThreshold;

    @Value("${app.security.risk.thresholds.medium:60}")
    private int mediumThreshold;

    @Value("${app.security.risk.thresholds.high:80}")
    private int highThreshold;

    public RiskAssessment calculateRisk(String prompt, Map<String, Object> context) {
        int score = 0;
        List<String> reasons = new java.util.ArrayList<>();

        // Check for prompt injection
        List<String> injectionPatterns = sanitizationService.detectInjection(prompt);
        if (!injectionPatterns.isEmpty()) {
            score += 40;
            reasons.add("Prompt injection detected: " + injectionPatterns.size() + " pattern(s)");
        }

        // Check prompt length
        if (prompt != null) {
            if (prompt.length() > 10000) {
                score += 20;
                reasons.add("Very long prompt (>10k chars)");
            } else if (prompt.length() > 5000) {
                score += 10;
                reasons.add("Long prompt (>5k chars)");
            }
        }

        // Check for suspicious keywords
        String lowerPrompt = prompt != null ? prompt.toLowerCase() : "";
        if (lowerPrompt.contains("jailbreak") || lowerPrompt.contains("bypass")) {
            score += 30;
            reasons.add("Suspicious keywords detected");
        }

        // Check for encoding attempts
        if (prompt != null && (prompt.contains("%") || prompt.contains("\\x") || prompt.contains("\\u"))) {
            score += 15;
            reasons.add("Potential encoding obfuscation");
        }

        // Check for repeated patterns (potential token abuse)
        if (prompt != null && hasRepeatedPatterns(prompt)) {
            score += 25;
            reasons.add("Repeated patterns detected (potential token abuse)");
        }

        // Context-based scoring
        if (context != null) {
            if (context.containsKey("suspicious_ip") && (Boolean) context.get("suspicious_ip")) {
                score += 15;
                reasons.add("Suspicious IP address");
            }
            if (context.containsKey("high_request_rate") && (Boolean) context.get("high_request_rate")) {
                score += 10;
                reasons.add("High request rate");
            }
        }

        // Cap at 100
        score = Math.min(score, 100);

        AuditLog.RiskLevel riskLevel = determineRiskLevel(score);

        return RiskAssessment.builder()
                .score(score)
                .level(riskLevel)
                .reasons(reasons)
                .build();
    }

    private boolean hasRepeatedPatterns(String prompt) {
        if (prompt.length() < 100) {
            return false;
        }
        
        // Check for repeated substrings
        for (int i = 0; i < prompt.length() - 50; i++) {
            String substring = prompt.substring(i, Math.min(i + 50, prompt.length()));
            int count = (prompt.length() - substring.length()) / substring.length();
            if (count > 3) {
                return true;
            }
        }
        return false;
    }

    private AuditLog.RiskLevel determineRiskLevel(int score) {
        if (score >= highThreshold) {
            return AuditLog.RiskLevel.CRITICAL;
        } else if (score >= mediumThreshold) {
            return AuditLog.RiskLevel.HIGH;
        } else if (score >= lowThreshold) {
            return AuditLog.RiskLevel.MEDIUM;
        } else {
            return AuditLog.RiskLevel.LOW;
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class RiskAssessment {
        private int score;
        private AuditLog.RiskLevel level;
        private List<String> reasons;
    }
}

