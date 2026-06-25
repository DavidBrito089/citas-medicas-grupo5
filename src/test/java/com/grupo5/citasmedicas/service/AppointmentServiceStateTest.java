package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.response.AppointmentHistoryResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppointmentServiceStateTest {

    @Mock AppointmentRepository appointmentRepository;
    @Mock PatientRepository patientRepository;
    @Mock CollaboratorRepository collaboratorRepository;
    @Mock AuditService auditService;
    @InjectMocks AppointmentService service;

    private Appointment appt(AppointmentStatus status) {
        Patient p = new Patient(); p.setId(UUID.randomUUID()); p.setFullName("Juan"); p.setNationalId("0101");
        Collaborator d = new Collaborator(); d.setId(UUID.randomUUID()); d.setFullName("Dra. Ana");
        Appointment a = Appointment.builder()
                .patient(p).doctor(d).startsAt(OffsetDateTime.now()).endsAt(OffsetDateTime.now().plusMinutes(30))
                .durationMinutes(30).type(AppointmentType.NORMAL).status(status).build();
        a.setId(UUID.randomUUID());
        return a;
    }

    @Test
    void changeStatusValidoPendienteAConfirmada() {
        Appointment a = appt(AppointmentStatus.PENDIENTE);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(service.changeStatus(a.getId(), AppointmentStatus.CONFIRMADA).status())
                .isEqualTo(AppointmentStatus.CONFIRMADA);
    }

    @Test
    void changeStatusInvalidoLanza() {
        Appointment a = appt(AppointmentStatus.ATENDIDA);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        assertThatThrownBy(() -> service.changeStatus(a.getId(), AppointmentStatus.PENDIENTE))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void cancelOk() {
        Appointment a = appt(AppointmentStatus.PENDIENTE);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(service.cancel(a.getId(), "no asistio").status()).isEqualTo(AppointmentStatus.CANCELADA);
    }

    @Test
    void cancelFallaSiYaAtendida() {
        Appointment a = appt(AppointmentStatus.ATENDIDA);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        assertThatThrownBy(() -> service.cancel(a.getId(), "x"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void findByIdLanzaSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findAllYFindByDoctorMapean() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appt(AppointmentStatus.PENDIENTE)));
        when(appointmentRepository.findByDoctorIdAndDeletedAtIsNull(any())).thenReturn(List.of(appt(AppointmentStatus.PENDIENTE)));
        assertThat(service.findAll()).hasSize(1);
        assertThat(service.findByDoctor(UUID.randomUUID())).hasSize(1);
    }

    @Test
    void historialPorCedulaSeparaPorEstado() {
        Patient p = new Patient(); p.setNationalId("0101"); p.setFullName("Juan");
        when(patientRepository.findByNationalId("0101")).thenReturn(Optional.of(p));
        when(appointmentRepository.findHistoryByPatientNationalId("0101")).thenReturn(List.of(
                appt(AppointmentStatus.ATENDIDA), appt(AppointmentStatus.CANCELADA), appt(AppointmentStatus.PENDIENTE)));
        AppointmentHistoryResponse h = service.historyByNationalId("0101");
        assertThat(h.atendidas()).hasSize(1);
        assertThat(h.canceladas()).hasSize(1);
        assertThat(h.pendientes()).hasSize(1);
    }

    @Test
    void historialLanzaSiPacienteNoExiste() {
        when(patientRepository.findByNationalId("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.historyByNationalId("x"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
