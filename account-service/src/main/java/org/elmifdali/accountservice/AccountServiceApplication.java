package org.elmifdali.accountservice;

import org.elmifdali.accountservice.service.AccountService;
import org.elmifdali.accountservice.service.CreateAccountRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAccounts(AccountService accountService) {
        return args -> {
            accountService.create(new CreateAccountRequest("CUST-001", "MAD", 5000));
            accountService.create(new CreateAccountRequest("CUST-002", "MAD", 12000));
            accountService.create(new CreateAccountRequest("CUST-003", "EUR", 3000));
        };
    }
}
