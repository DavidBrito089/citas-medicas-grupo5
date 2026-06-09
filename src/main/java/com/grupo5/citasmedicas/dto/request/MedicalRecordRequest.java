package com.grupo5.citasmedicas.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MedicalRecordRequest(
        @NotNull UUID patientId,
        UUID doctorId,
        UUID appointmentId,
        String motivoConsulta,
        String diagnostico,
        String tratamiento,
        String observaciones
) {}
