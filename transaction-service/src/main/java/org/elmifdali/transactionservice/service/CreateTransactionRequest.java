package org.elmifdali.transactionservice.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull UUID accountId,
        @NotNull BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotBlank String merchantCategory,
        @NotBlank String country) {
}
