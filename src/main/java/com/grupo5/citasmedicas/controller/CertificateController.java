package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.CertificateRequest;
import com.grupo5.citasmedicas.dto.response.CertificateResponse;
import com.grupo5.citasmedicas.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Certificados", description = "Certificados medicos imprimibles - RQ-CER-01")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Operation(summary = "Obtener certificado por id (para impresion)")
    @GetMapping("/{id}")
    public CertificateResponse findById(@PathVariable UUID id) {
        return certificateService.findById(id);
    }

    @Operation(summary = "Listar certificados de un paciente")
    @GetMapping("/patient/{patientId}")
    public List<CertificateResponse> findByPatient(@PathVariable UUID patientId) {
        return certificateService.findByPatient(patientId);
    }

    @Operation(summary = "Emitir certificado medico (solo MEDICO/ADMIN)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<CertificateResponse> create(@Valid @RequestBody CertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.create(request));
    }
}
