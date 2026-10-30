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
            String customer = "Mustapha Elmifdali - mustaphamifdali2001@gmail.com";
            accountService.create(new CreateAccountRequest(customer + " - 01", "MAD", 5000));
            accountService.create(new CreateAccountRequest(customer + " - 02", "MAD", 12000));
            accountService.create(new CreateAccountRequest(customer + " - 03", "EUR", 3000));
        };
    }
}
