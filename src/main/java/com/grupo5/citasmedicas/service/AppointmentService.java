package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.AppointmentRequest;
import com.grupo5.citasmedicas.dto.response.AppointmentHistoryResponse;
import com.grupo5.citasmedicas.dto.response.AppointmentResponse;
import com.grupo5.citasmedicas.enums.AppointmentStatus;
import com.grupo5.citasmedicas.enums.AppointmentType;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Appointment;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.AppointmentRepository;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Gestion de la agenda de citas (RQ-CIT-01), maquina de estados, bloqueo de
 * solapamientos y servicio web de historial por cedula (RQ-CIT-02).
 */
@Service
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final AuditService auditService;

    /** Transiciones permitidas de la maquina de estados (DDS). */
    private static final Map<AppointmentStatus, Set<AppointmentStatus>> TRANSITIONS =
            new EnumMap<>(AppointmentStatus.class);

    static {
        TRANSITIONS.put(AppointmentStatus.PENDIENTE,
                Set.of(AppointmentStatus.CONFIRMADA, AppointmentStatus.CANCELADA, AppointmentStatus.EXPIRADA));
        TRANSITIONS.put(AppointmentStatus.CONFIRMADA,
                Set.of(AppointmentStatus.ATENDIDA, AppointmentStatus.CANCELADA));
        TRANSITIONS.put(AppointmentStatus.ATENDIDA, Set.of(AppointmentStatus.CERRADA));
        TRANSITIONS.put(AppointmentStatus.CERRADA, Set.of());
        TRANSITIONS.put(AppointmentStatus.CANCELADA, Set.of());
        TRANSITIONS.put(AppointmentStatus.EXPIRADA, Set.of());
    }

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
                              CollaboratorRepository collaboratorRepository, AuditService auditService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.auditService = auditService;
    }

    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream().map(AppointmentResponse::from).toList();
    }

    public AppointmentResponse findById(UUID id) {
        return AppointmentResponse.from(get(id));
    }

    public List<AppointmentResponse> findByDoctor(UUID doctorId) {
        return appointmentRepository.findByDoctorIdAndDeletedAtIsNull(doctorId).stream()
                .map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse schedule(AppointmentRequest req) {
        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> ResourceNotFoundException.of("Paciente", req.patientId()));
        Collaborator doctor = collaboratorRepository.findById(req.doctorId())
                .orElseThrow(() -> ResourceNotFoundException.of("Medico", req.doctorId()));

        AppointmentType type = req.type();
        int duration = req.durationMinutes() != null ? req.durationMinutes() : type.getDefaultDurationMinutes();
        if (duration <= 0) {
            throw new BusinessException("ERR_DURATION", "La duracion debe ser mayor a cero");
        }
        OffsetDateTime starts = req.startsAt();
        OffsetDateTime ends = starts.plusMinutes(duration);

        // Bloqueo de solapamientos (DDS - reglas de negocio).
        List<Appointment> overlapping = appointmentRepository.findOverlapping(doctor.getId(), starts, ends);
        if (!overlapping.isEmpty()) {
            throw new BusinessException("ERR_OVERLAP",
                    "El medico ya tiene una cita en ese horario");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .startsAt(starts)
                .endsAt(ends)
                .durationMinutes(duration)
                .type(type)
                .status(AppointmentStatus.PENDIENTE)
                .reason(req.reason())
                .build();
        appointment = appointmentRepository.save(appointment);
        auditService.record("Appointment", appointment.getId(), "CREATE",
                "Cita agendada para paciente " + patient.getFullName());
        return AppointmentResponse.from(appointment);
    }

    @Transactional
    public AppointmentResponse changeStatus(UUID id, AppointmentStatus target) {
        Appointment appointment = get(id);
        AppointmentStatus current = appointment.getStatus();
        if (!TRANSITIONS.getOrDefault(current, Set.of()).contains(target)) {
            throw new BusinessException("ERR_INVALID_TRANSITION",
                    "Transicion invalida de " + current + " a " + target);
        }
        appointment.setStatus(target);
        appointment = appointmentRepository.save(appointment);
        auditService.record("Appointment", id, "STATUS_CHANGE", current + " -> " + target);
        return AppointmentResponse.from(appointment);
    }

    @Transactional
    public AppointmentResponse cancel(UUID id, String reason) {
        Appointment appointment = get(id);
        if (appointment.getStatus() != AppointmentStatus.PENDIENTE
                && appointment.getStatus() != AppointmentStatus.CONFIRMADA) {
            throw new BusinessException("ERR_INVALID_TRANSITION",
                    "Solo se pueden cancelar citas pendientes o confirmadas");
        }
        appointment.setStatus(AppointmentStatus.CANCELADA);
        if (reason != null && !reason.isBlank()) {
            appointment.setReason(reason);
        }
        appointment = appointmentRepository.save(appointment);
        auditService.record("Appointment", id, "CANCEL", "Cita cancelada: " + reason);
        return AppointmentResponse.from(appointment);
    }

    /** Servicio web RQ-CIT-02: historial de citas por cedula. */
    public AppointmentHistoryResponse historyByNationalId(String nationalId) {
        Patient patient = patientRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente no encontrado con cedula: " + nationalId));

        List<Appointment> all = appointmentRepository.findHistoryByPatientNationalId(nationalId);

        return new AppointmentHistoryResponse(
                nationalId,
                patient.getFullName(),
                filter(all, AppointmentStatus.ATENDIDA, AppointmentStatus.CERRADA),
                filter(all, AppointmentStatus.CANCELADA, AppointmentStatus.EXPIRADA),
                filter(all, AppointmentStatus.PENDIENTE, AppointmentStatus.CONFIRMADA),
                List.of());
    }

    private List<AppointmentResponse> filter(List<Appointment> source, AppointmentStatus... statuses) {
        Set<AppointmentStatus> set = Set.of(statuses);
        return source.stream()
                .filter(a -> set.contains(a.getStatus()))
                .map(AppointmentResponse::from)
                .toList();
    }

    private Appointment get(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Cita", id));
    }
}
