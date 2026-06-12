package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.model.AccountingAccount;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String codigo,
        String nombre,
        String tipo,
        boolean activa
) {
    public static AccountResponse from(AccountingAccount a) {
        return new AccountResponse(a.getId(), a.getCodigo(), a.getNombre(), a.getTipo(), a.isActiva());
    }
}
