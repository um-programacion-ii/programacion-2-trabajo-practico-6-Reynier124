package com.microservices_system.business_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO {
    private Long id;
    private Integer producto;
    private Integer cantidad;
    private Integer stockMinimo;
    private LocalDateTime fechaActualizacion;
}
