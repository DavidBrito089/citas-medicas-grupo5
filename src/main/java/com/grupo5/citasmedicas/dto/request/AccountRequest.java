package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AccountRequest(
        @NotBlank String codigo,
        @NotBlank String nombre,
        /** ACTIVO, PASIVO, PATRIMONIO, INGRESO, GASTO */
        @NotBlank String tipo,
        Boolean activa
) {}
