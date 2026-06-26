package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.MedicalRecordRequest;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.MedicalRecord;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.AppointmentRepository;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.MedicalRecordRepository;
import com.grupo5.citasmedicas.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MedicalRecordServiceTest {

    @Mock MedicalRecordRepository recordRepository;
    @Mock PatientRepository patientRepository;
    @Mock CollaboratorRepository collaboratorRepository;
    @Mock AppointmentRepository appointmentRepository;
    @Mock AuditService auditService;
    @InjectMocks MedicalRecordService service;

    private Patient patient(UUID id) {
        Patient p = new Patient(); p.setId(id); p.setFullName("Juan"); p.setNationalId("0101");
        return p;
    }

    @Test
    void createSoloConPaciente() {
        UUID pid = UUID.randomUUID();
        when(patientRepository.findById(pid)).thenReturn(Optional.of(patient(pid)));
        when(recordRepository.save(any(MedicalRecord.class))).thenAnswer(i -> {
            MedicalRecord m = i.getArgument(0); m.setId(UUID.randomUUID()); return m;
        });
        var r = service.create(new MedicalRecordRequest(pid, null, null, "tos", "gripe", "reposo", "ok"));
        assertThat(r.diagnostico()).isEqualTo("gripe");
        assertThat(r.patientName()).isEqualTo("Juan");
    }

    @Test
    void createConMedico() {
        UUID pid = UUID.randomUUID();
        UUID did = UUID.randomUUID();
        Collaborator d = new Collaborator(); d.setId(did); d.setFullName("Dra. Ana");
        when(patientRepository.findById(pid)).thenReturn(Optional.of(patient(pid)));
        when(collaboratorRepository.findById(did)).thenReturn(Optional.of(d));
        when(recordRepository.save(any(MedicalRecord.class))).thenAnswer(i -> i.getArgument(0));
        var r = service.create(new MedicalRecordRequest(pid, did, null, "x", "y", "z", "w"));
        assertThat(r.doctorName()).isEqualTo("Dra. Ana");
    }

    @Test
    void createLanzaSiPacienteNoExiste() {
        UUID pid = UUID.randomUUID();
        when(patientRepository.findById(pid)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(new MedicalRecordRequest(pid, null, null, "x", "y", "z", "w")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByPatientYPorCedula() {
        UUID pid = UUID.randomUUID();
        MedicalRecord m = MedicalRecord.builder().patient(patient(pid)).diagnostico("d").build();
        m.setId(UUID.randomUUID());
        when(recordRepository.findByPatientIdAndDeletedAtIsNullOrderByCreatedAtDesc(pid)).thenReturn(List.of(m));
        when(recordRepository.findByPatientNationalIdAndDeletedAtIsNullOrderByCreatedAtDesc("0101")).thenReturn(List.of(m));
        assertThat(service.findByPatient(pid)).hasSize(1);
        assertThat(service.findByPatientNationalId("0101")).hasSize(1);
    }

    @Test
    void findByIdLanzaSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(recordRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ResourceNotFoundException.class);
    }
}
