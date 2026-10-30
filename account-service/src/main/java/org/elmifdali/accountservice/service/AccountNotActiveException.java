package org.elmifdali.accountservice.service;

import java.util.UUID;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(UUID id) {
        super("Account is not active: " + id);
    }
}
