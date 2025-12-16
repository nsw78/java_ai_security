package com.trustai.repository;

import com.trustai.model.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    Page<AuditLog> findByUserId(String userId, Pageable pageable);
    
    Page<AuditLog> findByUserIdAndTimestampBetween(
            String userId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
    
    @Query("SELECT a FROM AuditLog a WHERE a.riskLevel = :riskLevel AND a.timestamp >= :since")
    List<AuditLog> findHighRiskLogsSince(
            @Param("riskLevel") AuditLog.RiskLevel riskLevel,
            @Param("since") LocalDateTime since
    );
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId AND a.timestamp >= :since")
    Long countByUserIdSince(@Param("userId") String userId, @Param("since") LocalDateTime since);
}

