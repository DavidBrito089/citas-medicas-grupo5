package com.grupo5.citasmedicas.dto.request;

import com.grupo5.citasmedicas.enums.AppointmentType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull UUID patientId,
        @NotNull UUID doctorId,
        @NotNull OffsetDateTime startsAt,
        @NotNull AppointmentType type,
        /** Opcional: si es null se usa la duracion por defecto del tipo. */
        Integer durationMinutes,
        String reason
) {}
