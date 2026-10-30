package org.elmifdali.transactionservice.mcp;

import lombok.RequiredArgsConstructor;
import org.elmifdali.transactionservice.domain.Transaction;
import org.elmifdali.transactionservice.service.CreateTransactionRequest;
import org.elmifdali.transactionservice.service.TransactionService;
import org.elmifdali.transactionservice.service.TransferRequest;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMcpTools {

    private final TransactionService transactionService;

    @McpTool(description = "Get all transactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.findAll();
    }

    @McpTool(description = "Get a transaction by id")
    public Transaction getTransactionById(@McpToolParam(description = "The transaction id") UUID id) {
        return transactionService.findById(id);
    }

    @McpTool(description = "Get the transactions of a bank account")
    public List<Transaction> getTransactionsByAccount(@McpToolParam(description = "The account id") UUID accountId) {
        return transactionService.findByAccountId(accountId);
    }

    @McpTool(description = "Create a payment transaction, which debits the account")
    public Transaction createTransaction(@McpToolParam(description = "The transaction (accountId, amount, currency, merchantCategory, country)") CreateTransactionRequest request) {
        return transactionService.create(request);
    }

    @McpTool(description = "Transfer money from one bank account to another")
    public List<Transaction> transfer(@McpToolParam(description = "The transfer (fromAccountId, toAccountId, amount, currency)") TransferRequest request) {
        return transactionService.transfer(request);
    }
}
