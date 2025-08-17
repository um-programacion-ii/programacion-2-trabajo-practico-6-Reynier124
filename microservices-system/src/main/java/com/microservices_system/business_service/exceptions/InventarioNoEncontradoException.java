package com.microservices_system.business_service.exceptions;

public class InventarioNoEncontradoException extends RecursoNoEncontradoException {
    public InventarioNoEncontradoException(String message) {
        super(message);
    }
}
