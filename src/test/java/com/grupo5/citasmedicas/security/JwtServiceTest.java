package com.grupo5.citasmedicas.security;

import com.grupo5.citasmedicas.enums.Role;
import com.grupo5.citasmedicas.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        // Clave de al menos 64 bytes para HS512.
        String secret = "clave_super_secreta_para_pruebas_unitarias_del_grupo_5_citas_medicas_2026";
        jwtService = new JwtService(secret, 60L, 7L);
        user = User.builder().username("admin").email("a@a.com").passwordHash("h")
                .roles(Set.of(Role.ADMIN)).build();
        user.setId(UUID.randomUUID());
    }

    @Test
    void generaYExtraeUsernameDelAccessToken() {
        String token = jwtService.generateAccessToken(user);
        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void refreshTokenEsValido() {
        String token = jwtService.generateRefreshToken(user);
        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    void tokenInvalidoDevuelveFalse() {
        assertThat(jwtService.isValid("token.invalido.xxx")).isFalse();
    }

    @Test
    void exponeMinutosDeExpiracion() {
        assertThat(jwtService.getAccessTokenMinutes()).isEqualTo(60L);
    }
}
