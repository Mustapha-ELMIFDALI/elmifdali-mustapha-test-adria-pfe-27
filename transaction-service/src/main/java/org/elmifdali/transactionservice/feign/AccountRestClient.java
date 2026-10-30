package org.elmifdali.transactionservice.feign;

import org.elmifdali.transactionservice.model.Account;
import org.elmifdali.transactionservice.model.AmountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountRestClient {

    @GetMapping("/{id}")
    Account getAccount(@PathVariable("id") UUID id);

    @PostMapping("/{id}/debit")
    Account debit(@PathVariable("id") UUID id, @RequestBody AmountRequest request);

    @PostMapping("/{id}/credit")
    Account credit(@PathVariable("id") UUID id, @RequestBody AmountRequest request);
}
