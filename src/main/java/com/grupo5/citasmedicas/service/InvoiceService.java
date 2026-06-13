package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.InvoiceItemRequest;
import com.grupo5.citasmedicas.dto.request.InvoiceRequest;
import com.grupo5.citasmedicas.dto.response.InvoiceResponse;
import com.grupo5.citasmedicas.enums.InvoiceStatus;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.Appointment;
import com.grupo5.citasmedicas.model.Invoice;
import com.grupo5.citasmedicas.model.InvoiceItem;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.repository.AppointmentRepository;
import com.grupo5.citasmedicas.repository.InvoiceRepository;
import com.grupo5.citasmedicas.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Emision de comprobantes de venta (RQ-FIN-01). Al emitir, registra
 * automaticamente el ingreso en el libro diario (RQ-FIN-02) y lo asocia a la
 * cuenta contable de ingresos (RQ-FIN-03).
 */
@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AccountingService accountingService;
    private final AuditService auditService;

    public InvoiceService(InvoiceRepository invoiceRepository, PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository, AccountingService accountingService,
                          AuditService auditService) {
        this.invoiceRepository = invoiceRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.accountingService = accountingService;
        this.auditService = auditService;
    }

    public List<InvoiceResponse> findAll() {
        return invoiceRepository.findAll().stream().map(InvoiceResponse::from).toList();
    }

    public InvoiceResponse findById(UUID id) {
        return InvoiceResponse.from(get(id));
    }

    @Transactional
    public InvoiceResponse create(InvoiceRequest req) {
        if (req.items() == null || req.items().isEmpty()) {
            throw new BusinessException("ERR_INVOICE_EMPTY", "El comprobante debe tener al menos un item");
        }

        Patient patient = null;
        if (req.patientId() != null) {
            patient = patientRepository.findById(req.patientId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Paciente", req.patientId()));
        }
        Appointment appointment = null;
        if (req.appointmentId() != null) {
            appointment = appointmentRepository.findById(req.appointmentId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Cita", req.appointmentId()));
        }

        Invoice invoice = Invoice.builder()
                .numero(nextNumber())
                .patient(patient)
                .appointment(appointment)
                .facturarA(req.facturarA())
                .identificacionTercero(req.identificacionTercero())
                .status(InvoiceStatus.EMITIDA)
                .issuedAt(OffsetDateTime.now())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItemRequest itemReq : req.items()) {
            BigDecimal lineTotal = itemReq.precioUnitario()
                    .multiply(BigDecimal.valueOf(itemReq.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            InvoiceItem item = InvoiceItem.builder()
                    .descripcion(itemReq.descripcion())
                    .cantidad(itemReq.cantidad())
                    .precioUnitario(itemReq.precioUnitario())
                    .subtotal(lineTotal)
                    .build();
            invoice.addItem(item);
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal pct = req.porcentajeImpuesto() != null ? req.porcentajeImpuesto() : BigDecimal.ZERO;
        BigDecimal impuestos = subtotal.multiply(pct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        invoice.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        invoice.setImpuestos(impuestos);
        invoice.setTotal(subtotal.add(impuestos).setScale(2, RoundingMode.HALF_UP));

        invoice = invoiceRepository.save(invoice);

        // RQ-FIN-02 / RQ-FIN-03: registro automatico en el diario de caja.
        accountingService.recordInvoiceIncome(invoice, accountingService.defaultIncomeAccount());

        auditService.record("Invoice", invoice.getId(), "CREATE",
                "Comprobante " + invoice.getNumero() + " total " + invoice.getTotal());
        return InvoiceResponse.from(invoice);
    }

    @Transactional
    public InvoiceResponse markPaid(UUID id) {
        Invoice invoice = get(id);
        if (invoice.getStatus() == InvoiceStatus.ANULADA) {
            throw new BusinessException("ERR_INVOICE_VOID", "No se puede pagar un comprobante anulado");
        }
        invoice.setStatus(InvoiceStatus.PAGADA);
        invoice = invoiceRepository.save(invoice);
        auditService.record("Invoice", id, "PAID", "Comprobante pagado");
        return InvoiceResponse.from(invoice);
    }

    private String nextNumber() {
        long seq = invoiceRepository.count() + 1;
        return String.format("001-001-%09d", seq);
    }

    private Invoice get(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Comprobante", id));
    }
}
