package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.model.Collaborator;

import java.util.UUID;

public record CollaboratorResponse(
        UUID id,
        String fullName,
        String nationalId,
        String specialty,
        String position,
        String phone,
        String email,
        UUID userId
) {
    public static CollaboratorResponse from(Collaborator c) {
        return new CollaboratorResponse(c.getId(), c.getFullName(), c.getNationalId(),
                c.getSpecialty(), c.getPosition(), c.getPhone(), c.getEmail(),
                c.getUser() != null ? c.getUser().getId() : null);
    }
}
