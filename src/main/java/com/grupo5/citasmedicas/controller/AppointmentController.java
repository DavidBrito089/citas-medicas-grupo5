package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.AppointmentRequest;
import com.grupo5.citasmedicas.dto.response.AppointmentHistoryResponse;
import com.grupo5.citasmedicas.dto.response.AppointmentResponse;
import com.grupo5.citasmedicas.enums.AppointmentStatus;
import com.grupo5.citasmedicas.service.AppointmentService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Citas", description = "Agenda de citas, maquina de estados e historial - RQ-CIT-01 / RQ-CIT-02")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Listar todas las citas")
    @GetMapping
    public List<AppointmentResponse> findAll() {
        return appointmentService.findAll();
    }

    @Operation(summary = "Obtener cita por id")
    @GetMapping("/{id}")
    public AppointmentResponse findById(@PathVariable UUID id) {
        return appointmentService.findById(id);
    }

    @Operation(summary = "Listar agenda de un medico")
    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentResponse> findByDoctor(@PathVariable UUID doctorId) {
        return appointmentService.findByDoctor(doctorId);
    }

    @Operation(summary = "RQ-CIT-02: Historial de citas por cedula (atendidas, canceladas, pendientes)")
    @GetMapping("/historial/{nationalId}")
    public AppointmentHistoryResponse history(@PathVariable String nationalId) {
        return appointmentService.historyByNationalId(nationalId);
    }

    @Operation(summary = "Agendar una nueva cita (estado inicial PENDIENTE)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA','MEDICO')")
    public ResponseEntity<AppointmentResponse> schedule(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.schedule(request));
    }

    @Operation(summary = "Cambiar el estado de una cita segun la maquina de estados")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA','MEDICO')")
    public AppointmentResponse changeStatus(@PathVariable UUID id, @RequestParam AppointmentStatus status) {
        return appointmentService.changeStatus(id, status);
    }

    @Operation(summary = "Cancelar una cita")
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA','MEDICO')")
    public AppointmentResponse cancel(@PathVariable UUID id,
                                      @RequestParam(required = false) String reason) {
        return appointmentService.cancel(id, reason);
    }
}
