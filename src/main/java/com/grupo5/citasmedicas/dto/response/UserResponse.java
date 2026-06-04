package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.enums.Role;
import com.grupo5.citasmedicas.model.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        Set<Role> roles
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getFullName(),
                u.getRoles().stream().collect(Collectors.toSet()));
    }
}
