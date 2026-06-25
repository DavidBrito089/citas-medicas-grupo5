package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.CollaboratorRequest;
import com.grupo5.citasmedicas.dto.response.CollaboratorResponse;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.User;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.UserRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CollaboratorServiceTest {

    @Mock CollaboratorRepository collaboratorRepository;
    @Mock UserRepository userRepository;
    @Mock AuditService auditService;
    @InjectMocks CollaboratorService collaboratorService;

    private Collaborator col(UUID id) {
        Collaborator c = Collaborator.builder().fullName("Dra. Ana").build();
        c.setId(id);
        return c;
    }

    private CollaboratorRequest req(UUID userId) {
        return new CollaboratorRequest("Dra. Ana", "0102", "Cardiologia", "MEDICO", "099", "a@a.com", userId);
    }

    @Test
    void findAllOk() {
        when(collaboratorRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(col(UUID.randomUUID())));
        assertThat(collaboratorService.findAll()).hasSize(1);
    }

    @Test
    void findByIdOk() {
        UUID id = UUID.randomUUID();
        when(collaboratorRepository.findById(id)).thenReturn(Optional.of(col(id)));
        assertThat(collaboratorService.findById(id).fullName()).isEqualTo("Dra. Ana");
    }

    @Test
    void createSinUsuarioOk() {
        when(collaboratorRepository.save(any(Collaborator.class))).thenAnswer(i -> {
            Collaborator c = i.getArgument(0); c.setId(UUID.randomUUID()); return c;
        });
        CollaboratorResponse r = collaboratorService.create(req(null));
        assertThat(r.fullName()).isEqualTo("Dra. Ana");
        assertThat(r.userId()).isNull();
    }

    @Test
    void createConUsuarioResuelveUsuario() {
        UUID uid = UUID.randomUUID();
        User u = new User(); u.setId(uid);
        when(userRepository.findById(uid)).thenReturn(Optional.of(u));
        when(collaboratorRepository.save(any(Collaborator.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(collaboratorService.create(req(uid)).userId()).isEqualTo(uid);
    }

    @Test
    void createConUsuarioInexistenteLanza() {
        UUID uid = UUID.randomUUID();
        when(userRepository.findById(uid)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> collaboratorService.create(req(uid)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateOk() {
        UUID id = UUID.randomUUID();
        when(collaboratorRepository.findById(id)).thenReturn(Optional.of(col(id)));
        when(collaboratorRepository.save(any(Collaborator.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(collaboratorService.update(id, req(null)).specialty()).isEqualTo("Cardiologia");
    }

    @Test
    void deleteHaceSoftDelete() {
        UUID id = UUID.randomUUID();
        Collaborator c = col(id);
        when(collaboratorRepository.findById(id)).thenReturn(Optional.of(c));
        collaboratorService.delete(id);
        assertThat(c.getDeletedAt()).isNotNull();
        verify(collaboratorRepository).save(c);
    }
}
