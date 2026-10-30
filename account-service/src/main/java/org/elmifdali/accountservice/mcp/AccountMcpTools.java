package org.elmifdali.accountservice.mcp;

import lombok.RequiredArgsConstructor;
import org.elmifdali.accountservice.domain.Account;
import org.elmifdali.accountservice.service.AccountService;
import org.elmifdali.accountservice.service.CreateAccountRequest;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountMcpTools {

    private final AccountService accountService;

    @McpTool(description = "Get all bank accounts")
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }

    @McpTool(description = "Get a bank account by id")
    public Account getAccountById(@McpToolParam(description = "The account id") UUID id) {
        return accountService.findById(id);
    }

    @McpTool(description = "Create a new bank account")
    public Account createAccount(@McpToolParam(description = "The account to create (customerReference, currency, balance)") CreateAccountRequest request) {
        return accountService.create(request);
    }

    @McpTool(description = "Block a bank account")
    public Account blockAccount(@McpToolParam(description = "The account id") UUID id) {
        return accountService.block(id);
    }

    @McpTool(description = "Activate a blocked bank account")
    public Account activateAccount(@McpToolParam(description = "The account id") UUID id) {
        return accountService.activate(id);
    }
}
