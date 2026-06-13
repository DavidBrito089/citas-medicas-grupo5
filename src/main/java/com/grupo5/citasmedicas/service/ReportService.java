package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.response.InvoiceResponse;
import com.grupo5.citasmedicas.dto.response.JournalEntryResponse;
import com.grupo5.citasmedicas.dto.response.MedicalRecordResponse;
import com.grupo5.citasmedicas.enums.TransactionType;
import com.grupo5.citasmedicas.model.CashJournalEntry;
import com.grupo5.citasmedicas.repository.CashJournalEntryRepository;
import com.grupo5.citasmedicas.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Reportes exportables: Libro Diario, Historia Clinica por paciente y
 * Resumen de comprobantes de venta (RQ-REP-01).
 */
@Service
public class ReportService {

    private final CashJournalEntryRepository journalRepository;
    private final InvoiceRepository invoiceRepository;
    private final MedicalRecordService medicalRecordService;

    public ReportService(CashJournalEntryRepository journalRepository, InvoiceRepository invoiceRepository,
                         MedicalRecordService medicalRecordService) {
        this.journalRepository = journalRepository;
        this.invoiceRepository = invoiceRepository;
        this.medicalRecordService = medicalRecordService;
    }

    /** Reporte del Libro Diario con totales de ingresos y egresos. */
    public Map<String, Object> libroDiario() {
        List<CashJournalEntry> entries = journalRepository.findAllByOrderByFechaAsc();
        BigDecimal ingresos = entries.stream()
                .filter(e -> e.getTipo() == TransactionType.INGRESO)
                .map(CashJournalEntry::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal egresos = entries.stream()
                .filter(e -> e.getTipo() == TransactionType.EGRESO)
                .map(CashJournalEntry::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of(
                "movimientos", entries.stream().map(JournalEntryResponse::from).toList(),
                "totalIngresos", ingresos,
                "totalEgresos", egresos,
                "saldo", ingresos.subtract(egresos));
    }

    /** Historia clinica completa de un paciente por cedula. */
    public List<MedicalRecordResponse> historiaClinica(String nationalId) {
        return medicalRecordService.findByPatientNationalId(nationalId);
    }

    /** Resumen de comprobantes de venta. */
    public Map<String, Object> resumenComprobantes() {
        List<InvoiceResponse> comprobantes = invoiceRepository.findAll().stream()
                .map(InvoiceResponse::from).toList();
        BigDecimal total = comprobantes.stream()
                .map(InvoiceResponse::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of(
                "comprobantes", comprobantes,
                "cantidad", comprobantes.size(),
                "totalFacturado", total);
    }
}
