package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.enums.AppointmentStatus;
import com.grupo5.citasmedicas.enums.AppointmentType;
import com.grupo5.citasmedicas.model.Appointment;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        int durationMinutes,
        AppointmentType type,
        AppointmentStatus status,
        String reason,
        long version
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getFullName(),
                a.getDoctor().getId(),
                a.getDoctor().getFullName(),
                a.getStartsAt(),
                a.getEndsAt(),
                a.getDurationMinutes(),
                a.getType(),
                a.getStatus(),
                a.getReason(),
                a.getVersion());
    }
}
