package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {
    List<MedicalRecord> findByPatientIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID patientId);
    List<MedicalRecord> findByPatientNationalIdAndDeletedAtIsNullOrderByCreatedAtDesc(String nationalId);
}
