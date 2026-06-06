package com.grupo5.citasmedicas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Colaborador del consultorio: medicos y otro personal (RQ-COL-01).
 * Se asocia opcionalmente a un usuario del sistema para el inicio de sesion.
 */
@Entity
@Table(name = "collaborators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collaborator extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "national_id", unique = true)
    private String nationalId;

    /** Especialidad medica (ej. Cardiologia). Nulo para personal no medico. */
    private String specialty;

    /** Cargo: MEDICO, RECEPCIONISTA, etc. */
    private String position;

    private String phone;

    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
