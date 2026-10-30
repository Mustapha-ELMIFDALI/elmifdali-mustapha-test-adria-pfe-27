package org.elmifdali.transactionservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID accountId;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private String merchantCategory;
    private String country;
    private Instant createdAt;
}
