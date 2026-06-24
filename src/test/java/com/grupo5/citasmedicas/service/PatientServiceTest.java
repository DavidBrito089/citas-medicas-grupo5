package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.PatientRequest;
import com.grupo5.citasmedicas.dto.response.PatientResponse;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PatientServiceTest {

    @Mock PatientRepository patientRepository;
    @Mock AuditService auditService;
    @InjectMocks PatientService patientService;

    private Patient patient(UUID id) {
        Patient p = Patient.builder().nationalId("0101").fullName("Juan").build();
        p.setId(id);
        return p;
    }

    private PatientRequest req() {
        return new PatientRequest("0101", "Juan", LocalDate.of(1990, 1, 1), "M", "099", "j@j.com", "Cuenca");
    }

    @Test
    void findAllDevuelveActivos() {
        when(patientRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(patient(UUID.randomUUID())));
        assertThat(patientService.findAll()).hasSize(1);
    }

    @Test
    void findByIdOk() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient(id)));
        assertThat(patientService.findById(id).fullName()).isEqualTo("Juan");
    }

    @Test
    void findByNationalIdOk() {
        when(patientRepository.findByNationalId("0101")).thenReturn(Optional.of(patient(UUID.randomUUID())));
        assertThat(patientService.findByNationalId("0101").nationalId()).isEqualTo("0101");
    }

    @Test
    void findByNationalIdLanzaSiNoExiste() {
        when(patientRepository.findByNationalId("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> patientService.findByNationalId("x"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createOk() {
        when(patientRepository.existsByNationalId("0101")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> {
            Patient p = i.getArgument(0); p.setId(UUID.randomUUID()); return p;
        });
        PatientResponse r = patientService.create(req());
        assertThat(r.fullName()).isEqualTo("Juan");
    }

    @Test
    void createFallaSiCedulaDuplicada() {
        when(patientRepository.existsByNationalId("0101")).thenReturn(true);
        assertThatThrownBy(() -> patientService.create(req()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateOk() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient(id)));
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(patientService.update(id, req()).fullName()).isEqualTo("Juan");
    }

    @Test
    void deleteHaceSoftDelete() {
        UUID id = UUID.randomUUID();
        Patient p = patient(id);
        when(patientRepository.findById(id)).thenReturn(Optional.of(p));
        patientService.delete(id);
        assertThat(p.getDeletedAt()).isNotNull();
        verify(patientRepository).save(p);
    }
}
