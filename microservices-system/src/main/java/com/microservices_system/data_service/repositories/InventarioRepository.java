package com.microservices_system.data_service.repositories;

import com.microservices_system.data_service.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoId(Long id);

    @Query("SELECT i FROM Inventario i WHERE i.cantidad <= i.stockMinimo")
    List<Inventario> findInventariosConStockBajo();

    @Query("SELECT CASE WHEN i.cantidad >= :cantidadRequerida THEN true ELSE false END " +
            "FROM Inventario i WHERE i.producto.id = :productoId")
    Boolean tieneStockSuficiente(@Param("productoId") Long productoId,
                                 @Param("cantidadRequerida") Integer cantidadRequerida);

    List<Inventario> findByCantidad(Integer cantidad);

    @Query("SELECT i FROM Inventario i JOIN FETCH i.producto")
    List<Inventario> findAllWithProducto();
}
