package org.elmifdali.transactionservice.web.graphql;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elmifdali.transactionservice.domain.Transaction;
import org.elmifdali.transactionservice.service.CreateTransactionRequest;
import org.elmifdali.transactionservice.service.TransactionService;
import org.elmifdali.transactionservice.service.TransferRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TransactionGraphQlController {

    private final TransactionService transactionService;

    @QueryMapping
    public List<Transaction> transactions() {
        return transactionService.findAll();
    }

    @QueryMapping
    public Transaction transaction(@Argument UUID id) {
        return transactionService.findById(id);
    }

    @QueryMapping
    public List<Transaction> transactionsByAccount(@Argument UUID accountId) {
        return transactionService.findByAccountId(accountId);
    }

    @MutationMapping
    public Transaction createTransaction(@Argument @Valid CreateTransactionRequest input) {
        return transactionService.create(input);
    }

    @MutationMapping
    public List<Transaction> transfer(@Argument @Valid TransferRequest input) {
        return transactionService.transfer(input);
    }
}
