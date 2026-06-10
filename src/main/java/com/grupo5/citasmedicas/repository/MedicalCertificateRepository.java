package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.MedicalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificate, UUID> {
    List<MedicalCertificate> findByPatientId(UUID patientId);
}
