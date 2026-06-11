package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.model.AccountingAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountingAccountRepository extends JpaRepository<AccountingAccount, UUID> {
    Optional<AccountingAccount> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
