package com.trustai.controller;

import com.trustai.model.dto.request.SecurePromptRequest;
import com.trustai.model.dto.response.SecurePromptResponse;
import com.trustai.model.entity.User;
import com.trustai.service.*;
import com.trustai.service.PolicyEngine.PolicyResult;
import com.trustai.service.RiskScoreCalculator.RiskAssessment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/secure-prompt")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Secure Prompt", description = "Secure AI prompt processing endpoints")
@SecurityRequirement(name = "bearerAuth")
public class SecurePromptController {

    private final PromptSanitizationService sanitizationService;
    private final RiskScoreCalculator riskCalculator;
    private final PolicyEngine policyEngine;
    private final RateLimitService rateLimitService;
    private final AuditService auditService;

    @PostMapping
    @Operation(summary = "Process a secure AI prompt with full security checks")
    public ResponseEntity<SecurePromptResponse> processSecurePrompt(
            @Valid @RequestBody SecurePromptRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest
    ) {
        String requestId = UUID.randomUUID().toString();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("requestId", requestId);
        metadata.put("model", request.getModel());

        // Rate limiting check
        RateLimitService.ConsumptionProbe rateLimit = rateLimitService.tryConsumeAndReturnRemaining(user);
        if (!rateLimit.isConsumed()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Rate-Limit-Remaining", String.valueOf(rateLimit.getRemaining()));
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .headers(headers)
                    .body(SecurePromptResponse.builder()
                            .requestId(requestId)
                            .blocked(true)
                            .blockReason("Rate limit exceeded")
                            .build());
        }

        // Policy evaluation
        PolicyResult policyResult = policyEngine.evaluate(request);
        if (!policyResult.isAllowed()) {
            auditService.logRequest(
                    user, httpRequest.getRequestURI(), httpRequest.getMethod(),
                    request.getPrompt(), null, 100, com.trustai.model.entity.AuditLog.RiskLevel.CRITICAL,
                    true, policyResult.getReason(), null, httpRequest, metadata
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(SecurePromptResponse.builder()
                            .requestId(requestId)
                            .blocked(true)
                            .blockReason(policyResult.getReason())
                            .build());
        }

        // Sanitization
        String sanitizedPrompt = sanitizationService.sanitize(request.getPrompt());
        
        // Risk assessment
        Map<String, Object> riskContext = new HashMap<>();
        riskContext.put("user_plan", user.getPlan().name());
        RiskAssessment riskAssessment = riskCalculator.calculateRisk(request.getPrompt(), riskContext);
        
        metadata.put("riskReasons", riskAssessment.getReasons());
        metadata.put("policy", policyResult.getPolicy());

        // Block if risk is too high
        boolean shouldBlock = riskAssessment.getLevel() == com.trustai.model.entity.AuditLog.RiskLevel.CRITICAL ||
                             riskAssessment.getScore() >= 90;

        String response = null;
        if (!shouldBlock) {
            // In a real implementation, this would call the LLM API
            response = "This is a mock response. In production, this would call the LLM API with the sanitized prompt.";
        }

        // Audit logging (async)
        auditService.logRequest(
                user, httpRequest.getRequestURI(), httpRequest.getMethod(),
                request.getPrompt(), response, riskAssessment.getScore(),
                riskAssessment.getLevel(), shouldBlock,
                shouldBlock ? "High risk score: " + riskAssessment.getScore() : null,
                sanitizedPrompt, httpRequest, metadata
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Rate-Limit-Remaining", String.valueOf(rateLimit.getRemaining()));
        headers.add("X-Risk-Score", String.valueOf(riskAssessment.getScore()));

        SecurePromptResponse secureResponse = SecurePromptResponse.builder()
                .requestId(requestId)
                .sanitizedPrompt(sanitizedPrompt)
                .response(response)
                .riskScore(riskAssessment.getScore())
                .riskLevel(riskAssessment.getLevel())
                .blocked(shouldBlock)
                .blockReason(shouldBlock ? "High risk score: " + riskAssessment.getScore() : null)
                .timestamp(java.time.LocalDateTime.now())
                .metadata(metadata)
                .build();

        return ResponseEntity.ok()
                .headers(headers)
                .body(secureResponse);
    }
}

