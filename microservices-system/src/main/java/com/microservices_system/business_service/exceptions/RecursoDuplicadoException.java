package com.microservices_system.business_service.exceptions;

public abstract class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String message) {
        super(message);
    }
}
