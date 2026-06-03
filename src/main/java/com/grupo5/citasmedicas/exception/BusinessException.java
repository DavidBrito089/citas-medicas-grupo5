package com.grupo5.citasmedicas.exception;

/**
 * Excepcion para violaciones de reglas de negocio (ej. solapamiento de citas,
 * transicion de estado invalida).
 */
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
