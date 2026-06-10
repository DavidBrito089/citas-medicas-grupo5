package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PrescriptionRequest(
        @NotNull UUID medicalRecordId,
        /** RECETA u ORDEN_EXAMEN */
        @NotBlank String tipo,
        @NotBlank String detalle,
        String indicaciones
) {}
