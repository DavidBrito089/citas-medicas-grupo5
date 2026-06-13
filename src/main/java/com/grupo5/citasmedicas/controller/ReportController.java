package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.response.MedicalRecordResponse;
import com.grupo5.citasmedicas.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Reportes", description = "Reportes exportables: libro diario, historia clinica y comprobantes - RQ-REP-01")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Reporte del Libro Diario con totales")
    @GetMapping("/libro-diario")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> libroDiario() {
        return reportService.libroDiario();
    }

    @Operation(summary = "Historia clinica por paciente (cedula)")
    @GetMapping("/historia-clinica/{nationalId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public List<MedicalRecordResponse> historiaClinica(@PathVariable String nationalId) {
        return reportService.historiaClinica(nationalId);
    }

    @Operation(summary = "Resumen de comprobantes de venta")
    @GetMapping("/comprobantes")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public Map<String, Object> resumenComprobantes() {
        return reportService.resumenComprobantes();
    }
}
