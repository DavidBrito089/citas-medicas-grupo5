package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.PrescriptionRequest;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.MedicalRecord;
import com.grupo5.citasmedicas.model.Prescription;
import com.grupo5.citasmedicas.repository.MedicalRecordRepository;
import com.grupo5.citasmedicas.repository.PrescriptionRepository;
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
class PrescriptionServiceTest {

    @Mock PrescriptionRepository prescriptionRepository;
    @Mock MedicalRecordRepository recordRepository;
    @Mock AuditService auditService;
    @InjectMocks PrescriptionService service;

    private MedicalRecord record(UUID id) {
        MedicalRecord m = new MedicalRecord(); m.setId(id);
        return m;
    }

    @Test
    void createOk() {
        UUID rid = UUID.randomUUID();
        when(recordRepository.findById(rid)).thenReturn(Optional.of(record(rid)));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(i -> {
            Prescription p = i.getArgument(0); p.setId(UUID.randomUUID()); return p;
        });
        var r = service.create(new PrescriptionRequest(rid, "RECETA", "Paracetamol", "cada 8h"));
        assertThat(r.tipo()).isEqualTo("RECETA");
        assertThat(r.detalle()).isEqualTo("Paracetamol");
    }

    @Test
    void createLanzaSiRecordNoExiste() {
        UUID rid = UUID.randomUUID();
        when(recordRepository.findById(rid)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(new PrescriptionRequest(rid, "RECETA", "x", "y")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByMedicalRecordOk() {
        UUID rid = UUID.randomUUID();
        Prescription p = Prescription.builder().medicalRecord(record(rid)).tipo("RECETA").detalle("x").build();
        p.setId(UUID.randomUUID());
        when(prescriptionRepository.findByMedicalRecordId(rid)).thenReturn(List.of(p));
        assertThat(service.findByMedicalRecord(rid)).hasSize(1);
    }
}
