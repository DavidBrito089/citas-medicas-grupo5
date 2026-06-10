package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.model.MedicalCertificate;

import java.time.LocalDate;
import java.util.UUID;

public record CertificateResponse(
        UUID id,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        LocalDate issueDate,
        Integer restDays,
        String contenido
) {
    public static CertificateResponse from(MedicalCertificate c) {
        return new CertificateResponse(
                c.getId(),
                c.getPatient().getId(),
                c.getPatient().getFullName(),
                c.getDoctor() != null ? c.getDoctor().getId() : null,
                c.getDoctor() != null ? c.getDoctor().getFullName() : null,
                c.getIssueDate(),
                c.getRestDays(),
                c.getContenido());
    }
}
