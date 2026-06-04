package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.LoginRequest;
import com.grupo5.citasmedicas.dto.request.RefreshRequest;
import com.grupo5.citasmedicas.dto.request.RegisterRequest;
import com.grupo5.citasmedicas.dto.response.AuthResponse;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.User;
import com.grupo5.citasmedicas.repository.UserRepository;
import com.grupo5.citasmedicas.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       AuthenticationManager authenticationManager, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.auditService = auditService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("ERR_USERNAME_TAKEN", "El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("ERR_EMAIL_TAKEN", "El email ya esta registrado");
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .roles(request.roles())
                .build();
        user = userRepository.save(user);
        auditService.record("User", user.getId(), "CREATE", "Registro de usuario " + user.getUsername());
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        if (!jwtService.isValid(request.refreshToken())) {
            throw new BusinessException("ERR_TOKEN_INVALID", "Refresh token invalido o expirado");
        }
        String username = jwtService.extractUsername(request.refreshToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                "Bearer",
                jwtService.getAccessTokenMinutes() * 60,
                user.getUsername(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
    }
}
