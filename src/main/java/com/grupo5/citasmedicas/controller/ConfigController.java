package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.InstitutionConfigRequest;
import com.grupo5.citasmedicas.dto.response.InstitutionConfigResponse;
import com.grupo5.citasmedicas.service.InstitutionConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Configuracion", description = "Parametros generales de la institucion - RQ-CONF-03")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    private final InstitutionConfigService configService;

    public ConfigController(InstitutionConfigService configService) {
        this.configService = configService;
    }

    @Operation(summary = "Obtener parametros de la institucion")
    @GetMapping
    public InstitutionConfigResponse get() {
        return configService.get();
    }

    @Operation(summary = "Actualizar parametros de la institucion (solo ADMIN)")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public InstitutionConfigResponse save(@Valid @RequestBody InstitutionConfigRequest request) {
        return configService.save(request);
    }
}
