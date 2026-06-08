package com.grupo5.citasmedicas.dto.response;

import java.util.List;

/**
 * Respuesta del servicio web de historial por cedula (RQ-CIT-02):
 * separa las citas en atendidas, canceladas y pendientes.
 */
public record AppointmentHistoryResponse(
        String nationalId,
        String patientName,
        List<AppointmentResponse> atendidas,
        List<AppointmentResponse> canceladas,
        List<AppointmentResponse> pendientes,
        List<AppointmentResponse> otras
) {}
