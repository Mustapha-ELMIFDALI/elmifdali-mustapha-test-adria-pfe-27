package org.elmifdali.accountservice.service;

import org.elmifdali.accountservice.domain.Account;
import org.elmifdali.accountservice.domain.AccountStatus;
import org.elmifdali.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private final UUID id = UUID.randomUUID();

    private void givenAccount(AccountStatus status, double balance) {
        Account account = Account.builder().id(id).status(status).currency("MAD").balance(balance).build();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
    }

    @Test
    void debitReducesBalance() {
        givenAccount(AccountStatus.ACTIVE, 1000);
        when(accountRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        assertEquals(750, accountService.debit(id, 250).getBalance());
    }

    @Test
    void creditIncreasesBalance() {
        givenAccount(AccountStatus.ACTIVE, 1000);
        when(accountRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        assertEquals(1250, accountService.credit(id, 250).getBalance());
    }

    @Test
    void debitFailsWhenBalanceIsTooLow() {
        givenAccount(AccountStatus.ACTIVE, 100);

        assertThrows(InsufficientBalanceException.class, () -> accountService.debit(id, 250));
    }

    @Test
    void debitFailsWhenAccountIsBlocked() {
        givenAccount(AccountStatus.BLOCKED, 1000);

        assertThrows(AccountNotActiveException.class, () -> accountService.debit(id, 250));
    }

    @Test
    void findByIdFailsWhenAccountDoesNotExist() {
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.findById(id));
    }
}
