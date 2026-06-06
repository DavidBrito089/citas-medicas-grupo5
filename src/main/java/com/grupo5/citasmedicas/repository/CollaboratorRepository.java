package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CollaboratorRepository extends JpaRepository<Collaborator, UUID> {
    List<Collaborator> findAllByDeletedAtIsNull();
}
