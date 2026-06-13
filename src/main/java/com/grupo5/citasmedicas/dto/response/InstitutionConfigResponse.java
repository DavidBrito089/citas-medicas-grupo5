package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.model.InstitutionConfig;

import java.util.UUID;

public record InstitutionConfigResponse(
        UUID id,
        String nombre,
        String ruc,
        String direccion,
        String telefono,
        String email,
        String logoUrl,
        String moneda
) {
    public static InstitutionConfigResponse from(InstitutionConfig c) {
        return new InstitutionConfigResponse(c.getId(), c.getNombre(), c.getRuc(), c.getDireccion(),
                c.getTelefono(), c.getEmail(), c.getLogoUrl(), c.getMoneda());
    }
}
