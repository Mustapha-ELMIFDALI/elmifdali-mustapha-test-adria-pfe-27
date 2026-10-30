package org.elmifdali.transactionservice.service;

public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException(Throwable cause) {
        super("Account service is unavailable, try again later", cause);
    }
}
