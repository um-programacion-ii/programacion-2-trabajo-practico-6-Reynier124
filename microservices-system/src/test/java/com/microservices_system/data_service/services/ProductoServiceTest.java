package com.microservices_system.data_service.services;

import com.microservices_system.business_service.exceptions.ProductoDuplicadoException;
import com.microservices_system.business_service.exceptions.ProductoNoEncontradoException;
import com.microservices_system.data_service.entity.Categoria;
import com.microservices_system.data_service.entity.Producto;
import com.microservices_system.data_service.repositories.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónicos");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop HP");
        producto.setDescripcion("Laptop HP Pavilion");
        producto.setPrecio(new BigDecimal("1500.00"));
        producto.setCategoria(categoria);
    }

    @Test
    void guardar_ProductoNuevo_DeberiaGuardarCorrectamente() {
        // Given
        when(productoRepository.existsByNombreIgnoreCase("Laptop HP")).thenReturn(false);
        when(productoRepository.save(producto)).thenReturn(producto);

        // When
        Producto resultado = productoService.guardar(producto);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Laptop HP");
        verify(productoRepository).existsByNombreIgnoreCase("Laptop HP");
        verify(productoRepository).save(producto);
    }

    @Test
    void guardar_ProductoDuplicado_DeberiaLanzarExcepcion() {
        // Given
        when(productoRepository.existsByNombreIgnoreCase("Laptop HP")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> productoService.guardar(producto))
                .isInstanceOf(ProductoDuplicadoException.class)
                .hasMessage("El producto ya está registrado: Laptop HP");

        verify(productoRepository).existsByNombreIgnoreCase("Laptop HP");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void buscarPorId_ProductoExistente_DeberiaRetornarProducto() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        Producto resultado = productoService.buscarPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Laptop HP");
        verify(productoRepository).findById(1L);
    }

    @Test
    void buscarPorId_ProductoNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.buscarPorId(1L))
                .isInstanceOf(ProductoNoEncontradoException.class)
                .hasMessage("Producto no encontrado con ID: 1");

        verify(productoRepository).findById(1L);
    }

    @Test
    void buscarPorCategoria_CategoriasConProductos_DeberiaRetornarLista() {
        // Given
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByNombreCategoria("Electrónicos")).thenReturn(productos);

        // When
        List<Producto> resultado = productoService.buscarPorCategoria("Electrónicos");

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Laptop HP");
        verify(productoRepository).findByNombreCategoria("Electrónicos");
    }

    @Test
    void obtenerTodos_DeberiaRetornarTodosLosProductos() {
        // Given
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);

        // When
        List<Producto> resultado = productoService.obtenerTodos();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(producto);
        verify(productoRepository).findAll();
    }

    @Test
    void actualizar_ProductoExistente_DeberiaActualizarCorrectamente() {
        // Given
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.save(producto)).thenReturn(producto);

        // When
        Producto resultado = productoService.actualizar(1L, producto);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(productoRepository).existsById(1L);
        verify(productoRepository).save(producto);
    }

    @Test
    void actualizar_ProductoNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(productoRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> productoService.actualizar(1L, producto))
                .isInstanceOf(ProductoNoEncontradoException.class)
                .hasMessage("Producto no encontrado con ID: 1");

        verify(productoRepository).existsById(1L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void eliminar_ProductoExistente_DeberiaEliminarCorrectamente() {
        // Given
        when(productoRepository.existsById(1L)).thenReturn(true);

        // When
        productoService.eliminar(1L);

        // Then
        verify(productoRepository).existsById(1L);
        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminar_ProductoNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(productoRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> productoService.eliminar(1L))
                .isInstanceOf(ProductoNoEncontradoException.class)
                .hasMessage("Empleado no encontrado con ID: 1");

        verify(productoRepository).existsById(1L);
        verify(productoRepository, never()).deleteById(any(Long.class));
    }
}