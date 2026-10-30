package org.elmifdali.accountservice.web.graphql;

import graphql.GraphQLError;
import org.elmifdali.accountservice.service.AccountNotFoundException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphQlErrors {

    @GraphQlExceptionHandler
    public GraphQLError handleNotFound(AccountNotFoundException e) {
        return GraphQLError.newError()
                .errorType(ErrorType.NOT_FOUND)
                .message(e.getMessage())
                .build();
    }
}
