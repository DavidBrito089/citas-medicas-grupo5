package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.InstitutionConfigRequest;
import com.grupo5.citasmedicas.dto.response.InstitutionConfigResponse;
import com.grupo5.citasmedicas.model.InstitutionConfig;
import com.grupo5.citasmedicas.repository.InstitutionConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Parametros generales de la institucion (RQ-CONF-03). Se maneja como
 * registro unico: si no existe se crea, si existe se actualiza.
 */
@Service
public class InstitutionConfigService {

    private final InstitutionConfigRepository repository;
    private final AuditService auditService;

    public InstitutionConfigService(InstitutionConfigRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public InstitutionConfigResponse get() {
        InstitutionConfig config = repository.findAll().stream().findFirst()
                .orElseGet(() -> InstitutionConfig.builder().nombre("Consultorio Medico").moneda("USD").build());
        return InstitutionConfigResponse.from(config);
    }

    @Transactional
    public InstitutionConfigResponse save(InstitutionConfigRequest req) {
        InstitutionConfig config = repository.findAll().stream().findFirst()
                .orElseGet(InstitutionConfig::new);
        config.setNombre(req.nombre());
        config.setRuc(req.ruc());
        config.setDireccion(req.direccion());
        config.setTelefono(req.telefono());
        config.setEmail(req.email());
        config.setLogoUrl(req.logoUrl());
        config.setMoneda(req.moneda() != null ? req.moneda() : "USD");
        config = repository.save(config);
        auditService.record("InstitutionConfig", config.getId(), "UPDATE", "Parametros de institucion");
        return InstitutionConfigResponse.from(config);
    }
}
