package org.elmifdali.transactionservice.service;

public class TransactionRejectedException extends RuntimeException {
    public TransactionRejectedException(String message) {
        super(message);
    }
}
