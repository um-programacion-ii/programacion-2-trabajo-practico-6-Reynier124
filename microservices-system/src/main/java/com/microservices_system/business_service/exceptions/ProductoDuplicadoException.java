package com.microservices_system.business_service.exceptions;

public class ProductoDuplicadoException extends RuntimeException {
    public ProductoDuplicadoException(String message) {
        super(message);
    }
}
