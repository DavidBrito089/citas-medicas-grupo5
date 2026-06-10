package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.CertificateRequest;
import com.grupo5.citasmedicas.dto.response.CertificateResponse;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.MedicalCertificate;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.MedicalCertificateRepository;
import com.grupo5.citasmedicas.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Emision de certificados medicos imprimibles (RQ-CER-01).
 */
@Service
public class CertificateService {

    private final MedicalCertificateRepository certificateRepository;
    private final PatientRepository patientRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final AuditService auditService;

    public CertificateService(MedicalCertificateRepository certificateRepository, PatientRepository patientRepository,
                              CollaboratorRepository collaboratorRepository, AuditService auditService) {
        this.certificateRepository = certificateRepository;
        this.patientRepository = patientRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.auditService = auditService;
    }

    public CertificateResponse findById(UUID id) {
        return CertificateResponse.from(get(id));
    }

    public List<CertificateResponse> findByPatient(UUID patientId) {
        return certificateRepository.findByPatientId(patientId).stream()
                .map(CertificateResponse::from).toList();
    }

    @Transactional
    public CertificateResponse create(CertificateRequest req) {
        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> ResourceNotFoundException.of("Paciente", req.patientId()));
        Collaborator doctor = null;
        if (req.doctorId() != null) {
            doctor = collaboratorRepository.findById(req.doctorId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Medico", req.doctorId()));
        }
        MedicalCertificate cert = MedicalCertificate.builder()
                .patient(patient)
                .doctor(doctor)
                .issueDate(LocalDate.now())
                .restDays(req.restDays())
                .contenido(req.contenido())
                .build();
        cert = certificateRepository.save(cert);
        auditService.record("MedicalCertificate", cert.getId(), "CREATE",
                "Certificado emitido para " + patient.getFullName());
        return CertificateResponse.from(cert);
    }

    private MedicalCertificate get(UUID id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Certificado", id));
    }
}
