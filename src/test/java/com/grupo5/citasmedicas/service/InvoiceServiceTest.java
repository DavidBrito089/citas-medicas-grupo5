package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.InvoiceItemRequest;
import com.grupo5.citasmedicas.dto.request.InvoiceRequest;
import com.grupo5.citasmedicas.dto.response.InvoiceResponse;
import com.grupo5.citasmedicas.enums.InvoiceStatus;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.model.Invoice;
import com.grupo5.citasmedicas.repository.AppointmentRepository;
import com.grupo5.citasmedicas.repository.InvoiceRepository;
import com.grupo5.citasmedicas.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InvoiceServiceTest {

    @Mock InvoiceRepository invoiceRepository;
    @Mock PatientRepository patientRepository;
    @Mock AppointmentRepository appointmentRepository;
    @Mock AccountingService accountingService;
    @Mock AuditService auditService;
    @InjectMocks InvoiceService service;

    private InvoiceRequest req(BigDecimal iva) {
        return new InvoiceRequest(null, null, "Empresa XYZ", "0990000001", iva,
                List.of(new InvoiceItemRequest("Consulta", 2, new BigDecimal("30.00"))));
    }

    @Test
    void createCalculaSubtotalImpuestosYTotal() {
        when(invoiceRepository.count()).thenReturn(0L);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> {
            Invoice inv = i.getArgument(0); inv.setId(UUID.randomUUID()); return inv;
        });
        InvoiceResponse r = service.create(req(new BigDecimal("12")));
        // subtotal = 2 * 30 = 60 ; iva 12% = 7.20 ; total = 67.20
        assertThat(r.subtotal()).isEqualByComparingTo("60.00");
        assertThat(r.impuestos()).isEqualByComparingTo("7.20");
        assertThat(r.total()).isEqualByComparingTo("67.20");
        assertThat(r.status()).isEqualTo(InvoiceStatus.EMITIDA);
        // registra el ingreso en el diario (RQ-FIN-02)
        verify(accountingService).recordInvoiceIncome(any(Invoice.class), any());
    }

    @Test
    void createSinImpuestoUsaCero() {
        when(invoiceRepository.count()).thenReturn(5L);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));
        InvoiceResponse r = service.create(req(null));
        assertThat(r.impuestos()).isEqualByComparingTo("0.00");
        assertThat(r.total()).isEqualByComparingTo("60.00");
        assertThat(r.numero()).isEqualTo("001-001-000000006");
    }

    @Test
    void createFallaSiSinItems() {
        InvoiceRequest sinItems = new InvoiceRequest(null, null, null, null, null, List.of());
        assertThatThrownBy(() -> service.create(sinItems)).isInstanceOf(BusinessException.class);
    }

    @Test
    void markPaidCambiaEstado() {
        UUID id = UUID.randomUUID();
        Invoice inv = Invoice.builder().numero("001").subtotal(BigDecimal.TEN).total(BigDecimal.TEN)
                .status(InvoiceStatus.EMITIDA).issuedAt(java.time.OffsetDateTime.now()).build();
        inv.setId(id);
        when(invoiceRepository.findById(id)).thenReturn(Optional.of(inv));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));
        assertThat(service.markPaid(id).status()).isEqualTo(InvoiceStatus.PAGADA);
    }

    @Test
    void markPaidFallaSiAnulada() {
        UUID id = UUID.randomUUID();
        Invoice inv = Invoice.builder().numero("001").subtotal(BigDecimal.TEN).total(BigDecimal.TEN)
                .status(InvoiceStatus.ANULADA).issuedAt(java.time.OffsetDateTime.now()).build();
        inv.setId(id);
        when(invoiceRepository.findById(id)).thenReturn(Optional.of(inv));
        assertThatThrownBy(() -> service.markPaid(id)).isInstanceOf(BusinessException.class);
    }

    @Test
    void findAllOk() {
        Invoice inv = Invoice.builder().numero("001").subtotal(BigDecimal.TEN).total(BigDecimal.TEN)
                .status(InvoiceStatus.EMITIDA).issuedAt(java.time.OffsetDateTime.now()).build();
        inv.setId(UUID.randomUUID());
        when(invoiceRepository.findAll()).thenReturn(List.of(inv));
        assertThat(service.findAll()).hasSize(1);
    }
}
