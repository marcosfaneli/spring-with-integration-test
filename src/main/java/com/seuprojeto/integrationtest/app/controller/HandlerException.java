package com.seuprojeto.integrationtest.app.controller;

import com.seuprojeto.integrationtest.domain.CustomerNotFoundException;
import com.seuprojeto.integrationtest.domain.ErrorMessage;
import com.seuprojeto.integrationtest.domain.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class HandlerException {

    private ErrorMessage createMap(String message) {
        return new ErrorMessage(message, Instant.now());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorMessage handleOrderNotFoundException(OrderNotFoundException e) {
        return this.createMap(e.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorMessage handleCustomerNotFoundException(CustomerNotFoundException e) {
        return this.createMap(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(Exception e) {
        System.out.println(e.getClass());
        System.out.println(e.getMessage());
        return this.createMap("Internal server error");
    }
}
