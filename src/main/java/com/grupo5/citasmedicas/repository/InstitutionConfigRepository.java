package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.InstitutionConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstitutionConfigRepository extends JpaRepository<InstitutionConfig, UUID> {
}
