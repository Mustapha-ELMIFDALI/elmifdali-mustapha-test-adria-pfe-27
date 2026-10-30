package org.elmifdali.accountservice.service;

import lombok.RequiredArgsConstructor;
import org.elmifdali.accountservice.domain.Account;
import org.elmifdali.accountservice.domain.AccountStatus;
import org.elmifdali.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account create(CreateAccountRequest request) {
        return accountRepository.save(Account.builder()
                .customerReference(request.customerReference())
                .status(AccountStatus.ACTIVE)
                .currency(request.currency().toUpperCase())
                .balance(request.balance())
                .build());
    }

    public Account block(UUID id) {
        return updateStatus(id, AccountStatus.BLOCKED);
    }

    public Account activate(UUID id) {
        return updateStatus(id, AccountStatus.ACTIVE);
    }

    public Account debit(UUID id, double amount) {
        Account account = findActive(id);
        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException(id);
        }
        account.setBalance(account.getBalance() - amount);
        return accountRepository.save(account);
    }

    public Account credit(UUID id, double amount) {
        Account account = findActive(id);
        account.setBalance(account.getBalance() + amount);
        return accountRepository.save(account);
    }

    private Account updateStatus(UUID id, AccountStatus status) {
        Account account = findById(id);
        account.setStatus(status);
        return accountRepository.save(account);
    }

    private Account findActive(UUID id) {
        Account account = findById(id);
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(id);
        }
        return account;
    }
}
