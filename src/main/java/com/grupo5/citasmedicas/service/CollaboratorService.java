package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.CollaboratorRequest;
import com.grupo5.citasmedicas.dto.response.CollaboratorResponse;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.User;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Gestion de medicos y colaboradores del consultorio (RQ-COL-01).
 */
@Service
public class CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public CollaboratorService(CollaboratorRepository collaboratorRepository, UserRepository userRepository,
                               AuditService auditService) {
        this.collaboratorRepository = collaboratorRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    public List<CollaboratorResponse> findAll() {
        return collaboratorRepository.findAllByDeletedAtIsNull().stream().map(CollaboratorResponse::from).toList();
    }

    public CollaboratorResponse findById(UUID id) {
        return CollaboratorResponse.from(get(id));
    }

    @Transactional
    public CollaboratorResponse create(CollaboratorRequest req) {
        Collaborator c = Collaborator.builder()
                .fullName(req.fullName())
                .nationalId(req.nationalId())
                .specialty(req.specialty())
                .position(req.position())
                .phone(req.phone())
                .email(req.email())
                .user(resolveUser(req.userId()))
                .build();
        c = collaboratorRepository.save(c);
        auditService.record("Collaborator", c.getId(), "CREATE", "Colaborador " + c.getFullName());
        return CollaboratorResponse.from(c);
    }

    @Transactional
    public CollaboratorResponse update(UUID id, CollaboratorRequest req) {
        Collaborator c = get(id);
        c.setFullName(req.fullName());
        c.setNationalId(req.nationalId());
        c.setSpecialty(req.specialty());
        c.setPosition(req.position());
        c.setPhone(req.phone());
        c.setEmail(req.email());
        c.setUser(resolveUser(req.userId()));
        c = collaboratorRepository.save(c);
        auditService.record("Collaborator", c.getId(), "UPDATE", "Actualizacion de colaborador");
        return CollaboratorResponse.from(c);
    }

    @Transactional
    public void delete(UUID id) {
        Collaborator c = get(id);
        c.setDeletedAt(OffsetDateTime.now());
        collaboratorRepository.save(c);
        auditService.record("Collaborator", id, "DELETE", "Soft-delete de colaborador");
    }

    private User resolveUser(UUID userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", userId));
    }

    private Collaborator get(UUID id) {
        return collaboratorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Colaborador", id));
    }
}
