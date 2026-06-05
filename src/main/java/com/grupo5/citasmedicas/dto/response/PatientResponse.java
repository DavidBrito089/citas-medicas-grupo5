package com.grupo5.citasmedicas.dto.response;

import com.grupo5.citasmedicas.model.Patient;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String nationalId,
        String fullName,
        LocalDate birthDate,
        String gender,
        String phone,
        String email,
        String address
) {
    public static PatientResponse from(Patient p) {
        return new PatientResponse(p.getId(), p.getNationalId(), p.getFullName(),
                p.getBirthDate(), p.getGender(), p.getPhone(), p.getEmail(), p.getAddress());
    }
}
