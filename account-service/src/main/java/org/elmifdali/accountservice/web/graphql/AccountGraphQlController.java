package org.elmifdali.accountservice.web.graphql;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elmifdali.accountservice.domain.Account;
import org.elmifdali.accountservice.service.AccountService;
import org.elmifdali.accountservice.service.CreateAccountRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphQlController {

    private final AccountService accountService;

    @QueryMapping
    public List<Account> accounts() {
        return accountService.findAll();
    }

    @QueryMapping
    public Account account(@Argument UUID id) {
        return accountService.findById(id);
    }

    @MutationMapping
    public Account createAccount(@Argument @Valid CreateAccountRequest input) {
        return accountService.create(input);
    }

    @MutationMapping
    public Account blockAccount(@Argument UUID id) {
        return accountService.block(id);
    }

    @MutationMapping
    public Account activateAccount(@Argument UUID id) {
        return accountService.activate(id);
    }
}
