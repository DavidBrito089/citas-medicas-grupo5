package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.constraints.NotBlank;

public record InstitutionConfigRequest(
        @NotBlank String nombre,
        String ruc,
        String direccion,
        String telefono,
        String email,
        String logoUrl,
        String moneda
) {}
