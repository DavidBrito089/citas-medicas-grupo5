package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.InvoiceRequest;
import com.grupo5.citasmedicas.dto.response.InvoiceResponse;
import com.grupo5.citasmedicas.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Comprobantes", description = "Emision de comprobantes de venta - RQ-FIN-01 / RQ-PAC-02")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "Listar comprobantes")
    @GetMapping
    public List<InvoiceResponse> findAll() {
        return invoiceService.findAll();
    }

    @Operation(summary = "Obtener comprobante por id")
    @GetMapping("/{id}")
    public InvoiceResponse findById(@PathVariable UUID id) {
        return invoiceService.findById(id);
    }

    @Operation(summary = "Emitir comprobante de venta (registra ingreso en el diario)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(request));
    }

    @Operation(summary = "Marcar comprobante como pagado")
    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public InvoiceResponse markPaid(@PathVariable UUID id) {
        return invoiceService.markPaid(id);
    }
}
