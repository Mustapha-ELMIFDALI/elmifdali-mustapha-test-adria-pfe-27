package org.elmifdali.transactionservice.service;

import org.elmifdali.transactionservice.domain.Transaction;
import org.elmifdali.transactionservice.feign.AccountGateway;
import org.elmifdali.transactionservice.model.Account;
import org.elmifdali.transactionservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountGateway accountGateway;

    private TransactionService transactionService;

    private final UUID sourceId = UUID.randomUUID();
    private final UUID destinationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository, accountGateway, new BigDecimal("1000"));
        lenient().when(transactionRepository.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
    }

    private void givenAccount(UUID id, String status, String currency, double balance) {
        when(accountGateway.getAccount(id)).thenReturn(new Account(id, "CUST", status, currency, balance));
    }

    private CreateTransactionRequest payment(String amount, String currency) {
        return new CreateTransactionRequest(sourceId, new BigDecimal(amount), currency, "GROCERY", "MA");
    }

    private void assertRejected(CreateTransactionRequest request) {
        assertThrows(TransactionRejectedException.class, () -> transactionService.create(request));
        verify(transactionRepository, never()).saveAndFlush(any());
        verify(accountGateway, never()).debit(any(), org.mockito.ArgumentMatchers.anyDouble());
    }

    @Test
    void createSavesTransactionThenDebitsAccount() {
        givenAccount(sourceId, "ACTIVE", "MAD", 5000);

        Transaction transaction = transactionService.create(payment("250.00", "MAD"));

        assertEquals(new BigDecimal("250.00"), transaction.getAmount());
        verify(accountGateway).debit(sourceId, 250.0);
    }

    @Test
    void createAcceptsAmountEqualToTheLimit() {
        givenAccount(sourceId, "ACTIVE", "MAD", 5000);

        transactionService.create(payment("1000", "MAD"));

        verify(accountGateway).debit(sourceId, 1000.0);
    }

    @Test
    void createRejectsZeroAmount() {
        assertRejected(payment("0", "MAD"));
    }

    @Test
    void createRejectsAmountAboveTheLimit() {
        assertRejected(payment("1000.01", "MAD"));
    }

    @Test
    void createRejectsBlockedAccount() {
        givenAccount(sourceId, "BLOCKED", "MAD", 5000);

        assertRejected(payment("250", "MAD"));
    }

    @Test
    void createRejectsOtherCurrency() {
        givenAccount(sourceId, "ACTIVE", "MAD", 5000);

        assertRejected(payment("250", "EUR"));
    }

    @Test
    void createRejectsInsufficientBalance() {
        givenAccount(sourceId, "ACTIVE", "MAD", 100);

        assertRejected(payment("250", "MAD"));
    }

    @Test
    void transferDebitsSourceAndCreditsDestination() {
        givenAccount(sourceId, "ACTIVE", "MAD", 5000);
        givenAccount(destinationId, "ACTIVE", "MAD", 0);

        List<Transaction> transactions = transactionService.transfer(
                new TransferRequest(sourceId, destinationId, new BigDecimal("300"), "MAD"));

        assertEquals(2, transactions.size());
        verify(accountGateway).debit(sourceId, 300.0);
        verify(accountGateway).credit(destinationId, 300.0);
    }

    @Test
    void transferRefundsSourceWhenCreditFails() {
        givenAccount(sourceId, "ACTIVE", "MAD", 5000);
        givenAccount(destinationId, "ACTIVE", "MAD", 0);
        doThrow(new AccountServiceUnavailableException(new RuntimeException("down")))
                .when(accountGateway).credit(destinationId, 300.0);

        assertThrows(AccountServiceUnavailableException.class, () -> transactionService.transfer(
                new TransferRequest(sourceId, destinationId, new BigDecimal("300"), "MAD")));

        verify(accountGateway).credit(sourceId, 300.0);
    }

    @Test
    void transferRejectsSameAccount() {
        assertThrows(TransactionRejectedException.class, () -> transactionService.transfer(
                new TransferRequest(sourceId, sourceId, new BigDecimal("300"), "MAD")));
    }
}
