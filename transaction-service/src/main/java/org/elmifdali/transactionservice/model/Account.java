package org.elmifdali.transactionservice.model;

import java.util.UUID;

public record Account(UUID id, String customerReference, String status, String currency, double balance) {

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
