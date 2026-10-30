package org.elmifdali.transactionservice.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.elmifdali.transactionservice.model.Account;
import org.elmifdali.transactionservice.model.AmountRequest;
import org.elmifdali.transactionservice.service.AccountNotFoundException;
import org.elmifdali.transactionservice.service.AccountServiceUnavailableException;
import org.elmifdali.transactionservice.service.TransactionRejectedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

// The fallbacks fail closed: without the real account a transaction must be refused, never approved
@Component
@RequiredArgsConstructor
public class AccountGateway {

    private static final String ACCOUNT_SERVICE = "accountService";

    private final AccountRestClient accountRestClient;

    @Retry(name = ACCOUNT_SERVICE, fallbackMethod = "getAccountFallback")
    @CircuitBreaker(name = ACCOUNT_SERVICE)
    public Account getAccount(UUID id) {
        return accountRestClient.getAccount(id);
    }

    // No retry on debit and credit: repeating them could move the money twice
    @CircuitBreaker(name = ACCOUNT_SERVICE, fallbackMethod = "updateBalanceFallback")
    public void debit(UUID id, double amount) {
        accountRestClient.debit(id, new AmountRequest(amount));
    }

    @CircuitBreaker(name = ACCOUNT_SERVICE, fallbackMethod = "updateBalanceFallback")
    public void credit(UUID id, double amount) {
        accountRestClient.credit(id, new AmountRequest(amount));
    }

    private Account getAccountFallback(UUID id, Throwable cause) {
        throw toServiceException(cause);
    }

    private void updateBalanceFallback(UUID id, double amount, Throwable cause) {
        throw toServiceException(cause);
    }

    private RuntimeException toServiceException(Throwable cause) {
        if (cause instanceof AccountNotFoundException || cause instanceof TransactionRejectedException) {
            return (RuntimeException) cause;
        }
        return new AccountServiceUnavailableException(cause);
    }
}
