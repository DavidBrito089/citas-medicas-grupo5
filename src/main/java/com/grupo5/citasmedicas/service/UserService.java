package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.response.UserResponse;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.User;
import com.grupo5.citasmedicas.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Gestion de usuarios y perfiles de acceso (RQ-CONF-01).
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse findById(UUID id) {
        return UserResponse.from(get(id));
    }

    @Transactional
    public void delete(UUID id) {
        User user = get(id);
        userRepository.delete(user);
        auditService.record("User", id, "DELETE", "Eliminacion de usuario " + user.getUsername());
    }

    private User get(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", id));
    }
}
