package com.trustai.controller;

import com.trustai.model.entity.AuditLog;
import com.trustai.model.entity.User;
import com.trustai.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit log endpoints for compliance")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping("/logs")
    @Operation(summary = "Get audit logs for the authenticated user")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        Page<AuditLog> logs;
        if (startDate != null && endDate != null) {
            logs = auditLogRepository.findByUserIdAndTimestampBetween(
                    user.getEmail(), startDate, endDate, pageable
            );
        } else {
            logs = auditLogRepository.findByUserId(user.getEmail(), pageable);
        }
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/{id}")
    @Operation(summary = "Get a specific audit log by ID")
    public ResponseEntity<AuditLog> getAuditLog(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return auditLogRepository.findById(id)
                .filter(log -> log.getUserId().equals(user.getEmail()) || user.getRole() == com.trustai.model.entity.User.Role.ADMIN)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

