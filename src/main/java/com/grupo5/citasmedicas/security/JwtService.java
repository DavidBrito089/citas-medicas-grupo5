package com.grupo5.citasmedicas.security;

import com.grupo5.citasmedicas.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generacion y validacion de JSON Web Tokens (HS256).
 * El DDS contempla RS256/KMS en produccion; aqui se usa HS256 por simplicidad academica.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTokenMinutes;
    private final long refreshTokenDays;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-minutes}") long accessTokenMinutes,
                      @Value("${app.jwt.refresh-token-days}") long refreshTokenDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMinutes = accessTokenMinutes;
        this.refreshTokenDays = refreshTokenDays;
    }

    public String generateAccessToken(User user) {
        List<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toList());
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of(
                        "uid", user.getId().toString(),
                        "roles", roles,
                        "type", "access"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenMinutes * 60_000))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenDays * 86_400_000L))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token) {
        try {
            return !extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return resolver.apply(claims);
    }

    public long getAccessTokenMinutes() {
        return accessTokenMinutes;
    }
}
