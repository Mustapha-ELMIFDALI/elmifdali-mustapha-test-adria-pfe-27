package org.elmifdali.transactionservice.web.graphql;

import graphql.GraphQLError;
import org.elmifdali.transactionservice.service.AccountNotFoundException;
import org.elmifdali.transactionservice.service.AccountServiceUnavailableException;
import org.elmifdali.transactionservice.service.TransactionNotFoundException;
import org.elmifdali.transactionservice.service.TransactionRejectedException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphQlErrors {

    @GraphQlExceptionHandler({AccountNotFoundException.class, TransactionNotFoundException.class})
    public GraphQLError handleNotFound(RuntimeException e) {
        return error(ErrorType.NOT_FOUND, e);
    }

    @GraphQlExceptionHandler
    public GraphQLError handleRejected(TransactionRejectedException e) {
        return error(ErrorType.BAD_REQUEST, e);
    }

    @GraphQlExceptionHandler
    public GraphQLError handleUnavailable(AccountServiceUnavailableException e) {
        return error(ErrorType.INTERNAL_ERROR, e);
    }

    private GraphQLError error(ErrorType type, RuntimeException e) {
        return GraphQLError.newError().errorType(type).message(e.getMessage()).build();
    }
}
