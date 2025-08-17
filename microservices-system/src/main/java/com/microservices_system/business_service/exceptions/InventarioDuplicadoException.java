package com.microservices_system.business_service.exceptions;

public class InventarioDuplicadoException extends RecursoDuplicadoException {
    public InventarioDuplicadoException(String message) {
        super(message);
    }
}
