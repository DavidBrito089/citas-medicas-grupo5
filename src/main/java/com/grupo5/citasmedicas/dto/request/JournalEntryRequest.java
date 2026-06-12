package com.grupo5.citasmedicas.dto.request;

import com.grupo5.citasmedicas.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Registro manual de un ingreso o egreso en el libro diario (RQ-FIN-02).
 */
public record JournalEntryRequest(
        @NotNull TransactionType tipo,
        @NotBlank String concepto,
        @NotNull @Positive BigDecimal monto,
        /** Cuenta contable destino (RQ-FIN-03). Opcional. */
        UUID accountId
) {}
