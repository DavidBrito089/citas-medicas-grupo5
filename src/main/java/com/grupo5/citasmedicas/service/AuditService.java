package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.model.AuditLog;
import com.grupo5.citasmedicas.repository.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio de auditoria: registra operaciones sensibles en la tabla audit_logs
 * (seccion de seguridad/auditoria del DDS).
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(String entity, UUID entityId, String operation, String detail) {
        String username = currentUsername();
        AuditLog log = AuditLog.builder()
                .username(username)
                .entity(entity)
                .entityId(entityId)
                .operation(operation)
                .detail(detail)
                .build();
        auditLogRepository.save(log);
    }

    public String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
