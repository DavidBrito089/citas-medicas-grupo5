package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String nationalId,
        @NotBlank String fullName,
        LocalDate birthDate,
        String gender,
        String phone,
        String email,
        String address
) {}
