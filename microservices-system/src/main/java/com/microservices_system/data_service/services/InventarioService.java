package com.microservices_system.data_service.services;

import com.microservices_system.business_service.exceptions.InventarioNoEncontradoException;
import com.microservices_system.data_service.entity.Inventario;
import com.microservices_system.data_service.repositories.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventarioService {
    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public Inventario guardar(Inventario inventario) {
        inventario.setFechaActualizacion(LocalDateTime.now());
        return inventarioRepository.save(inventario);
    }

    public Inventario buscarPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario no encontrado con ID: " + id));
    }

    public Inventario buscarPorProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario no encontrado para producto ID: " + productoId));
    }

    public List<Inventario> obtenerTodos() {
        return inventarioRepository.findAll();
    }

    public List<Inventario> obtenerProductosConStockBajo() {
        return inventarioRepository.findInventariosConStockBajo();
    }

    public Inventario actualizar(Long id, Inventario inventario) {
        if (!inventarioRepository.existsById(id)) {
            throw new InventarioNoEncontradoException("Inventario no encontrado con ID: " + id);
        }
        inventario.setId(id);
        inventario.setFechaActualizacion(LocalDateTime.now());
        return inventarioRepository.save(inventario);
    }

    public void eliminar(Long id) {
        if (!inventarioRepository.existsById(id)) {
            throw new InventarioNoEncontradoException("Inventario no encontrado con ID: " + id);
        }
        inventarioRepository.deleteById(id);
    }

    public Inventario actualizarCantidad(Long productoId, Integer nuevaCantidad) {
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        Inventario inventario = buscarPorProductoId(productoId);
        inventario.setCantidad(nuevaCantidad);
        inventario.setFechaActualizacion(LocalDateTime.now());
        return inventarioRepository.save(inventario);
    }


    public boolean tieneStockSuficiente(Long productoId, Integer cantidadRequerida) {
        Boolean resultado = inventarioRepository.tieneStockSuficiente(productoId, cantidadRequerida);
        return resultado != null ? resultado : false;
    }

    public List<Inventario> obtenerProductosSinStock() {
        return inventarioRepository.findByCantidad(0);
    }

    public List<Inventario> obtenerTodosConProducto(){
        return inventarioRepository.findAllWithProducto();
    }
}
