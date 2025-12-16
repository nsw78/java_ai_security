package com.trustai.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurePromptRequest {

    @NotBlank(message = "Prompt is required")
    @Size(max = 16000, message = "Prompt must not exceed 16000 characters")
    private String prompt;

    private String model;

    private String policy;

    private Map<String, Object> parameters;

    private Map<String, String> metadata;
}

