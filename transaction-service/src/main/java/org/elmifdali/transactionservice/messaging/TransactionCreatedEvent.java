package org.elmifdali.transactionservice.messaging;

import org.elmifdali.transactionservice.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID eventId,
        String eventType,
        UUID transactionId,
        UUID accountId,
        BigDecimal amount,
        String currency,
        Instant occurredAt) {

    public static TransactionCreatedEvent from(Transaction transaction) {
        return new TransactionCreatedEvent(
                UUID.randomUUID(),
                "TransactionCreated",
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getCreatedAt());
    }
}
