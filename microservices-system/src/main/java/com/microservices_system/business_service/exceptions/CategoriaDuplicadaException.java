package com.microservices_system.business_service.exceptions;

public class CategoriaDuplicadaException extends RuntimeException {
    public CategoriaDuplicadaException(String message) {
        super(message);
    }
}
