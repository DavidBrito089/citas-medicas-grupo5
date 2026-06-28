package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.InstitutionConfigRequest;
import com.grupo5.citasmedicas.model.InstitutionConfig;
import com.grupo5.citasmedicas.repository.InstitutionConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InstitutionConfigServiceTest {

    @Mock InstitutionConfigRepository repository;
    @Mock AuditService auditService;
    @InjectMocks InstitutionConfigService service;

    private InstitutionConfigRequest req() {
        return new InstitutionConfigRequest("Clinica G5", "0190000000001", "Cuenca", "072", "i@i.com", null, "USD");
    }

    @Test
    void getDevuelveDefaultSiNoExiste() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.get().nombre()).isEqualTo("Consultorio Medico");
    }

    @Test
    void getDevuelveExistente() {
        when(repository.findAll()).thenReturn(List.of(
                InstitutionConfig.builder().nombre("Clinica G5").moneda("USD").build()));
        assertThat(service.get().nombre()).isEqualTo("Clinica G5");
    }

    @Test
    void saveCreaSiNoExiste() {
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any(InstitutionConfig.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(service.save(req()).nombre()).isEqualTo("Clinica G5");
    }

    @Test
    void saveActualizaExistente() {
        InstitutionConfig existing = InstitutionConfig.builder().nombre("Viejo").moneda("USD").build();
        when(repository.findAll()).thenReturn(List.of(existing));
        when(repository.save(any(InstitutionConfig.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(service.save(req()).ruc()).isEqualTo("0190000000001");
    }
}
