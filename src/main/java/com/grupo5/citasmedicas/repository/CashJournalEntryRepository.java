package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.CashJournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CashJournalEntryRepository extends JpaRepository<CashJournalEntry, UUID> {
    List<CashJournalEntry> findByFechaBetweenOrderByFechaAsc(OffsetDateTime desde, OffsetDateTime hasta);
    List<CashJournalEntry> findAllByOrderByFechaAsc();
}
