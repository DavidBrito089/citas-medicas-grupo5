package com.grupo5.citasmedicas.exception;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Formato de error estandar definido en el DDS (T02.02):
 * { "code": "ERR_CODE", "message": "Descripcion", "details": {...} }
 */
public record ApiError(String code, String message, Map<String, Object> details, OffsetDateTime timestamp) {
    public ApiError(String code, String message, Map<String, Object> details) {
        this(code, message, details, OffsetDateTime.now());
    }
}
