package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.enums.InvoiceStatus;
import com.grupo5.citasmedicas.model.Invoice;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        String numero,
        UUID patientId,
        String facturarA,
        String identificacionTercero,
        BigDecimal subtotal,
        BigDecimal impuestos,
        BigDecimal total,
        InvoiceStatus status,
        OffsetDateTime issuedAt,
        List<Item> items
) {
    public record Item(String descripcion, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {}

    public static InvoiceResponse from(Invoice inv) {
        List<Item> items = inv.getItems().stream()
                .map(i -> new Item(i.getDescripcion(), i.getCantidad(), i.getPrecioUnitario(), i.getSubtotal()))
                .toList();
        return new InvoiceResponse(
                inv.getId(),
                inv.getNumero(),
                inv.getPatient() != null ? inv.getPatient().getId() : null,
                inv.getFacturarA(),
                inv.getIdentificacionTercero(),
                inv.getSubtotal(),
                inv.getImpuestos(),
                inv.getTotal(),
                inv.getStatus(),
                inv.getIssuedAt(),
                items);
    }
}
