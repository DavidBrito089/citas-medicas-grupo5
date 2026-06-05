package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.PatientRequest;
import com.grupo5.citasmedicas.dto.response.PatientResponse;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Gestion de pacientes (RQ-PAC-01).
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AuditService auditService;

    public PatientService(PatientRepository patientRepository, AuditService auditService) {
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    public List<PatientResponse> findAll() {
        return patientRepository.findAllByDeletedAtIsNull().stream().map(PatientResponse::from).toList();
    }

    public PatientResponse findById(UUID id) {
        return PatientResponse.from(get(id));
    }

    public PatientResponse findByNationalId(String nationalId) {
        Patient p = patientRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con cedula: " + nationalId));
        return PatientResponse.from(p);
    }

    @Transactional
    public PatientResponse create(PatientRequest req) {
        if (patientRepository.existsByNationalId(req.nationalId())) {
            throw new BusinessException("ERR_PATIENT_DUPLICATE", "Ya existe un paciente con esa cedula");
        }
        Patient p = Patient.builder()
                .nationalId(req.nationalId())
                .fullName(req.fullName())
                .birthDate(req.birthDate())
                .gender(req.gender())
                .phone(req.phone())
                .email(req.email())
                .address(req.address())
                .build();
        p = patientRepository.save(p);
        auditService.record("Patient", p.getId(), "CREATE", "Paciente " + p.getFullName());
        return PatientResponse.from(p);
    }

    @Transactional
    public PatientResponse update(UUID id, PatientRequest req) {
        Patient p = get(id);
        p.setFullName(req.fullName());
        p.setBirthDate(req.birthDate());
        p.setGender(req.gender());
        p.setPhone(req.phone());
        p.setEmail(req.email());
        p.setAddress(req.address());
        p = patientRepository.save(p);
        auditService.record("Patient", p.getId(), "UPDATE", "Actualizacion de paciente");
        return PatientResponse.from(p);
    }

    @Transactional
    public void delete(UUID id) {
        Patient p = get(id);
        p.setDeletedAt(OffsetDateTime.now()); // soft-delete
        patientRepository.save(p);
        auditService.record("Patient", id, "DELETE", "Soft-delete de paciente");
    }

    private Patient get(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Paciente", id));
    }
}
