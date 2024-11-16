package com.seuprojeto.integrationtest.app.controller;

import com.seuprojeto.integrationtest.domain.CustomerNotFoundException;
import com.seuprojeto.integrationtest.domain.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class HandlerException {

    private Map<String, String> createMap(String message) {
        final Map<String, String> map = new HashMap<>();
        map.put("message", message);
        map.put("timestamp", Instant.now().toString());

        return map;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Map<String, String> handleOrderNotFoundException(OrderNotFoundException e) {
        return this.createMap(e.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Map<String, String> handleCustomerNotFoundException(CustomerNotFoundException e) {
        return this.createMap(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception e) {
        System.out.println(e.getClass());
        System.out.println(e.getMessage());
        return this.createMap("Internal server error");
    }
}
