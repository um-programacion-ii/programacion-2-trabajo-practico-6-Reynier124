package com.microservices_system.data_service.controller;

import com.microservices_system.business_service.exceptions.MicroserviceCommunicationException;
import com.microservices_system.business_service.exceptions.RecursoDuplicadoException;
import com.microservices_system.business_service.exceptions.RecursoNoEncontradoException;
import com.microservices_system.business_service.exceptions.ValidacionNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return Map.of("error", ex.getMessage());
    }

    //Recurso duplicado
    @ExceptionHandler(RecursoDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleRecursoDuplicado(RecursoDuplicadoException ex) {
        return Map.of("error", ex.getMessage());
    }

    //Comunicación entre microservicios
    @ExceptionHandler(MicroserviceCommunicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMicroserviceComunication(MicroserviceCommunicationException ex) {
        return Map.of("error", ex.getMessage());
    }

    //Validación de logica de negocio
    @ExceptionHandler(ValidacionNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidacionNegocio(ValidacionNegocioException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Error genérico
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGenericException(Exception ex) {
        return Map.of("error", "Ocurrió un error inesperado");
    }
}