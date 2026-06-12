package com.grupo5.citasmedicas.model;

import com.grupo5.citasmedicas.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Asiento del libro diario de caja (RQ-FIN-02). Cada ingreso/egreso se registra
 * automaticamente y se asocia a una cuenta contable (RQ-FIN-03).
 */
@Entity
@Table(name = "cash_journal_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashJournalEntry extends BaseEntity {

    @Column(name = "fecha", nullable = false)
    private OffsetDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType tipo;

    @Column(nullable = false)
    private String concepto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    /** Cuenta contable destino (RQ-FIN-03). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountingAccount account;

    /** Comprobante que origino el movimiento, si aplica. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}
