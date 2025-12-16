package com.trustai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PromptSanitizationService {

    private final List<String> defaultPatterns = List.of(
        "(?i)(ignore|forget|disregard).*(previous|above|instructions)",
        "(?i)(system|assistant).*(you are|you're|your role)",
        "(?i)(new instructions|new prompt|override)",
        "(?i)(\\[INST\\]|\\[/INST\\]|<|>)",
        "(?i)(jailbreak|bypass|hack)",
        "(?i)(repeat|say|output).*(word|phrase|text)",
        "(?i)(\\$\\{|\\{\\{|\\[\\[)"
    );

    private List<Pattern> compiledPatterns = new ArrayList<>();

    @PostConstruct
    public void init() {
        compilePatterns();
    }

    public String sanitize(String prompt) {
        if (prompt == null || prompt.isEmpty()) {
            return prompt;
        }

        String sanitized = prompt;
        
        // Remove control characters
        sanitized = sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "");
        
        // Normalize whitespace
        sanitized = sanitized.replaceAll("\\s+", " ").trim();
        
        // Remove potential HTML/XML tags
        sanitized = sanitized.replaceAll("<[^>]+>", "");
        
        // Remove potential script tags
        sanitized = sanitized.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        
        // Remove potential SQL injection patterns
        sanitized = sanitized.replaceAll("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute).*", "");
        
        return sanitized;
    }

    public List<String> detectInjection(String prompt) {
        List<String> detectedPatterns = new ArrayList<>();
        
        if (prompt == null || prompt.isEmpty()) {
            return detectedPatterns;
        }

        if (compiledPatterns.isEmpty()) {
            compilePatterns();
        }

        for (Pattern pattern : compiledPatterns) {
            if (pattern.matcher(prompt).find()) {
                detectedPatterns.add(pattern.pattern());
            }
        }

        return detectedPatterns;
    }

    private void compilePatterns() {
        compiledPatterns = new ArrayList<>();
        for (String patternStr : defaultPatterns) {
            try {
                compiledPatterns.add(Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL));
            } catch (Exception e) {
                log.warn("Invalid pattern: {}", patternStr, e);
            }
        }
    }
}

