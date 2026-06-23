package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.response.UserResponse;
import com.grupo5.citasmedicas.enums.Role;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.User;
import com.grupo5.citasmedicas.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock AuditService auditService;
    @InjectMocks UserService userService;

    private User user(UUID id) {
        User u = User.builder().username("u").email("u@u.com").passwordHash("h")
                .roles(Set.of(Role.MEDICO)).build();
        u.setId(id);
        return u;
    }

    @Test
    void findAllMapeaUsuarios() {
        when(userRepository.findAll()).thenReturn(List.of(user(UUID.randomUUID())));
        List<UserResponse> result = userService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("u");
    }

    @Test
    void findByIdDevuelveUsuario() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(user(id)));
        assertThat(userService.findById(id).id()).isEqualTo(id);
    }

    @Test
    void findByIdLanzaSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteEliminaUsuario() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(user(id)));
        userService.delete(id);
        verify(userRepository).delete(any(User.class));
    }
}
