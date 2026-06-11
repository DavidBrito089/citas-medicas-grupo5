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
 * Cuenta del plan de cuentas contable (RQ-CONF-02).
 */
@Entity
@Table(name = "accounting_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingAccount extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    /** ACTIVO, PASIVO, PATRIMONIO, INGRESO, GASTO */
    @Column(nullable = false)
    private String tipo;

    @Builder.Default
    private boolean activa = true;
}
