package com.grupo5.citasmedicas.enums;

/**
 * Tipos de cita con su duracion por defecto en minutos (DDS - reglas de negocio).
 */
public enum AppointmentType {
    NORMAL(30),
    URGENCIA(20),
    TELECONSULTA(25);

    private final int defaultDurationMinutes;

    AppointmentType(int defaultDurationMinutes) {
        this.defaultDurationMinutes = defaultDurationMinutes;
    }

    public int getDefaultDurationMinutes() {
        return defaultDurationMinutes;
    }
}
