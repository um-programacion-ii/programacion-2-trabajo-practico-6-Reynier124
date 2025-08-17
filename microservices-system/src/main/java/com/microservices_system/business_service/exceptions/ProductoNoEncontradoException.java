package com.microservices_system.business_service.exceptions;

public class ProductoNoEncontradoException extends RecursoNoEncontradoException {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
