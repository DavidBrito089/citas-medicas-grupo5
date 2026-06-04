package com.grupo5.citasmedicas.dto.response;

import java.util.Set;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        String username,
        Set<String> roles
) {}
