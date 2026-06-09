package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.MedicalRecordRequest;
import com.grupo5.citasmedicas.dto.response.MedicalRecordResponse;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Appointment;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.MedicalRecord;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.AppointmentRepository;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.MedicalRecordRepository;
import com.grupo5.citasmedicas.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Registro de consultas e historia clinica (RQ-CON-01).
 */
@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final PatientRepository patientRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditService auditService;

    public MedicalRecordService(MedicalRecordRepository recordRepository, PatientRepository patientRepository,
                                CollaboratorRepository collaboratorRepository,
                                AppointmentRepository appointmentRepository, AuditService auditService) {
        this.recordRepository = recordRepository;
        this.patientRepository = patientRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
    }

    public MedicalRecordResponse findById(UUID id) {
        return MedicalRecordResponse.from(get(id));
    }

    public List<MedicalRecordResponse> findByPatient(UUID patientId) {
        return recordRepository.findByPatientIdAndDeletedAtIsNullOrderByCreatedAtDesc(patientId).stream()
                .map(MedicalRecordResponse::from).toList();
    }

    public List<MedicalRecordResponse> findByPatientNationalId(String nationalId) {
        return recordRepository.findByPatientNationalIdAndDeletedAtIsNullOrderByCreatedAtDesc(nationalId).stream()
                .map(MedicalRecordResponse::from).toList();
    }

    @Transactional
    public MedicalRecordResponse create(MedicalRecordRequest req) {
        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> ResourceNotFoundException.of("Paciente", req.patientId()));

        Collaborator doctor = null;
        if (req.doctorId() != null) {
            doctor = collaboratorRepository.findById(req.doctorId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Medico", req.doctorId()));
        }
        Appointment appointment = null;
        if (req.appointmentId() != null) {
            appointment = appointmentRepository.findById(req.appointmentId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Cita", req.appointmentId()));
        }

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .appointment(appointment)
                .motivoConsulta(req.motivoConsulta())
                .diagnostico(req.diagnostico())
                .tratamiento(req.tratamiento())
                .observaciones(req.observaciones())
                .build();
        record = recordRepository.save(record);
        auditService.record("MedicalRecord", record.getId(), "CREATE",
                "Consulta registrada para paciente " + patient.getFullName());
        return MedicalRecordResponse.from(record);
    }

    private MedicalRecord get(UUID id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Historia clinica", id));
    }
}
