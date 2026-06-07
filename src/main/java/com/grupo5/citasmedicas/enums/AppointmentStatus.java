package com.grupo5.citasmedicas.enums;

/**
 * Maquina de estados de la cita segun el DDS (T02.02):
 * PENDIENTE -> CONFIRMADA -> ATENDIDA -> CERRADA
 *                       \-> CANCELADA
 * PENDIENTE -> EXPIRADA (timeout sin confirmacion)
 */
public enum AppointmentStatus {
    PENDIENTE,
    CONFIRMADA,
    ATENDIDA,
    CERRADA,
    CANCELADA,
    EXPIRADA
}
