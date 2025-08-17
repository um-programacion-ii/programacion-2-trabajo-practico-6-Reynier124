package com.microservices_system.business_service.exceptions;

public class CategoriaNoEncontradaException extends RecursoNoEncontradoException {
    public CategoriaNoEncontradaException(String message) {
        super(message);
    }
}
