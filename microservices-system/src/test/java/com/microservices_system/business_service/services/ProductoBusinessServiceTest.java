package com.microservices_system.business_service.services;

import com.microservices_system.business_service.client.DataServiceClient;
import com.microservices_system.business_service.dto.ProductoDTO;
import com.microservices_system.business_service.dto.ProductoRequest;
import com.microservices_system.business_service.exceptions.MicroserviceCommunicationException;
import com.microservices_system.business_service.exceptions.ProductoNoEncontradoException;
import com.microservices_system.business_service.exceptions.ValidacionNegocioException;
import com.microservices_system.business_service.service.ProductoBusinessService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoBusinessServiceTest {

    @Mock
    private DataServiceClient dataServiceClient;

    @InjectMocks
    private ProductoBusinessService productoBusinessService;

    @Test
    void cuandoObtenerTodosLosProductos_entoncesRetornaLista() {
        // Arrange
        List<ProductoDTO> productosEsperados = Arrays.asList(
                new ProductoDTO(1L, "Producto 1", "Descripción 1", BigDecimal.valueOf(100), "Categoría 1", 10, false),
                new ProductoDTO(2L, "Producto 2", "Descripción 2", BigDecimal.valueOf(200), "Categoría 2", 5, true)
        );

        when(dataServiceClient.obtenerTodosLosProductos()).thenReturn(productosEsperados);

        // Act
        List<ProductoDTO> resultado = productoBusinessService.obtenerTodosLosProductos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Producto 1", resultado.get(0).getNombre());
        assertEquals("Producto 2", resultado.get(1).getNombre());
        verify(dataServiceClient).obtenerTodosLosProductos();
    }

    @Test
    void cuandoObtenerTodosLosProductos_yOcurreFeignException_entoncesLanzaMicroserviceCommunicationException() {
        // Arrange
        when(dataServiceClient.obtenerTodosLosProductos())
                .thenThrow(mock(FeignException.class));

        // Act & Assert
        assertThrows(MicroserviceCommunicationException.class, () -> {
            productoBusinessService.obtenerTodosLosProductos();
        });

        verify(dataServiceClient).obtenerTodosLosProductos();
    }

    @Test
    void cuandoObtenerProductoPorId_entoncesRetornaProducto() {
        // Arrange
        Long id = 1L;
        ProductoDTO productoEsperado = new ProductoDTO(id, "Producto 1", "Descripción 1",
                BigDecimal.valueOf(100), "Categoría 1", 10, false);

        when(dataServiceClient.obtenerProductoPorId(id)).thenReturn(productoEsperado);

        // Act
        ProductoDTO resultado = productoBusinessService.obtenerProductoPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Producto 1", resultado.getNombre());
        verify(dataServiceClient).obtenerProductoPorId(id);
    }

    @Test
    void cuandoObtenerProductoPorId_yProductoNoExiste_entoncesLanzaProductoNoEncontradoException() {
        // Arrange
        Long id = 1L;
        when(dataServiceClient.obtenerProductoPorId(id))
                .thenThrow(mock(FeignException.NotFound.class));

        // Act & Assert
        ProductoNoEncontradoException exception = assertThrows(ProductoNoEncontradoException.class, () -> {
            productoBusinessService.obtenerProductoPorId(id);
        });

        assertEquals("Producto no encontrado con ID: 1", exception.getMessage());
        verify(dataServiceClient).obtenerProductoPorId(id);
    }

    @Test
    void cuandoObtenerProductoPorId_yOcurreFeignException_entoncesLanzaMicroserviceCommunicationException() {
        // Arrange
        Long id = 1L;
        when(dataServiceClient.obtenerProductoPorId(id))
                .thenThrow(mock(FeignException.class));

        // Act & Assert
        assertThrows(MicroserviceCommunicationException.class, () -> {
            productoBusinessService.obtenerProductoPorId(id);
        });

        verify(dataServiceClient).obtenerProductoPorId(id);
    }

    @Test
    void cuandoCrearProductoValido_entoncesRetornaProductoCreado() {
        // Arrange
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Producto Test");
        request.setPrecio(BigDecimal.valueOf(100));
        request.setStock(10);

        ProductoDTO productoCreado = new ProductoDTO(1L, "Producto Test", "Descripción",
                BigDecimal.valueOf(100), "Categoría", 10, false);

        when(dataServiceClient.crearProducto(request)).thenReturn(productoCreado);

        // Act
        ProductoDTO resultado = productoBusinessService.crearProducto(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("Producto Test", resultado.getNombre());
        verify(dataServiceClient).crearProducto(request);
    }

    @Test
    void cuandoCrearProductoConPrecioInvalido_entoncesLanzaExcepcion() {
        // Arrange
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Producto Test");
        request.setPrecio(BigDecimal.valueOf(-10));
        request.setStock(5);

        // Act & Assert
        ValidacionNegocioException exception = assertThrows(ValidacionNegocioException.class, () -> {
            productoBusinessService.crearProducto(request);
        });

        assertEquals("El precio debe ser mayor a cero", exception.getMessage());
        verify(dataServiceClient, never()).crearProducto(any());
    }

    @Test
    void cuandoCrearProductoConPrecioCero_entoncesLanzaExcepcion() {
        // Arrange
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Producto Test");
        request.setPrecio(BigDecimal.ZERO);
        request.setStock(5);

        // Act & Assert
        ValidacionNegocioException exception = assertThrows(ValidacionNegocioException.class, () -> {
            productoBusinessService.crearProducto(request);
        });

        assertEquals("El precio debe ser mayor a cero", exception.getMessage());
        verify(dataServiceClient, never()).crearProducto(any());
    }

    @Test
    void cuandoCrearProductoConStockNegativo_entoncesLanzaExcepcion() {
        // Arrange
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Producto Test");
        request.setPrecio(BigDecimal.valueOf(100));
        request.setStock(-5);

        // Act & Assert
        ValidacionNegocioException exception = assertThrows(ValidacionNegocioException.class, () -> {
            productoBusinessService.crearProducto(request);
        });

        assertEquals("El stock no puede ser negativo", exception.getMessage());
        verify(dataServiceClient, never()).crearProducto(any());
    }

    @Test
    void cuandoCrearProducto_yOcurreFeignException_entoncesLanzaMicroserviceCommunicationException() {
        // Arrange
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Producto Test");
        request.setPrecio(BigDecimal.valueOf(100));
        request.setStock(10);

        when(dataServiceClient.crearProducto(request))
                .thenThrow(mock(FeignException.class));

        // Act & Assert
        assertThrows(MicroserviceCommunicationException.class, () -> {
            productoBusinessService.crearProducto(request);
        });

        verify(dataServiceClient).crearProducto(request);
    }

    @Test
    void cuandoObtenerProductosPorCategoria_entoncesRetornaLista() {
        // Arrange
        String categoria = "Electrónicos";
        List<ProductoDTO> productosEsperados = Arrays.asList(
                new ProductoDTO(1L, "Laptop", "Laptop HP", BigDecimal.valueOf(1500), categoria, 5, false),
                new ProductoDTO(2L, "Mouse", "Mouse inalámbrico", BigDecimal.valueOf(50), categoria, 20, false)
        );

        when(dataServiceClient.obtenerProductosPorCategoria(categoria)).thenReturn(productosEsperados);

        // Act
        List<ProductoDTO> resultado = productoBusinessService.obtenerProductosPorCategoria(categoria);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());
        assertEquals("Mouse", resultado.get(1).getNombre());
        verify(dataServiceClient).obtenerProductosPorCategoria(categoria);
    }

    @Test
    void cuandoObtenerProductosPorCategoria_yOcurreFeignException_entoncesLanzaMicroserviceCommunicationException() {
        // Arrange
        String categoria = "Electrónicos";
        when(dataServiceClient.obtenerProductosPorCategoria(categoria))
                .thenThrow(mock(FeignException.class));

        // Act & Assert
        assertThrows(MicroserviceCommunicationException.class, () -> {
            productoBusinessService.obtenerProductosPorCategoria(categoria);
        });

        verify(dataServiceClient).obtenerProductosPorCategoria(categoria);
    }

    @Test
    void cuandoObtenerProductosPorCategoria_yNoHayProductos_entoncesRetornaListaVacia() {
        // Arrange
        String categoria = "Categoría Sin Productos";
        when(dataServiceClient.obtenerProductosPorCategoria(categoria))
                .thenReturn(Collections.emptyList());

        // Act
        List<ProductoDTO> resultado = productoBusinessService.obtenerProductosPorCategoria(categoria);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(dataServiceClient).obtenerProductosPorCategoria(categoria);
    }
}