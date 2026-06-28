package com.grupo5.citasmedicas.service;

import com.grupo5.citasmedicas.dto.request.AccountRequest;
import com.grupo5.citasmedicas.dto.request.JournalEntryRequest;
import com.grupo5.citasmedicas.enums.TransactionType;
import com.grupo5.citasmedicas.exception.BusinessException;
import com.grupo5.citasmedicas.model.AccountingAccount;
import com.grupo5.citasmedicas.model.CashJournalEntry;
import com.grupo5.citasmedicas.model.Invoice;
import com.grupo5.citasmedicas.repository.AccountingAccountRepository;
import com.grupo5.citasmedicas.repository.CashJournalEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountingServiceTest {

    @Mock AccountingAccountRepository accountRepository;
    @Mock CashJournalEntryRepository journalRepository;
    @Mock AuditService auditService;
    @InjectMocks AccountingService service;

    @Test
    void createAccountOk() {
        when(accountRepository.existsByCodigo("1.1.01")).thenReturn(false);
        when(accountRepository.save(any(AccountingAccount.class))).thenAnswer(i -> {
            AccountingAccount a = i.getArgument(0); a.setId(UUID.randomUUID()); return a;
        });
        var r = service.createAccount(new AccountRequest("1.1.01", "Caja", "ACTIVO", null));
        assertThat(r.codigo()).isEqualTo("1.1.01");
        assertThat(r.activa()).isTrue();
    }

    @Test
    void createAccountFallaSiDuplicada() {
        when(accountRepository.existsByCodigo("1.1.01")).thenReturn(true);
        assertThatThrownBy(() -> service.createAccount(new AccountRequest("1.1.01", "Caja", "ACTIVO", true)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateAccountOk() {
        UUID id = UUID.randomUUID();
        AccountingAccount a = AccountingAccount.builder().codigo("1.1.01").nombre("Caja").tipo("ACTIVO").activa(true).build();
        a.setId(id);
        when(accountRepository.findById(id)).thenReturn(Optional.of(a));
        when(accountRepository.save(any(AccountingAccount.class))).thenAnswer(i -> i.getArgument(0));
        var r = service.updateAccount(id, new AccountRequest("1.1.01", "Caja General", "ACTIVO", false));
        assertThat(r.nombre()).isEqualTo("Caja General");
        assertThat(r.activa()).isFalse();
    }

    @Test
    void registerEntryConCuenta() {
        UUID accId = UUID.randomUUID();
        AccountingAccount a = AccountingAccount.builder().codigo("4.1.01").nombre("Ingresos").tipo("INGRESO").build();
        a.setId(accId);
        when(accountRepository.findById(accId)).thenReturn(Optional.of(a));
        when(journalRepository.save(any(CashJournalEntry.class))).thenAnswer(i -> {
            CashJournalEntry e = i.getArgument(0); e.setId(UUID.randomUUID()); return e;
        });
        var r = service.registerEntry(new JournalEntryRequest(TransactionType.INGRESO, "Pago", new BigDecimal("50"), accId));
        assertThat(r.tipo()).isEqualTo(TransactionType.INGRESO);
        assertThat(r.cuentaCodigo()).isEqualTo("4.1.01");
    }

    @Test
    void registerEntrySinCuenta() {
        when(journalRepository.save(any(CashJournalEntry.class))).thenAnswer(i -> i.getArgument(0));
        var r = service.registerEntry(new JournalEntryRequest(TransactionType.EGRESO, "Compra", new BigDecimal("20"), null));
        assertThat(r.tipo()).isEqualTo(TransactionType.EGRESO);
    }

    @Test
    void findAllAccountsYEntries() {
        when(accountRepository.findAll()).thenReturn(List.of(
                AccountingAccount.builder().codigo("1").nombre("c").tipo("ACTIVO").build()));
        when(journalRepository.findAllByOrderByFechaAsc()).thenReturn(List.of(new CashJournalEntry()));
        assertThat(service.findAllAccounts()).hasSize(1);
        assertThat(service.findAllEntries()).hasSize(1);
    }

    @Test
    void recordInvoiceIncomeGuardaIngreso() {
        Invoice inv = Invoice.builder().numero("001").total(new BigDecimal("67.20")).build();
        inv.setId(UUID.randomUUID());
        service.recordInvoiceIncome(inv, null);
        verify(journalRepository).save(any(CashJournalEntry.class));
    }

    @Test
    void defaultIncomeAccountBuscaPorCodigo() {
        AccountingAccount a = AccountingAccount.builder().codigo("4.1.01").nombre("Ingresos").tipo("INGRESO").build();
        when(accountRepository.findByCodigo("4.1.01")).thenReturn(Optional.of(a));
        assertThat(service.defaultIncomeAccount()).isNotNull();
    }

    @Test
    void balanceCalculaIngresosMenosEgresos() {
        CashJournalEntry ing = CashJournalEntry.builder().tipo(TransactionType.INGRESO).monto(new BigDecimal("100")).build();
        CashJournalEntry egr = CashJournalEntry.builder().tipo(TransactionType.EGRESO).monto(new BigDecimal("30")).build();
        when(journalRepository.findAll()).thenReturn(List.of(ing, egr));
        assertThat(service.balance()).isEqualByComparingTo("70");
    }
}
