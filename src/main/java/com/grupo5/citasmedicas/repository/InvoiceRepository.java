package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByPatientId(UUID patientId);
    long count();
}
