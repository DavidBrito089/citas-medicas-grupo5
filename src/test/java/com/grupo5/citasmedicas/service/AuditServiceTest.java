package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.model.AuditLog;
import com.grupo5.citasmedicas.repository.AuditLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock AuditLogRepository auditLogRepository;
    @InjectMocks AuditService auditService;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordGuardaLogConUsuarioSystemSiNoHayAuth() {
        UUID entityId = UUID.randomUUID();
        auditService.record("Patient", entityId, "CREATE", "detalle");
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertThat(captor.getValue().getEntity()).isEqualTo("Patient");
        assertThat(captor.getValue().getOperation()).isEqualTo("CREATE");
        assertThat(captor.getValue().getUsername()).isEqualTo("system");
    }

    @Test
    void currentUsernameDevuelveElAutenticado() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null));
        assertThat(auditService.currentUsername()).isEqualTo("admin");
    }
}
