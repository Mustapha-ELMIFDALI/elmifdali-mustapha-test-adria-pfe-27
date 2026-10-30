package org.elmifdali.transactionservice.web.rest;

import org.elmifdali.transactionservice.service.AccountNotFoundException;
import org.elmifdali.transactionservice.service.AccountServiceUnavailableException;
import org.elmifdali.transactionservice.service.TransactionNotFoundException;
import org.elmifdali.transactionservice.service.TransactionRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({AccountNotFoundException.class, TransactionNotFoundException.class})
    public ProblemDetail handleNotFound(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(TransactionRejectedException.class)
    public ProblemDetail handleRejected(TransactionRejectedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ProblemDetail handleUnavailable(AccountServiceUnavailableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }
}
