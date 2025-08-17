package com.microservices_system.data_service.services;

import com.microservices_system.business_service.exceptions.CategoriaDuplicadaException;
import com.microservices_system.business_service.exceptions.CategoriaNoEncontradaException;
import com.microservices_system.data_service.entity.Categoria;
import com.microservices_system.data_service.repositories.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónicos");
        categoria.setDescripcion("Productos electrónicos diversos");
    }

    @Test
    void guardar_CategoriaNueva_DeberiaGuardarCorrectamente() {
        // Given
        when(categoriaRepository.existsByNombreIgnoreCase("Electrónicos")).thenReturn(false);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        // When
        Categoria resultado = categoriaService.guardar(categoria);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Electrónicos");
        verify(categoriaRepository).existsByNombreIgnoreCase("Electrónicos");
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void guardar_CategoriaDuplicada_DeberiaLanzarExcepcion() {
        // Given
        when(categoriaRepository.existsByNombreIgnoreCase("Electrónicos")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoriaService.guardar(categoria))
                .isInstanceOf(CategoriaDuplicadaException.class)
                .hasMessage("La categoría ya está registrada: Electrónicos");

        verify(categoriaRepository).existsByNombreIgnoreCase("Electrónicos");
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void buscarPorId_CategoriaExistente_DeberiaRetornarCategoria() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        Categoria resultado = categoriaService.buscarPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Electrónicos");
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void buscarPorId_CategoriaNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoriaService.buscarPorId(1L))
                .isInstanceOf(CategoriaNoEncontradaException.class)
                .hasMessage("Categoría no encontrada con ID: 1");

        verify(categoriaRepository).findById(1L);
    }

    @Test
    void buscarPorNombre_CategoriaExistente_DeberiaRetornarCategoria() {
        // Given
        when(categoriaRepository.findByNombreIgnoreCase("Electrónicos")).thenReturn(Optional.of(categoria));

        // When
        Categoria resultado = categoriaService.buscarPorNombre("Electrónicos");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Electrónicos");
        verify(categoriaRepository).findByNombreIgnoreCase("Electrónicos");
    }

    @Test
    void buscarPorNombre_CategoriaNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(categoriaRepository.findByNombreIgnoreCase("NoExiste")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoriaService.buscarPorNombre("NoExiste"))
                .isInstanceOf(CategoriaNoEncontradaException.class)
                .hasMessage("Categoría no encontrada con nombre: NoExiste");

        verify(categoriaRepository).findByNombreIgnoreCase("NoExiste");
    }

    @Test
    void obtenerTodas_DeberiaRetornarTodasLasCategorias() {
        // Given
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        // When
        List<Categoria> resultado = categoriaService.obtenerTodas();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(categoria);
        verify(categoriaRepository).findAll();
    }

    @Test
    void actualizar_CategoriaExistente_DeberiaActualizarCorrectamente() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        // When
        Categoria resultado = categoriaService.actualizar(1L, categoria);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void actualizar_CategoriaNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> categoriaService.actualizar(1L, categoria))
                .isInstanceOf(CategoriaNoEncontradaException.class)
                .hasMessage("Categoría no encontrada con ID: 1");

        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void eliminar_CategoriaExistente_DeberiaEliminarCorrectamente() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        // When
        categoriaService.eliminar(1L);

        // Then
        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void eliminar_CategoriaNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> categoriaService.eliminar(1L))
                .isInstanceOf(CategoriaNoEncontradaException.class)
                .hasMessage("Categoría no encontrada con ID: 1");

        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository, never()).deleteById(any(Long.class));
    }
}
