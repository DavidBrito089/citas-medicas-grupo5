package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.LoginRequest;
import com.grupo5.citasmedicas.dto.request.RefreshRequest;
import com.grupo5.citasmedicas.dto.request.RegisterRequest;
import com.grupo5.citasmedicas.dto.response.AuthResponse;
import com.grupo5.citasmedicas.enums.Role;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.model.User;
import com.grupo5.citasmedicas.repository.UserRepository;
import com.grupo5.citasmedicas.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;
    @Mock AuditService auditService;
    @InjectMocks AuthService authService;

    private User sampleUser() {
        User u = User.builder()
                .username("admin").email("a@a.com").passwordHash("hash")
                .roles(Set.of(Role.ADMIN)).build();
        u.setId(UUID.randomUUID());
        return u;
    }

    @Test
    void registerCreaUsuarioYDevuelveTokens() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("a@a.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0); u.setId(UUID.randomUUID()); return u;
        });
        when(jwtService.generateAccessToken(any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh");
        when(jwtService.getAccessTokenMinutes()).thenReturn(60L);

        AuthResponse resp = authService.register(new RegisterRequest(
                "admin", "a@a.com", "secret", "Admin", Set.of(Role.ADMIN)));

        assertThat(resp.accessToken()).isEqualTo("access");
        assertThat(resp.roles()).contains("ADMIN");
    }

    @Test
    void registerFallaSiUsernameExiste() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(new RegisterRequest(
                "admin", "a@a.com", "secret", "Admin", Set.of(Role.ADMIN))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void registerFallaSiEmailExiste() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("a@a.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(new RegisterRequest(
                "admin", "a@a.com", "secret", "Admin", Set.of(Role.ADMIN))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void loginDevuelveTokens() {
        User u = sampleUser();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(u));
        when(jwtService.generateAccessToken(u)).thenReturn("access");
        when(jwtService.generateRefreshToken(u)).thenReturn("refresh");
        when(jwtService.getAccessTokenMinutes()).thenReturn(60L);

        AuthResponse resp = authService.login(new LoginRequest("admin", "secret"));
        assertThat(resp.username()).isEqualTo("admin");
        assertThat(resp.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void refreshConTokenValidoDevuelveTokens() {
        User u = sampleUser();
        when(jwtService.isValid("rt")).thenReturn(true);
        when(jwtService.extractUsername("rt")).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(u));
        when(jwtService.generateAccessToken(u)).thenReturn("access");
        when(jwtService.generateRefreshToken(u)).thenReturn("refresh");
        when(jwtService.getAccessTokenMinutes()).thenReturn(60L);

        AuthResponse resp = authService.refresh(new RefreshRequest("rt"));
        assertThat(resp.accessToken()).isEqualTo("access");
    }

    @Test
    void refreshFallaSiTokenInvalido() {
        when(jwtService.isValid("rt")).thenReturn(false);
        assertThatThrownBy(() -> authService.refresh(new RefreshRequest("rt")))
                .isInstanceOf(BusinessException.class);
    }
}
