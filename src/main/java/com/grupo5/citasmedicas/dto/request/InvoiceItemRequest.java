package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InvoiceItemRequest(
        @NotBlank String descripcion,
        @Min(1) int cantidad,
        @NotNull BigDecimal precioUnitario
) {}
