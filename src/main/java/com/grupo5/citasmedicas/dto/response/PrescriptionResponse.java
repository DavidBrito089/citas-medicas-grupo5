package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.model.Prescription;

import java.util.UUID;

public record PrescriptionResponse(
        UUID id,
        UUID medicalRecordId,
        String tipo,
        String detalle,
        String indicaciones
) {
    public static PrescriptionResponse from(Prescription p) {
        return new PrescriptionResponse(p.getId(), p.getMedicalRecord().getId(),
                p.getTipo(), p.getDetalle(), p.getIndicaciones());
    }
}
