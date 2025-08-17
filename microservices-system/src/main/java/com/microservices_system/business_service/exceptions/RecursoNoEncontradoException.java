package com.microservices_system.business_service.exceptions;

public abstract class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
