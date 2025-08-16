package com.microservices_system.data_service.services;

import com.microservices_system.business_service.exceptions.ProductoDuplicadoException;
import com.microservices_system.business_service.exceptions.ProductoNoEncontradoException;
import com.microservices_system.data_service.entity.Producto;
import com.microservices_system.data_service.repositories.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto guardar (Producto producto) {
        if (productoRepository.existsByNombreIgnoreCase(producto.getNombre())) {
            throw new ProductoDuplicadoException("El producto ya está registrado: " + producto.getNombre());
        }
        return productoRepository.save(producto);
    }

    public Producto buscarPorId(Long id){
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    public List<Producto> buscarPorCategoria(String nombreCategoria) {
        return productoRepository.findByNombreCategoria(nombreCategoria);
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Producto actualizar(Long id, Producto producto){
        if (!productoRepository.existsById(id)) {
            throw new ProductoNoEncontradoException("Producto no encontrado con ID: " + id);
        }
        producto.setId(id);
        return productoRepository.save(producto);
    }

    public void eliminar(Long id){
        if (!productoRepository.existsById(id)) {
            throw new ProductoNoEncontradoException("Empleado no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }
}
