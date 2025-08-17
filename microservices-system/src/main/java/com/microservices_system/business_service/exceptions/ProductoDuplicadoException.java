package com.microservices_system.business_service.exceptions;

public class ProductoDuplicadoException extends RecursoDuplicadoException {
    public ProductoDuplicadoException(String message) {
        super(message);
    }
}
