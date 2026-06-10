package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.PrescriptionRequest;
import com.grupo5.citasmedicas.dto.response.PrescriptionResponse;
import com.grupo5.citasmedicas.service.PrescriptionService;
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

@Tag(name = "Prescripciones", description = "Recetas medicas y ordenes de examenes - RQ-CON-02")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @Operation(summary = "Listar prescripciones de una consulta")
    @GetMapping("/medical-record/{medicalRecordId}")
    public List<PrescriptionResponse> findByMedicalRecord(@PathVariable UUID medicalRecordId) {
        return prescriptionService.findByMedicalRecord(medicalRecordId);
    }

    @Operation(summary = "Emitir prescripcion u orden de examen (solo MEDICO/ADMIN)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<PrescriptionResponse> create(@Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.create(request));
    }
}
