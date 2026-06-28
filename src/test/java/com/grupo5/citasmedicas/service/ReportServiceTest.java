package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.enums.TransactionType;
import com.grupo5.citasmedicas.model.CashJournalEntry;
import com.grupo5.citasmedicas.model.Invoice;
import com.grupo5.citasmedicas.repository.CashJournalEntryRepository;
import com.grupo5.citasmedicas.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReportServiceTest {

    @Mock CashJournalEntryRepository journalRepository;
    @Mock InvoiceRepository invoiceRepository;
    @Mock MedicalRecordService medicalRecordService;
    @InjectMocks ReportService service;

    @Test
    void libroDiarioCalculaTotales() {
        CashJournalEntry ing = CashJournalEntry.builder().tipo(TransactionType.INGRESO).monto(new BigDecimal("100")).build();
        CashJournalEntry egr = CashJournalEntry.builder().tipo(TransactionType.EGRESO).monto(new BigDecimal("40")).build();
        when(journalRepository.findAllByOrderByFechaAsc()).thenReturn(List.of(ing, egr));
        var r = service.libroDiario();
        assertThat(r.get("totalIngresos")).isEqualTo(new BigDecimal("100"));
        assertThat(r.get("totalEgresos")).isEqualTo(new BigDecimal("40"));
        assertThat(r.get("saldo")).isEqualTo(new BigDecimal("60"));
    }

    @Test
    void historiaClinicaDelegaEnServicio() {
        when(medicalRecordService.findByPatientNationalId("0101")).thenReturn(List.of());
        assertThat(service.historiaClinica("0101")).isEmpty();
    }

    @Test
    void resumenComprobantesCuentaYSuma() {
        Invoice inv = Invoice.builder().numero("001").subtotal(BigDecimal.TEN).total(new BigDecimal("33.60"))
                .status(com.grupo5.citasmedicas.enums.InvoiceStatus.EMITIDA)
                .issuedAt(java.time.OffsetDateTime.now()).build();
        inv.setId(java.util.UUID.randomUUID());
        when(invoiceRepository.findAll()).thenReturn(List.of(inv));
        var r = service.resumenComprobantes();
        assertThat(r.get("cantidad")).isEqualTo(1);
        assertThat(r.get("totalFacturado")).isEqualTo(new BigDecimal("33.60"));
    }
}
