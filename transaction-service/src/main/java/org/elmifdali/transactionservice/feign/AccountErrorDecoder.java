package org.elmifdali.transactionservice.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.elmifdali.transactionservice.service.AccountNotFoundException;
import org.elmifdali.transactionservice.service.TransactionRejectedException;
import org.springframework.stereotype.Component;

// 404 and 409 are business answers, not outages: they must not open the circuit breaker
@Component
public class AccountErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new AccountNotFoundException();
            case 409 -> new TransactionRejectedException("Operation rejected by the account service");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
