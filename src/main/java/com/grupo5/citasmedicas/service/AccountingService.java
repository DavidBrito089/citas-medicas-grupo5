package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.AccountRequest;
import com.grupo5.citasmedicas.dto.request.JournalEntryRequest;
import com.grupo5.citasmedicas.dto.response.AccountResponse;
import com.grupo5.citasmedicas.dto.response.JournalEntryResponse;
import com.grupo5.citasmedicas.enums.TransactionType;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.exception.ResourceNotFoundException;
import com.grupo5.citasmedicas.model.AccountingAccount;
import com.grupo5.citasmedicas.model.CashJournalEntry;
import com.grupo5.citasmedicas.model.Invoice;
import com.grupo5.citasmedicas.repository.AccountingAccountRepository;
import com.grupo5.citasmedicas.repository.CashJournalEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Plan de cuentas contable (RQ-CONF-02) y libro diario de caja (RQ-FIN-02, RQ-FIN-03).
 */
@Service
public class AccountingService {

    private final AccountingAccountRepository accountRepository;
    private final CashJournalEntryRepository journalRepository;
    private final AuditService auditService;

    public AccountingService(AccountingAccountRepository accountRepository,
                             CashJournalEntryRepository journalRepository, AuditService auditService) {
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
        this.auditService = auditService;
    }

    // ---- Plan de cuentas (RQ-CONF-02) ----

    public List<AccountResponse> findAllAccounts() {
        return accountRepository.findAll().stream().map(AccountResponse::from).toList();
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest req) {
        if (accountRepository.existsByCodigo(req.codigo())) {
            throw new BusinessException("ERR_ACCOUNT_DUPLICATE", "Ya existe una cuenta con ese codigo");
        }
        AccountingAccount account = AccountingAccount.builder()
                .codigo(req.codigo())
                .nombre(req.nombre())
                .tipo(req.tipo())
                .activa(req.activa() == null || req.activa())
                .build();
        account = accountRepository.save(account);
        auditService.record("AccountingAccount", account.getId(), "CREATE", "Cuenta " + req.codigo());
        return AccountResponse.from(account);
    }

    @Transactional
    public AccountResponse updateAccount(UUID id, AccountRequest req) {
        AccountingAccount account = accountRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Cuenta contable", id));
        account.setNombre(req.nombre());
        account.setTipo(req.tipo());
        if (req.activa() != null) {
            account.setActiva(req.activa());
        }
        account = accountRepository.save(account);
        auditService.record("AccountingAccount", id, "UPDATE", "Actualizacion de cuenta");
        return AccountResponse.from(account);
    }

    // ---- Libro diario (RQ-FIN-02 / RQ-FIN-03) ----

    public List<JournalEntryResponse> findAllEntries() {
        return journalRepository.findAllByOrderByFechaAsc().stream().map(JournalEntryResponse::from).toList();
    }

    @Transactional
    public JournalEntryResponse registerEntry(JournalEntryRequest req) {
        AccountingAccount account = null;
        if (req.accountId() != null) {
            account = accountRepository.findById(req.accountId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Cuenta contable", req.accountId()));
        }
        CashJournalEntry entry = CashJournalEntry.builder()
                .fecha(OffsetDateTime.now())
                .tipo(req.tipo())
                .concepto(req.concepto())
                .monto(req.monto())
                .account(account)
                .build();
        entry = journalRepository.save(entry);
        auditService.record("CashJournalEntry", entry.getId(), "CREATE",
                req.tipo() + " " + req.monto());
        return JournalEntryResponse.from(entry);
    }

    /**
     * Registro automatico en el diario al emitir un comprobante (RQ-FIN-02).
     * Lo invoca InvoiceService dentro de la misma transaccion.
     */
    @Transactional
    public void recordInvoiceIncome(Invoice invoice, AccountingAccount account) {
        CashJournalEntry entry = CashJournalEntry.builder()
                .fecha(OffsetDateTime.now())
                .tipo(TransactionType.INGRESO)
                .concepto("Comprobante " + invoice.getNumero())
                .monto(invoice.getTotal())
                .account(account)
                .invoice(invoice)
                .build();
        journalRepository.save(entry);
    }

    /** Cuenta de ingresos por defecto para asociar las ventas (RQ-FIN-03). */
    public AccountingAccount defaultIncomeAccount() {
        return accountRepository.findByCodigo("4.1.01").orElse(null);
    }

    public BigDecimal balance() {
        return journalRepository.findAll().stream()
                .map(e -> e.getTipo() == TransactionType.INGRESO ? e.getMonto() : e.getMonto().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
