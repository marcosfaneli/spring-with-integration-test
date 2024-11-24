package com.seuprojeto.integrationtest.app.controller;

import com.seuprojeto.integrationtest.domain.CustomerNotFoundException;
import com.seuprojeto.integrationtest.domain.ErrorMessage;
import com.seuprojeto.integrationtest.domain.InvalidStatusException;
import com.seuprojeto.integrationtest.domain.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class HandlerException {

    private ErrorMessage createMap(String message) {
        return new ErrorMessage(message, LocalDateTime.now());
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

    @ExceptionHandler(InvalidStatusException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleInvalidStatusException(InvalidStatusException e) {
        return this.createMap(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final var fields = e.getBindingResult().getFieldErrors().stream()
                .collect(
                        () -> new java.util.HashMap<String, String>(),
                        (map, fieldError) -> map.put(fieldError.getField(), fieldError.getDefaultMessage()),
                        Map::putAll
                );
        return new ErrorMessage("Invalid fields", LocalDateTime.now(), fields);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(Exception e) {
        System.out.println(e.getClass());
        System.out.println(e.getMessage());
        return this.createMap("Internal server error");
    }
}
