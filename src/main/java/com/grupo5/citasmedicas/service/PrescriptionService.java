package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.PrescriptionRequest;
import com.grupo5.citasmedicas.dto.response.PrescriptionResponse;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.MedicalRecord;
import com.grupo5.citasmedicas.model.Prescription;
import com.grupo5.citasmedicas.repository.MedicalRecordRepository;
import com.grupo5.citasmedicas.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Prescripciones medicas y ordenes de examenes (RQ-CON-02).
 */
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository recordRepository;
    private final AuditService auditService;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               MedicalRecordRepository recordRepository, AuditService auditService) {
        this.prescriptionRepository = prescriptionRepository;
        this.recordRepository = recordRepository;
        this.auditService = auditService;
    }

    public List<PrescriptionResponse> findByMedicalRecord(UUID medicalRecordId) {
        return prescriptionRepository.findByMedicalRecordId(medicalRecordId).stream()
                .map(PrescriptionResponse::from).toList();
    }

    @Transactional
    public PrescriptionResponse create(PrescriptionRequest req) {
        MedicalRecord record = recordRepository.findById(req.medicalRecordId())
                .orElseThrow(() -> ResourceNotFoundException.of("Historia clinica", req.medicalRecordId()));
        Prescription p = Prescription.builder()
                .medicalRecord(record)
                .tipo(req.tipo())
                .detalle(req.detalle())
                .indicaciones(req.indicaciones())
                .build();
        p = prescriptionRepository.save(p);
        auditService.record("Prescription", p.getId(), "CREATE", "Prescripcion tipo " + req.tipo());
        return PrescriptionResponse.from(p);
    }
}
