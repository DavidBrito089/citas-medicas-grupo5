package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CertificateRequest(
        @NotNull UUID patientId,
        UUID doctorId,
        Integer restDays,
        @NotBlank String contenido
) {}
