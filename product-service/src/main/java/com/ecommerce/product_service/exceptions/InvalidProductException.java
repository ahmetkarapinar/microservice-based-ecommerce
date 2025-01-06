package com.ecommerce.product_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProductException extends RuntimeException {
    public InvalidProductException(String message) {
        super(message);
    }
}