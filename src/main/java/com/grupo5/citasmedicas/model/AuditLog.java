package com.grupo5.citasmedicas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Registro de auditoria (tabla AUDIT_LOGS del DDS). Guarda quien hizo que sobre
 * que entidad. Cubre la seccion de seguridad/auditoria.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    /** Usuario que ejecuto la accion. */
    private UUID userId;

    private String username;

    /** Nombre de la entidad afectada, ej. Appointment. */
    @Column(nullable = false)
    private String entity;

    private UUID entityId;

    /** CREATE, UPDATE, DELETE, etc. */
    @Column(nullable = false)
    private String operation;

    @Column(columnDefinition = "TEXT")
    private String detail;
}
