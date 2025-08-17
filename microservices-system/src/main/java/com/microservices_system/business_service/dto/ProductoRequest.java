package com.microservices_system.business_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequest {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer categoriaId;
    private String categoriaNombre;
    private Integer stock;
}
