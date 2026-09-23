package com.pokeprint.api.infra.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends BusinessException {
    public BusinessRuleException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
