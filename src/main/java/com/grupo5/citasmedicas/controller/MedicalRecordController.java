package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.MedicalRecordRequest;
import com.grupo5.citasmedicas.dto.response.MedicalRecordResponse;
import com.grupo5.citasmedicas.service.MedicalRecordService;
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

@Tag(name = "Historia Clinica", description = "Registro de consultas medicas - RQ-CON-01")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/medical_records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @Operation(summary = "Obtener registro de consulta por id")
    @GetMapping("/{id}")
    public MedicalRecordResponse findById(@PathVariable UUID id) {
        return medicalRecordService.findById(id);
    }

    @Operation(summary = "Historial clinico de un paciente por id")
    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordResponse> findByPatient(@PathVariable UUID patientId) {
        return medicalRecordService.findByPatient(patientId);
    }

    @Operation(summary = "Historial clinico de un paciente por cedula")
    @GetMapping("/cedula/{nationalId}")
    public List<MedicalRecordResponse> findByNationalId(@PathVariable String nationalId) {
        return medicalRecordService.findByPatientNationalId(nationalId);
    }

    @Operation(summary = "Registrar una consulta medica (solo MEDICO/ADMIN)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<MedicalRecordResponse> create(@Valid @RequestBody MedicalRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRecordService.create(request));
    }
}
