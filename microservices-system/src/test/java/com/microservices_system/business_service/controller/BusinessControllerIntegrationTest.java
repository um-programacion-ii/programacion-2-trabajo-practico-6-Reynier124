package com.microservices_system.business_service.controller;

import com.microservices_system.business_service.dto.ProductoDTO;
import com.microservices_system.business_service.dto.ProductoRequest;
import com.microservices_system.business_service.service.CategoriaBusinessService;
import com.microservices_system.business_service.service.InventarioBusinessService;
import com.microservices_system.business_service.service.ProductoBusinessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BusinessControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ProductoBusinessService productoBusinessService;

    @MockBean
    private CategoriaBusinessService categoriaBusinessService;

    @MockBean
    private InventarioBusinessService inventarioBusinessService;

    @Test
    void cuandoCrearProducto_entoncesSePersisteCorrectamente() {
        // Arrange
        ProductoRequest producto = new ProductoRequest();
        producto.setNombre("Producto Test");
        producto.setDescripcion("Descripción de prueba");
        producto.setPrecio(BigDecimal.valueOf(100.50));

        ProductoDTO productoGuardado = new ProductoDTO();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("Producto Test");
        productoGuardado.setDescripcion("Descripción de prueba");
        productoGuardado.setPrecio(BigDecimal.valueOf(100.50));

        when(productoBusinessService.crearProducto(any(ProductoRequest.class)))
                .thenReturn(productoGuardado);

        // Act
        ResponseEntity<ProductoDTO> response = restTemplate.postForEntity(
                "/api/productos", producto, ProductoDTO.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Producto Test", response.getBody().getNombre());
    }

    @Test
    void cuandoObtenerTodosLosProductos_entoncesRetornaListaDeProductos() {
        // Arrange
        List<ProductoDTO> productosEsperados = Arrays.asList(
                createProductoDTO(1L, "Producto 1", "Descripción 1", BigDecimal.valueOf(50.0)),
                createProductoDTO(2L, "Producto 2", "Descripción 2", BigDecimal.valueOf(75.0)),
                createProductoDTO(3L, "Producto 3", "Descripción 3", BigDecimal.valueOf(100.0))
        );

        when(productoBusinessService.obtenerTodosLosProductos())
                .thenReturn(productosEsperados);

        // Act
        ResponseEntity<ProductoDTO[]> response = restTemplate.getForEntity(
                "/api/productos", ProductoDTO[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().length);
        assertEquals("Producto 1", response.getBody()[0].getNombre());
        assertEquals("Producto 2", response.getBody()[1].getNombre());
        assertEquals("Producto 3", response.getBody()[2].getNombre());

        verify(productoBusinessService).obtenerTodosLosProductos();
    }

    @Test
    void cuandoObtenerTodosLosProductos_yListaVacia_entoncesRetornaListaVacia() {
        // Arrange
        when(productoBusinessService.obtenerTodosLosProductos())
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ProductoDTO[]> response = restTemplate.getForEntity(
                "/api/productos", ProductoDTO[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);

        verify(productoBusinessService).obtenerTodosLosProductos();
    }

    @Test
    void cuandoObtenerProductoPorId_conIdExistente_entoncesRetornaProducto() {
        // Arrange
        Long productId = 1L;
        ProductoDTO productoEsperado = createProductoDTO(productId, "Producto Test", "Descripción Test", BigDecimal.valueOf(99.99));

        when(productoBusinessService.obtenerProductoPorId(productId))
                .thenReturn(productoEsperado);

        // Act
        ResponseEntity<ProductoDTO> response = restTemplate.getForEntity(
                "/api/productos/" + productId, ProductoDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getId());
        assertEquals("Producto Test", response.getBody().getNombre());
        assertEquals("Descripción Test", response.getBody().getDescripcion());
        assertEquals(BigDecimal.valueOf(99.99), response.getBody().getPrecio());

        verify(productoBusinessService).obtenerProductoPorId(productId);
    }

    @Test
    void cuandoCrearProducto_conDatosValidos_entoncesRetornaProductoCreado() {
        // Arrange
        ProductoRequest productoRequest = createProductoRequest("Producto Nuevo", "Descripción nueva", BigDecimal.valueOf(150.0));
        ProductoDTO productoCreado = createProductoDTO(1L, "Producto Nuevo", "Descripción nueva", BigDecimal.valueOf(150.0));

        when(productoBusinessService.crearProducto(any(ProductoRequest.class)))
                .thenReturn(productoCreado);

        // Act
        ResponseEntity<ProductoDTO> response = restTemplate.postForEntity(
                "/api/productos", productoRequest, ProductoDTO.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Producto Nuevo", response.getBody().getNombre());
        assertEquals("Descripción nueva", response.getBody().getDescripcion());
        assertEquals(BigDecimal.valueOf(150.0), response.getBody().getPrecio());

        verify(productoBusinessService).crearProducto(any(ProductoRequest.class));
    }


    @Test
    void cuandoCrearProducto_yServicioLanzaExcepcion_entoncesRetorna500() {
        // Arrange
        ProductoRequest productoRequest = createProductoRequest("Producto", "Descripción", BigDecimal.valueOf(100.0));

        when(productoBusinessService.crearProducto(any(ProductoRequest.class)))
                .thenThrow(new RuntimeException("Error interno del servicio"));

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/productos", productoRequest, String.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(productoBusinessService).crearProducto(any(ProductoRequest.class));
    }


    @Test
    void cuandoObtenerProductosPorCategoria_conCategoriaExistente_entoncesRetornaProductos() {
        // Arrange
        String nombreCategoria = "Electronics";
        List<ProductoDTO> productosDeCategoria = Arrays.asList(
                createProductoDTO(1L, "Laptop", "Laptop gaming", BigDecimal.valueOf(1500.0)),
                createProductoDTO(2L, "Mouse", "Mouse inalámbrico", BigDecimal.valueOf(25.0))
        );

        when(productoBusinessService.obtenerProductosPorCategoria(nombreCategoria))
                .thenReturn(productosDeCategoria);

        // Act
        ResponseEntity<ProductoDTO[]> response = restTemplate.getForEntity(
                "/api/productos/categoria/" + nombreCategoria, ProductoDTO[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        assertEquals("Laptop", response.getBody()[0].getNombre());
        assertEquals("Mouse", response.getBody()[1].getNombre());

        verify(productoBusinessService).obtenerProductosPorCategoria(nombreCategoria);
    }

    @Test
    void cuandoObtenerProductosPorCategoria_conCategoriaInexistente_entoncesRetornaListaVacia() {
        // Arrange
        String categoriaInexistente = "CategoriaInexistente";

        when(productoBusinessService.obtenerProductosPorCategoria(categoriaInexistente))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ProductoDTO[]> response = restTemplate.getForEntity(
                "/api/productos/categoria/" + categoriaInexistente, ProductoDTO[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);

        verify(productoBusinessService).obtenerProductosPorCategoria(categoriaInexistente);
    }

    @Test
    void cuandoObtenerProductosPorCategoria_yServicioLanzaExcepcion_entoncesRetorna500() {
        // Arrange
        String nombreCategoria = "Electronics";

        when(productoBusinessService.obtenerProductosPorCategoria(nombreCategoria))
                .thenThrow(new RuntimeException("Error al obtener productos por categoría"));

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/productos/categoria/" + nombreCategoria, String.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(productoBusinessService).obtenerProductosPorCategoria(nombreCategoria);
    }


    @Test
    void cuandoObtenerValorTotalInventario_entoncesRetornaValorTotal() {
        // Arrange
        BigDecimal valorTotalEsperado = BigDecimal.valueOf(25500.75);

        when(inventarioBusinessService.calcularValorTotalInventario())
                .thenReturn(valorTotalEsperado);

        // Act
        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
                "/api/reportes/valor-inventario", BigDecimal.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(valorTotalEsperado, response.getBody());

        verify(inventarioBusinessService).calcularValorTotalInventario();
    }

    @Test
    void cuandoObtenerValorTotalInventario_conInventarioVacio_entoncesRetornaCero() {
        // Arrange
        when(inventarioBusinessService.calcularValorTotalInventario())
                .thenReturn(BigDecimal.ZERO);

        // Act
        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
                "/api/reportes/valor-inventario", BigDecimal.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BigDecimal.ZERO, response.getBody());

        verify(inventarioBusinessService).calcularValorTotalInventario();
    }

    @Test
    void cuandoObtenerValorTotalInventario_yServicioLanzaExcepcion_entoncesRetorna500() {
        // Arrange
        when(inventarioBusinessService.calcularValorTotalInventario())
                .thenThrow(new RuntimeException("Error al calcular valor total"));

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/reportes/valor-inventario", String.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(inventarioBusinessService).calcularValorTotalInventario();
    }

    // ================== MÉTODOS HELPER ==================

    private ProductoDTO createProductoDTO(Long id, String nombre, String descripcion, BigDecimal precio) {
        ProductoDTO producto = new ProductoDTO();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        return producto;
    }

    private ProductoRequest createProductoRequest(String nombre, String descripcion, BigDecimal precio) {
        ProductoRequest request = new ProductoRequest();
        request.setNombre(nombre);
        request.setDescripcion(descripcion);
        request.setPrecio(precio);
        return request;
    }

}