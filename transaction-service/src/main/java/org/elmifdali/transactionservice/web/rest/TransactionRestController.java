package org.elmifdali.transactionservice.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elmifdali.transactionservice.domain.Transaction;
import org.elmifdali.transactionservice.service.CreateTransactionRequest;
import org.elmifdali.transactionservice.service.TransactionService;
import org.elmifdali.transactionservice.service.TransferRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    @GetMapping
    public List<Transaction> findAll() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public Transaction findById(@PathVariable UUID id) {
        return transactionService.findById(id);
    }

    @GetMapping("/account/{accountId}")
    public List<Transaction> findByAccountId(@PathVariable UUID accountId) {
        return transactionService.findByAccountId(accountId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.create(request);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Transaction> transfer(@Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(request);
    }
}
