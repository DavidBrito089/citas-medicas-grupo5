package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record InvoiceRequest(
        UUID patientId,
        UUID appointmentId,
        /** Facturacion a tercero (RQ-PAC-02). Opcional. */
        String facturarA,
        String identificacionTercero,
        /** Porcentaje de impuesto (ej. 12 para 12%). 0 si no aplica. */
        BigDecimal porcentajeImpuesto,
        @NotEmpty @Valid List<InvoiceItemRequest> items
) {}
