package com.microservices_system.data_service.services;

import com.microservices_system.business_service.exceptions.CategoriaDuplicadaException;
import com.microservices_system.business_service.exceptions.CategoriaNoEncontradaException;
import com.microservices_system.data_service.entity.Categoria;
import com.microservices_system.data_service.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria guardar(Categoria categoria) {
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new CategoriaDuplicadaException("La categoría ya está registrada: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + id));
    }

    public Categoria buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada con nombre: " + nombre));
    }

    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria actualizar(Long id, Categoria categoria) {
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + id);
        }
        categoria.setId(id);
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}