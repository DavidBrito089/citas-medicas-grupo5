package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByNationalId(String nationalId);
    boolean existsByNationalId(String nationalId);
    List<Patient> findAllByDeletedAtIsNull();
}
