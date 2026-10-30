package org.elmifdali.accountservice.service;

import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(UUID id) {
        super("Insufficient balance on account: " + id);
    }
}
