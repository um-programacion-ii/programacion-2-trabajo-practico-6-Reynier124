package com.microservices_system.data_service.services;

import com.microservices_system.business_service.exceptions.InventarioNoEncontradoException;
import com.microservices_system.data_service.entity.Inventario;
import com.microservices_system.data_service.entity.Producto;
import com.microservices_system.data_service.repositories.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventario;
    private Producto producto;
    private LocalDateTime fechaFija;

    @BeforeEach
    void setUp() {
        fechaFija = LocalDateTime.of(2024, 1, 15, 10, 30);

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop HP");

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(10);
        inventario.setStockMinimo(5);
        inventario.setFechaActualizacion(fechaFija);
    }

    @Test
    void guardar_InventarioNuevo_DeberiaAsignarFechaYGuardar() {
        // Given
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fechaFija);
            when(inventarioRepository.save(inventario)).thenReturn(inventario);

            // When
            Inventario resultado = inventarioService.guardar(inventario);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getFechaActualizacion()).isEqualTo(fechaFija);
            verify(inventarioRepository).save(inventario);
        }
    }

    @Test
    void buscarPorId_InventarioExistente_DeberiaRetornarInventario() {
        // Given
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        // When
        Inventario resultado = inventarioService.buscarPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(inventarioRepository).findById(1L);
    }

    @Test
    void buscarPorId_InventarioNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(inventarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> inventarioService.buscarPorId(1L))
                .isInstanceOf(InventarioNoEncontradoException.class)
                .hasMessage("Inventario no encontrado con ID: 1");

        verify(inventarioRepository).findById(1L);
    }

    @Test
    void buscarPorProductoId_InventarioExistente_DeberiaRetornarInventario() {
        // Given
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));

        // When
        Inventario resultado = inventarioService.buscarPorProductoId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getProducto().getId()).isEqualTo(1L);
        verify(inventarioRepository).findByProductoId(1L);
    }

    @Test
    void buscarPorProductoId_InventarioNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> inventarioService.buscarPorProductoId(1L))
                .isInstanceOf(InventarioNoEncontradoException.class)
                .hasMessage("Inventario no encontrado para producto ID: 1");

        verify(inventarioRepository).findByProductoId(1L);
    }

    @Test
    void obtenerTodos_DeberiaRetornarTodosLosInventarios() {
        // Given
        List<Inventario> inventarios = Arrays.asList(inventario);
        when(inventarioRepository.findAll()).thenReturn(inventarios);

        // When
        List<Inventario> resultado = inventarioService.obtenerTodos();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(inventario);
        verify(inventarioRepository).findAll();
    }

    @Test
    void obtenerProductosConStockBajo_DeberiaRetornarListaInventarios() {
        // Given
        List<Inventario> inventarios = Arrays.asList(inventario);
        when(inventarioRepository.findInventariosConStockBajo()).thenReturn(inventarios);

        // When
        List<Inventario> resultado = inventarioService.obtenerProductosConStockBajo();

        // Then
        assertThat(resultado).hasSize(1);
        verify(inventarioRepository).findInventariosConStockBajo();
    }

    @Test
    void actualizar_InventarioExistente_DeberiaActualizarConFecha() {
        // Given
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fechaFija);
            when(inventarioRepository.existsById(1L)).thenReturn(true);
            when(inventarioRepository.save(inventario)).thenReturn(inventario);

            // When
            Inventario resultado = inventarioService.actualizar(1L, inventario);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getFechaActualizacion()).isEqualTo(fechaFija);
            verify(inventarioRepository).existsById(1L);
            verify(inventarioRepository).save(inventario);
        }
    }

    @Test
    void actualizar_InventarioNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(inventarioRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> inventarioService.actualizar(1L, inventario))
                .isInstanceOf(InventarioNoEncontradoException.class)
                .hasMessage("Inventario no encontrado con ID: 1");

        verify(inventarioRepository).existsById(1L);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void eliminar_InventarioExistente_DeberiaEliminarCorrectamente() {
        // Given
        when(inventarioRepository.existsById(1L)).thenReturn(true);

        // When
        inventarioService.eliminar(1L);

        // Then
        verify(inventarioRepository).existsById(1L);
        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void eliminar_InventarioNoExistente_DeberiaLanzarExcepcion() {
        // Given
        when(inventarioRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> inventarioService.eliminar(1L))
                .isInstanceOf(InventarioNoEncontradoException.class)
                .hasMessage("Inventario no encontrado con ID: 1");

        verify(inventarioRepository).existsById(1L);
        verify(inventarioRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void actualizarCantidad_CantidadValida_DeberiaActualizarCorrectamente() {
        // Given
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fechaFija);
            when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));
            when(inventarioRepository.save(inventario)).thenReturn(inventario);

            // When
            Inventario resultado = inventarioService.actualizarCantidad(1L, 15);

            // Then
            assertThat(resultado.getCantidad()).isEqualTo(15);
            assertThat(resultado.getFechaActualizacion()).isEqualTo(fechaFija);
            verify(inventarioRepository).findByProductoId(1L);
            verify(inventarioRepository).save(inventario);
        }
    }

    @Test
    void actualizarCantidad_CantidadNegativa_DeberiaLanzarExcepcion() {
        // When & Then
        assertThatThrownBy(() -> inventarioService.actualizarCantidad(1L, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La cantidad no puede ser negativa");

        verify(inventarioRepository, never()).findByProductoId(any(Long.class));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void tieneStockSuficiente_ConStockSuficiente_DeberiaRetornarTrue() {
        // Given
        when(inventarioRepository.tieneStockSuficiente(1L, 5)).thenReturn(true);

        // When
        boolean resultado = inventarioService.tieneStockSuficiente(1L, 5);

        // Then
        assertThat(resultado).isTrue();
        verify(inventarioRepository).tieneStockSuficiente(1L, 5);
    }

    @Test
    void tieneStockSuficiente_SinStockSuficiente_DeberiaRetornarFalse() {
        // Given
        when(inventarioRepository.tieneStockSuficiente(1L, 15)).thenReturn(false);

        // When
        boolean resultado = inventarioService.tieneStockSuficiente(1L, 15);

        // Then
        assertThat(resultado).isFalse();
        verify(inventarioRepository).tieneStockSuficiente(1L, 15);
    }

    @Test
    void tieneStockSuficiente_ResultadoNull_DeberiaRetornarFalse() {
        // Given
        when(inventarioRepository.tieneStockSuficiente(1L, 5)).thenReturn(null);

        // When
        boolean resultado = inventarioService.tieneStockSuficiente(1L, 5);

        // Then
        assertThat(resultado).isFalse();
        verify(inventarioRepository).tieneStockSuficiente(1L, 5);
    }

    @Test
    void obtenerProductosSinStock_DeberiaRetornarInventariosConCantidadCero() {
        // Given
        inventario.setCantidad(0);
        List<Inventario> inventarios = Arrays.asList(inventario);
        when(inventarioRepository.findByCantidad(0)).thenReturn(inventarios);

        // When
        List<Inventario> resultado = inventarioService.obtenerProductosSinStock();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCantidad()).isZero();
        verify(inventarioRepository).findByCantidad(0);
    }

    @Test
    void obtenerTodosConProducto_DeberiaRetornarInventariosConProductos() {
        // Given
        List<Inventario> inventarios = Arrays.asList(inventario);
        when(inventarioRepository.findAllWithProducto()).thenReturn(inventarios);

        // When
        List<Inventario> resultado = inventarioService.obtenerTodosConProducto();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProducto()).isNotNull();
        verify(inventarioRepository).findAllWithProducto();
    }
}

