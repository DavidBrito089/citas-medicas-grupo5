package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
