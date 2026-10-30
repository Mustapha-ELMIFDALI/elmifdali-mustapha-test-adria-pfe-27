package org.elmifdali.transactionservice.service;

import lombok.extern.slf4j.Slf4j;
import org.elmifdali.transactionservice.domain.Transaction;
import org.elmifdali.transactionservice.domain.TransactionType;
import org.elmifdali.transactionservice.feign.AccountGateway;
import org.elmifdali.transactionservice.model.Account;
import org.elmifdali.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountGateway accountGateway;
    private final BigDecimal maxAmount;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountGateway accountGateway,
                              @Value("${bank.transaction.max-amount}") BigDecimal maxAmount) {
        this.transactionRepository = transactionRepository;
        this.accountGateway = accountGateway;
        this.maxAmount = maxAmount;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(UUID id) {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public List<Transaction> findByAccountId(UUID accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public Transaction create(CreateTransactionRequest request) {
        checkAmount(request.amount());
        Account account = findDebitableAccount(request.accountId(), request.currency(), request.amount());

        Transaction transaction = record(request.accountId(), TransactionType.PAYMENT, request.amount(),
                account.currency(), request.merchantCategory(), request.country());
        accountGateway.debit(request.accountId(), request.amount().doubleValue());
        return transaction;
    }

    public List<Transaction> transfer(TransferRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new TransactionRejectedException("Source and destination accounts must be different");
        }
        checkAmount(request.amount());
        Account source = findDebitableAccount(request.fromAccountId(), request.currency(), request.amount());
        Account destination = accountGateway.getAccount(request.toAccountId());
        checkActive(destination);
        checkCurrency(destination, request.currency());

        Transaction transferOut = record(source.id(), TransactionType.TRANSFER_OUT, request.amount(),
                source.currency(), null, null);
        Transaction transferIn = record(destination.id(), TransactionType.TRANSFER_IN, request.amount(),
                destination.currency(), null, null);

        double amount = request.amount().doubleValue();
        accountGateway.debit(source.id(), amount);
        try {
            accountGateway.credit(destination.id(), amount);
        } catch (RuntimeException e) {
            refund(source.id(), amount);
            throw e;
        }
        return List.of(transferOut, transferIn);
    }

    private Transaction record(UUID accountId, TransactionType type, BigDecimal amount, String currency,
                               String merchantCategory, String country) {
        // Flushed before the remote balance update so a database error cannot follow a debit
        return transactionRepository.saveAndFlush(Transaction.builder()
                .accountId(accountId)
                .type(type)
                .amount(amount)
                .currency(currency)
                .merchantCategory(merchantCategory)
                .country(country)
                .createdAt(Instant.now())
                .build());
    }

    private void refund(UUID accountId, double amount) {
        try {
            accountGateway.credit(accountId, amount);
        } catch (RuntimeException e) {
            log.error("Refund failed, account {} must be corrected by {}", accountId, amount, e);
        }
    }

    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new TransactionRejectedException("Amount must be strictly positive");
        }
        if (amount.compareTo(maxAmount) > 0) {
            throw new TransactionRejectedException("Amount exceeds the maximum of " + maxAmount);
        }
    }

    private Account findDebitableAccount(UUID accountId, String currency, BigDecimal amount) {
        Account account = accountGateway.getAccount(accountId);
        checkActive(account);
        checkCurrency(account, currency);
        if (BigDecimal.valueOf(account.balance()).compareTo(amount) < 0) {
            throw new TransactionRejectedException("Insufficient balance");
        }
        return account;
    }

    private void checkActive(Account account) {
        if (!account.isActive()) {
            throw new TransactionRejectedException("Account is not active");
        }
    }

    private void checkCurrency(Account account, String currency) {
        if (!account.currency().equalsIgnoreCase(currency)) {
            throw new TransactionRejectedException("Currency does not match the account currency " + account.currency());
        }
    }
}
