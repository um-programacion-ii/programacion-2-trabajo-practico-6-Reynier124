package com.microservices_system.data_service.repositories;

import com.microservices_system.data_service.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Boolean existsByNombreIgnoreCase(String nombre);
    @Query("SELECT e FROM Producto e WHERE e.categoria.nombre = :nombreCategoria")
    List<Producto> findByNombreCategoria(String nombreCategoria);
}
