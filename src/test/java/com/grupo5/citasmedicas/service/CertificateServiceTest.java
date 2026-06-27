package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.CertificateRequest;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.MedicalCertificate;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.MedicalCertificateRepository;
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
class CertificateServiceTest {

    @Mock MedicalCertificateRepository certificateRepository;
    @Mock PatientRepository patientRepository;
    @Mock CollaboratorRepository collaboratorRepository;
    @Mock AuditService auditService;
    @InjectMocks CertificateService service;

    private Patient patient(UUID id) {
        Patient p = new Patient(); p.setId(id); p.setFullName("Juan");
        return p;
    }

    @Test
    void createOk() {
        UUID pid = UUID.randomUUID();
        when(patientRepository.findById(pid)).thenReturn(Optional.of(patient(pid)));
        when(certificateRepository.save(any(MedicalCertificate.class))).thenAnswer(i -> {
            MedicalCertificate c = i.getArgument(0); c.setId(UUID.randomUUID()); return c;
        });
        var r = service.create(new CertificateRequest(pid, null, 3, "Reposo por gripe"));
        assertThat(r.restDays()).isEqualTo(3);
        assertThat(r.issueDate()).isNotNull();
    }

    @Test
    void createConMedico() {
        UUID pid = UUID.randomUUID();
        UUID did = UUID.randomUUID();
        Collaborator d = new Collaborator(); d.setId(did); d.setFullName("Dra. Ana");
        when(patientRepository.findById(pid)).thenReturn(Optional.of(patient(pid)));
        when(collaboratorRepository.findById(did)).thenReturn(Optional.of(d));
        when(certificateRepository.save(any(MedicalCertificate.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(service.create(new CertificateRequest(pid, did, null, "texto")).doctorName())
                .isEqualTo("Dra. Ana");
    }

    @Test
    void createLanzaSiPacienteNoExiste() {
        UUID pid = UUID.randomUUID();
        when(patientRepository.findById(pid)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(new CertificateRequest(pid, null, 1, "x")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByPatientYFindById() {
        UUID pid = UUID.randomUUID();
        MedicalCertificate c = MedicalCertificate.builder().patient(patient(pid))
                .issueDate(java.time.LocalDate.now()).contenido("x").build();
        c.setId(UUID.randomUUID());
        when(certificateRepository.findByPatientId(pid)).thenReturn(List.of(c));
        when(certificateRepository.findById(c.getId())).thenReturn(Optional.of(c));
        assertThat(service.findByPatient(pid)).hasSize(1);
        assertThat(service.findById(c.getId()).contenido()).isEqualTo("x");
    }
}
