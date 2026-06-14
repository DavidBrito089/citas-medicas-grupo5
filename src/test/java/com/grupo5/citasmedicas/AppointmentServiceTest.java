package com.grupo5.citasmedicas;

import com.grupo5.citasmedicas.dto.request.AppointmentRequest;
import com.grupo5.citasmedicas.enums.AppointmentStatus;
import com.grupo5.citasmedicas.enums.AppointmentType;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.model.Appointment;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.AppointmentRepository;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.PatientRepository;
import com.grupo5.citasmedicas.service.AppointmentService;
import com.grupo5.citasmedicas.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de reglas de negocio de la agenda (DDS - pruebas unitarias):
 * duracion por defecto y bloqueo de solapamientos.
 */
class AppointmentServiceTest {

    private AppointmentRepository appointmentRepository;
    private PatientRepository patientRepository;
    private CollaboratorRepository collaboratorRepository;
    private AppointmentService service;

    private final UUID patientId = UUID.randomUUID();
    private final UUID doctorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        appointmentRepository = Mockito.mock(AppointmentRepository.class);
        patientRepository = Mockito.mock(PatientRepository.class);
        collaboratorRepository = Mockito.mock(CollaboratorRepository.class);
        AuditService auditService = Mockito.mock(AuditService.class);
        service = new AppointmentService(appointmentRepository, patientRepository, collaboratorRepository, auditService);

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setFullName("Juan Perez");
        Collaborator doctor = new Collaborator();
        doctor.setId(doctorId);
        doctor.setFullName("Dra. Ana");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(collaboratorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void agendaUsaDuracionPorDefectoDelTipo() {
        when(appointmentRepository.findOverlapping(any(), any(), any())).thenReturn(List.of());
        AppointmentRequest req = new AppointmentRequest(patientId, doctorId,
                OffsetDateTime.now().plusDays(1), AppointmentType.NORMAL, null, "control");

        var response = service.schedule(req);

        assertEquals(AppointmentType.NORMAL.getDefaultDurationMinutes(), response.durationMinutes());
        assertEquals(AppointmentStatus.PENDIENTE, response.status());
    }

    @Test
    void rechazaCitaSolapada() {
        when(appointmentRepository.findOverlapping(any(), any(), any()))
                .thenReturn(List.of(new Appointment()));
        AppointmentRequest req = new AppointmentRequest(patientId, doctorId,
                OffsetDateTime.now().plusDays(1), AppointmentType.NORMAL, 30, "control");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.schedule(req));
        assertEquals("ERR_OVERLAP", ex.getCode());
    }
}
