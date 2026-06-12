package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.enums.TransactionType;
import com.grupo5.citasmedicas.model.CashJournalEntry;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record JournalEntryResponse(
        UUID id,
        OffsetDateTime fecha,
        TransactionType tipo,
        String concepto,
        BigDecimal monto,
        String cuentaCodigo,
        String cuentaNombre,
        UUID invoiceId
) {
    public static JournalEntryResponse from(CashJournalEntry e) {
        return new JournalEntryResponse(
                e.getId(),
                e.getFecha(),
                e.getTipo(),
                e.getConcepto(),
                e.getMonto(),
                e.getAccount() != null ? e.getAccount().getCodigo() : null,
                e.getAccount() != null ? e.getAccount().getNombre() : null,
                e.getInvoice() != null ? e.getInvoice().getId() : null);
    }
}
