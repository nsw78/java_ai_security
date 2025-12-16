package com.trustai.service;

import com.trustai.model.dto.request.SecurePromptRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PolicyEngine {

    @Value("${app.security.policy.default-policy:restrictive}")
    private String defaultPolicy;

    @Value("#{${app.security.policy.policies:{}}}")
    private Map<String, Map<String, Object>> policies;

    public PolicyResult evaluate(SecurePromptRequest request) {
        String policyName = request.getPolicy() != null ? request.getPolicy() : defaultPolicy;
        Map<String, Object> policy = policies.getOrDefault(policyName, policies.get(defaultPolicy));

        if (policy == null) {
            return PolicyResult.builder()
                    .allowed(false)
                    .reason("Policy not found: " + policyName)
                    .build();
        }

        List<String> violations = new java.util.ArrayList<>();

        // Check prompt length
        Integer maxLength = (Integer) policy.get("max-prompt-length");
        if (maxLength != null && request.getPrompt().length() > maxLength) {
            violations.add("Prompt length (" + request.getPrompt().length() + ") exceeds maximum (" + maxLength + ")");
        }

        // Check external API access
        Boolean allowExternalApis = (Boolean) policy.get("allow-external-apis");
        if (allowExternalApis != null && !allowExternalApis && request.getModel() != null) {
            @SuppressWarnings("unchecked")
            List<String> allowedDomains = (List<String>) policy.get("allowed-domains");
            if (allowedDomains == null || allowedDomains.isEmpty() || 
                !allowedDomains.contains("*") && !isAllowedDomain(request.getModel(), allowedDomains)) {
                violations.add("External API access not allowed for model: " + request.getModel());
            }
        }

        boolean allowed = violations.isEmpty();
        return PolicyResult.builder()
                .allowed(allowed)
                .violations(violations)
                .reason(allowed ? null : String.join("; ", violations))
                .policy(policyName)
                .build();
    }

    private boolean isAllowedDomain(String model, List<String> allowedDomains) {
        if (model == null) {
            return false;
        }
        return allowedDomains.stream().anyMatch(domain -> 
            model.toLowerCase().contains(domain.toLowerCase())
        );
    }

    @lombok.Data
    @lombok.Builder
    public static class PolicyResult {
        private boolean allowed;
        private String reason;
        private List<String> violations;
        private String policy;
    }
}

