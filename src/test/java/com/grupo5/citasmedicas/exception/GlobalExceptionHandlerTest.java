package com.grupo5.citasmedicas.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFoundDevuelve404() {
        ResponseEntity<ApiError> r = handler.handleNotFound(new ResourceNotFoundException("no existe"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(r.getBody().code()).isEqualTo("ERR_NOT_FOUND");
    }

    @Test
    void businessDevuelve409ConCodigo() {
        ResponseEntity<ApiError> r = handler.handleBusiness(new BusinessException("ERR_X", "msg"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(r.getBody().code()).isEqualTo("ERR_X");
    }

    @Test
    void optimisticLockDevuelve409() {
        ResponseEntity<ApiError> r = handler.handleOptimisticLock(new OptimisticLockingFailureException("x"));
        assertThat(r.getBody().code()).isEqualTo("ERR_CONCURRENCY");
    }

    @Test
    void badCredentialsDevuelve401() {
        ResponseEntity<ApiError> r = handler.handleBadCredentials(new BadCredentialsException("x"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void accessDeniedDevuelve403() {
        ResponseEntity<ApiError> r = handler.handleAccessDenied(new AccessDeniedException("x"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void illegalArgumentDevuelve400() {
        ResponseEntity<ApiError> r = handler.handleIllegalArgument(new IllegalArgumentException("malo"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void genericDevuelve500() {
        ResponseEntity<ApiError> r = handler.handleGeneric(new RuntimeException("boom"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(r.getBody().code()).isEqualTo("ERR_INTERNAL");
    }
}
