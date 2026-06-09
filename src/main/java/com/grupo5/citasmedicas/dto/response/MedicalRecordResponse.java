package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.model.MedicalRecord;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MedicalRecordResponse(
        UUID id,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        UUID appointmentId,
        String motivoConsulta,
        String diagnostico,
        String tratamiento,
        String observaciones,
        OffsetDateTime createdAt
) {
    public static MedicalRecordResponse from(MedicalRecord r) {
        return new MedicalRecordResponse(
                r.getId(),
                r.getPatient().getId(),
                r.getPatient().getFullName(),
                r.getDoctor() != null ? r.getDoctor().getId() : null,
                r.getDoctor() != null ? r.getDoctor().getFullName() : null,
                r.getAppointment() != null ? r.getAppointment().getId() : null,
                r.getMotivoConsulta(),
                r.getDiagnostico(),
                r.getTratamiento(),
                r.getObservaciones(),
                r.getCreatedAt());
    }
}
