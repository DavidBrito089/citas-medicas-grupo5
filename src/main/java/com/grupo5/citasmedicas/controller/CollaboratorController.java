package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.CollaboratorRequest;
import com.grupo5.citasmedicas.dto.response.CollaboratorResponse;
import com.grupo5.citasmedicas.service.CollaboratorService;
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

@Tag(name = "Colaboradores", description = "Gestion de medicos y colaboradores - RQ-COL-01")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/collaborators")
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    public CollaboratorController(CollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    @Operation(summary = "Listar colaboradores activos")
    @GetMapping
    public List<CollaboratorResponse> findAll() {
        return collaboratorService.findAll();
    }

    @Operation(summary = "Obtener colaborador por id")
    @GetMapping("/{id}")
    public CollaboratorResponse findById(@PathVariable UUID id) {
        return collaboratorService.findById(id);
    }

    @Operation(summary = "Registrar colaborador")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CollaboratorResponse> create(@Valid @RequestBody CollaboratorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaboratorService.create(request));
    }

    @Operation(summary = "Actualizar colaborador")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CollaboratorResponse update(@PathVariable UUID id, @Valid @RequestBody CollaboratorRequest request) {
        return collaboratorService.update(id, request);
    }

    @Operation(summary = "Eliminar colaborador (soft-delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        collaboratorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
