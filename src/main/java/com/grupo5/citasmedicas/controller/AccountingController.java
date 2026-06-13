package com.grupo5.citasmedicas.controller;

import com.grupo5.citasmedicas.dto.request.AccountRequest;
import com.grupo5.citasmedicas.dto.request.JournalEntryRequest;
import com.grupo5.citasmedicas.dto.response.AccountResponse;
import com.grupo5.citasmedicas.dto.response.JournalEntryResponse;
import com.grupo5.citasmedicas.service.AccountingService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Contabilidad", description = "Plan de cuentas y libro diario - RQ-CONF-02 / RQ-FIN-02 / RQ-FIN-03 (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/accounting")
@PreAuthorize("hasRole('ADMIN')")
public class AccountingController {

    private final AccountingService accountingService;

    public AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @Operation(summary = "Listar plan de cuentas")
    @GetMapping("/accounts")
    public List<AccountResponse> findAllAccounts() {
        return accountingService.findAllAccounts();
    }

    @Operation(summary = "Crear cuenta contable")
    @PostMapping("/accounts")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountingService.createAccount(request));
    }

    @Operation(summary = "Actualizar cuenta contable")
    @PutMapping("/accounts/{id}")
    public AccountResponse updateAccount(@PathVariable UUID id, @Valid @RequestBody AccountRequest request) {
        return accountingService.updateAccount(id, request);
    }

    @Operation(summary = "Listar movimientos del libro diario")
    @GetMapping("/journal")
    public List<JournalEntryResponse> findAllEntries() {
        return accountingService.findAllEntries();
    }

    @Operation(summary = "Registrar un ingreso o egreso manual")
    @PostMapping("/journal")
    public ResponseEntity<JournalEntryResponse> registerEntry(@Valid @RequestBody JournalEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountingService.registerEntry(request));
    }

    @Operation(summary = "Saldo de caja (ingresos - egresos)")
    @GetMapping("/balance")
    public Map<String, BigDecimal> balance() {
        return Map.of("saldo", accountingService.balance());
    }
}
