package com.microservices_system.business_service.services;
import com.microservices_system.business_service.client.DataServiceClient;
import com.microservices_system.business_service.dto.InventarioDTO;
import com.microservices_system.business_service.dto.ProductoDTO;
import com.microservices_system.business_service.exceptions.MicroserviceCommunicationException;
import com.microservices_system.business_service.service.InventarioBusinessService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioBusinessServiceTest {

    @Mock
    private DataServiceClient dataServiceClient;

    @InjectMocks
    private InventarioBusinessService inventarioBusinessService;

    @Test
    void cuandoObtenerProductosConStockBajo_entoncesRetornaLista() {
        // Arrange
        List<InventarioDTO> inventariosEsperados = Arrays.asList(
                crearInventarioDTO(1L, 3, 5, "Producto 1", BigDecimal.valueOf(100)),
                crearInventarioDTO(2L, 2, 10, "Producto 2", BigDecimal.valueOf(200))
        );

        when(dataServiceClient.obtenerProductosConStockBajo()).thenReturn(inventariosEsperados);

        // Act
        List<InventarioDTO> resultado = inventarioBusinessService.obtenerProductosConStockBajo();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(3, resultado.get(0).getCantidad());
        assertEquals(2, resultado.get(1).getCantidad());
        verify(dataServiceClient).obtenerProductosConStockBajo();
    }

    @Test
    void cuandoObtenerProductosConStockBajo_yOcurreFeignException_entoncesLanzaMicroserviceCommunicationException() {
        // Arrange
        when(dataServiceClient.obtenerProductosConStockBajo())
                .thenThrow(mock(FeignException.class));

        // Act & Assert
        assertThrows(MicroserviceCommunicationException.class, () -> {
            inventarioBusinessService.obtenerProductosConStockBajo();
        });

        verify(dataServiceClient).obtenerProductosConStockBajo();
    }

    @Test
    void cuandoCalcularValorTotalInventario_entoncesRetornaValorCorrecto() {
        // Arrange
        List<InventarioDTO> inventarios = Arrays.asList(
                crearInventarioDTO(1L, 10, 5, "Producto 1", BigDecimal.valueOf(100)), // 10 * 100 = 1000
                crearInventarioDTO(2L, 5, 3, "Producto 2", BigDecimal.valueOf(200)),  // 5 * 200 = 1000
                crearInventarioDTO(3L, 0, 2, "Producto 3", BigDecimal.valueOf(50))    // 0 * 50 = 0 (sin stock)
        );

        when(dataServiceClient.obtenerTodosLosInventarios()).thenReturn(inventarios);

        // Act
        BigDecimal resultado = inventarioBusinessService.calcularValorTotalInventario();

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("2000"), resultado);
        verify(dataServiceClient).obtenerTodosLosInventarios();
    }

    @Test
    void cuandoCalcularValorTotalInventario_conProductosSinPrecio_entoncesOmiteDelCalculo() {
        // Arrange
        List<InventarioDTO> inventarios = Arrays.asList(
                crearInventarioDTO(1L, 10, 5, "Producto 1", BigDecimal.valueOf(100)), // 10 * 100 = 1000
                crearInventarioDTO(2L, 5, 3, "Producto 2", null)  // Sin precio, se omite
        );

        when(dataServiceClient.obtenerTodosLosInventarios()).thenReturn(inventarios);

        // Act
        BigDecimal resultado = inventarioBusinessService.calcularValorTotalInventario();

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("1000"), resultado);
        verify(dataServiceClient).obtenerTodosLosInventarios();
    }

    @Test
    void cuandoCalcularValorTotalInventario_conInventariosSinProducto_entoncesOmiteDelCalculo() {
        // Arrange
        InventarioDTO inventarioSinProducto = new InventarioDTO();
        inventarioSinProducto.setId(1L);
        inventarioSinProducto.setCantidad(10);
        inventarioSinProducto.setProducto(null);

        List<InventarioDTO> inventarios = Arrays.asList(
                inventarioSinProducto,
                crearInventarioDTO(2L, 5, 3, "Producto 2", BigDecimal.valueOf(200))
        );

        when(dataServiceClient.obtenerTodosLosInventarios()).thenReturn(inventarios);

        // Act
        BigDecimal resultado = inventarioBusinessService.calcularValorTotalInventario();

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("1000"), resultado);
        verify(dataServiceClient).obtenerTodosLosInventarios();
    }

    @Test
    void cuandoCalcularValorTotalInventario_conInventarioVacio_entoncesRetornaCero() {
        // Arrange
        when(dataServiceClient.obtenerTodosLosInventarios())
                .thenReturn(Collections.emptyList());

        // Act
        BigDecimal resultado = inventarioBusinessService.calcularValorTotalInventario();

        // Assert
        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, resultado);
        verify(dataServiceClient).obtenerTodosLosInventarios();
    }

    @Test
    void cuandoCalcularValorTotalInventario_yOcurreFeignException_entoncesLanzaMicroserviceCommunicationException() {
        // Arrange
        when(dataServiceClient.obtenerTodosLosInventarios())
                .thenThrow(mock(FeignException.class));

        // Act & Assert
        assertThrows(MicroserviceCommunicationException.class, () -> {
            inventarioBusinessService.calcularValorTotalInventario();
        });

        verify(dataServiceClient).obtenerTodosLosInventarios();
    }

    @Test
    void cuandoCalcularValorTotalInventario_conCantidadNull_entoncesOmiteDelCalculo() {
        // Arrange
        InventarioDTO inventarioConCantidadNull = crearInventarioDTO(1L, null, 5, "Producto 1", BigDecimal.valueOf(100));
        List<InventarioDTO> inventarios = Arrays.asList(
                inventarioConCantidadNull,
                crearInventarioDTO(2L, 5, 3, "Producto 2", BigDecimal.valueOf(200))
        );

        when(dataServiceClient.obtenerTodosLosInventarios()).thenReturn(inventarios);

        // Act
        BigDecimal resultado = inventarioBusinessService.calcularValorTotalInventario();

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("1000"), resultado);
        verify(dataServiceClient).obtenerTodosLosInventarios();
    }

    private InventarioDTO crearInventarioDTO(Long id, Integer cantidad, Integer stockMinimo,
                                             String nombreProducto, BigDecimal precio) {
        ProductoDTO producto = new ProductoDTO();
        producto.setId(id);
        producto.setNombre(nombreProducto);
        producto.setPrecio(precio);

        InventarioDTO inventario = new InventarioDTO();
        inventario.setId(id);
        inventario.setCantidad(cantidad);
        inventario.setStockMinimo(stockMinimo);
        inventario.setProducto(producto);
        inventario.setFechaActualizacion(LocalDateTime.now());

        return inventario;
    }
}