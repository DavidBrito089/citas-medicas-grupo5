package com.grupo5.citasmedicas.dto.request;

import com.grupo5.citasmedicas.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres") String password,
        String fullName,
        @NotEmpty Set<Role> roles
) {}
