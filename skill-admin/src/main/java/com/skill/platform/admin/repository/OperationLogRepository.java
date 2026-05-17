package com.skill.platform.admin.repository;

import com.skill.platform.admin.model.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, UUID>, JpaSpecificationExecutor<OperationLog> {
    Page<OperationLog> findByLogType(String logType, Pageable pageable);
    Page<OperationLog> findByUserId(UUID userId, Pageable pageable);
    Page<OperationLog> findByCreatedAtBetween(Instant start, Instant end, Pageable pageable);
}
