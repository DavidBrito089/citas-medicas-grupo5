package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.PatientRequest;
import com.grupo5.citasmedicas.dto.response.PatientResponse;
import com.grupo5.citasmedicas.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Pacientes", description = "Gestion de pacientes - RQ-PAC-01")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "Listar pacientes activos")
    @GetMapping
    public List<PatientResponse> findAll() {
        return patientService.findAll();
    }

    @Operation(summary = "Obtener paciente por id")
    @GetMapping("/{id}")
    public PatientResponse findById(@PathVariable UUID id) {
        return patientService.findById(id);
    }

    @Operation(summary = "Buscar paciente por cedula")
    @GetMapping("/cedula/{nationalId}")
    public PatientResponse findByNationalId(@PathVariable String nationalId) {
        return patientService.findByNationalId(nationalId);
    }

    @Operation(summary = "Registrar paciente")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(request));
    }

    @Operation(summary = "Actualizar paciente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public PatientResponse update(@PathVariable UUID id, @Valid @RequestBody PatientRequest request) {
        return patientService.update(id, request);
    }

    @Operation(summary = "Eliminar paciente (soft-delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
