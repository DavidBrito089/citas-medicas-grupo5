package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CollaboratorRequest(
        @NotBlank String fullName,
        String nationalId,
        String specialty,
        String position,
        String phone,
        String email,
        UUID userId
) {}
