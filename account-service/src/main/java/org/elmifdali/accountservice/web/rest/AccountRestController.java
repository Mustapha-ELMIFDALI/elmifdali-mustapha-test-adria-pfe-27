package org.elmifdali.accountservice.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elmifdali.accountservice.domain.Account;
import org.elmifdali.accountservice.service.AccountService;
import org.elmifdali.accountservice.service.CreateAccountRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable UUID id) {
        return accountService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.create(request);
    }

    @PatchMapping("/{id}/block")
    public Account block(@PathVariable UUID id) {
        return accountService.block(id);
    }

    @PatchMapping("/{id}/activate")
    public Account activate(@PathVariable UUID id) {
        return accountService.activate(id);
    }

    @PostMapping("/{id}/debit")
    public Account debit(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        return accountService.debit(id, request.amount());
    }

    @PostMapping("/{id}/credit")
    public Account credit(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        return accountService.credit(id, request.amount());
    }
}
