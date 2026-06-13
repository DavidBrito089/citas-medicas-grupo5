package com.grupo5.citasmedicas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Parametros generales de la institucion (RQ-CONF-03): nombre, RUC, logo, etc.
 * Se maneja como registro unico (singleton de configuracion).
 */
@Entity
@Table(name = "institution_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionConfig extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    private String ruc;

    private String direccion;

    private String telefono;

    private String email;

    /** URL o ruta del logo institucional. */
    private String logoUrl;

    /** Moneda por defecto, ej. USD. */
    @Builder.Default
    private String moneda = "USD";
}
