package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.events.*;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountEventHandler implements EventHandler {

    @Autowired
    private AccountRepository accountRepository;

    /* ---------- Apertura de cuenta ---------- */
    @Override
    public void on(AccountOpenedEvent event) {
        BankAccount bankAccount = BankAccount.builder()
                .id(event.getId())
                .accountHolder(event.getAccountHolder())
                .creationDate(event.getCreatedDate())
                .accountType(event.getAccountType())
                .balance(event.getOpeningBalance())              // openingBalance es double
                .build();
        accountRepository.save(bankAccount);
    }

    /* ---------- Depósito ---------- */
    @Override
    public void on(FundsDepositedEvent event) {
        accountRepository.findById(event.getId()).ifPresent(account -> {
            double latestBalance = account.getBalance() + event.getAmount(); // suma
            account.setBalance(latestBalance);
            accountRepository.save(account);
        });
    }

    /* ---------- Retiro ---------- */
    @Override
    public void on(FundsWithdrawnEvent event) {
        accountRepository.findById(event.getId()).ifPresent(account -> {
            double latestBalance = account.getBalance() - event.getAmount(); // resta
            account.setBalance(latestBalance);          // ← línea que faltaba
            accountRepository.save(account);
        });
    }

    /* ---------- Cierre de cuenta ---------- */
    @Override
    public void on(AccountClosedEvent event) {
        accountRepository.deleteById(event.getId());
    }
}
