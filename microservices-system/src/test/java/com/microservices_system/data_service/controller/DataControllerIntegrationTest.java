package com.microservices_system.data_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices_system.business_service.exceptions.ProductoNoEncontradoException;
import com.microservices_system.data_service.entity.Inventario;
import com.microservices_system.data_service.entity.Producto;
import com.microservices_system.data_service.services.CategoriaService;
import com.microservices_system.data_service.services.InventarioService;
import com.microservices_system.data_service.services.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DataController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class DataControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private InventarioService inventarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void cuandoCrearProducto_entoncesSePersisteCorrectamente() throws Exception {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setDescripcion("Descripción de prueba");
        producto.setPrecio(BigDecimal.valueOf(100.50));

        Producto productoGuardado = new Producto();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("Producto Test");
        productoGuardado.setDescripcion("Descripción de prueba");
        productoGuardado.setPrecio(BigDecimal.valueOf(100.50));

        when(productoService.guardar(any(Producto.class))).thenReturn(productoGuardado);

        // Act & Assert
        mockMvc.perform(post("/data/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Producto Test"))
                .andExpect(jsonPath("$.descripcion").value("Descripción de prueba"))
                .andExpect(jsonPath("$.precio").value(100.50));

        verify(productoService).guardar(any(Producto.class));
    }

    @Test
    void cuandoBuscarProductoInexistente_entoncesRetorna404() throws Exception {
        // Arrange
        when(productoService.buscarPorId(999L)).thenThrow(new ProductoNoEncontradoException("Producto no encontrado con ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/data/productos/999"))
                .andExpect(status().isNotFound());

        verify(productoService).buscarPorId(999L);
    }

    @Test
    void cuandoObtenerTodosLosProductos_entoncesRetornaLista() throws Exception {
        // Arrange
        Producto producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Producto 1");
        producto1.setDescripcion("Descripción 1");
        producto1.setPrecio(BigDecimal.valueOf(50.00));

        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Producto 2");
        producto2.setDescripcion("Descripción 2");
        producto2.setPrecio(BigDecimal.valueOf(75.00));

        List<Producto> productos = Arrays.asList(producto1, producto2);
        when(productoService.obtenerTodos()).thenReturn(productos);

        // Act & Assert
        mockMvc.perform(get("/data/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Producto 1"))
                .andExpect(jsonPath("$[1].nombre").value("Producto 2"));

        verify(productoService).obtenerTodos();
    }

    @Test
    void cuandoObtenerProductoPorId_entoncesRetornaProducto() throws Exception {
        // Arrange
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test ID");
        producto.setDescripcion("Descripción para buscar por ID");
        producto.setPrecio(BigDecimal.valueOf(120.00));

        when(productoService.buscarPorId(1L)).thenReturn(producto);

        // Act & Assert
        mockMvc.perform(get("/data/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Producto Test ID"))
                .andExpect(jsonPath("$.descripcion").value("Descripción para buscar por ID"))
                .andExpect(jsonPath("$.precio").value(120.00));

        verify(productoService).buscarPorId(1L);
    }

    @Test
    void cuandoActualizarProducto_entoncesSeModificaCorrectamente() throws Exception {
        // Arrange
        Producto productoActualizado = new Producto();
        productoActualizado.setNombre("Producto Actualizado");
        productoActualizado.setDescripcion("Descripción actualizada");
        productoActualizado.setPrecio(BigDecimal.valueOf(150.00));

        Producto productoResultado = new Producto();
        productoResultado.setId(1L);
        productoResultado.setNombre("Producto Actualizado");
        productoResultado.setDescripcion("Descripción actualizada");
        productoResultado.setPrecio(BigDecimal.valueOf(150.00));

        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(productoResultado);

        // Act & Assert
        mockMvc.perform(put("/data/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Producto Actualizado"))
                .andExpect(jsonPath("$.descripcion").value("Descripción actualizada"))
                .andExpect(jsonPath("$.precio").value(150.00));

        verify(productoService).actualizar(eq(1L), any(Producto.class));
    }

    @Test
    void cuandoEliminarProducto_entoncesSeEliminaCorrectamente() throws Exception {
        // Arrange
        doNothing().when(productoService).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/data/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }

    @Test
    void cuandoObtenerProductosPorCategoria_entoncesRetornaProductosDeLaCategoria() throws Exception {
        // Arrange
        String nombreCategoria = "Electrónicos";

        Producto producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Laptop");
        producto1.setDescripcion("Laptop gaming");
        producto1.setPrecio(BigDecimal.valueOf(1500.00));

        List<Producto> productos = Arrays.asList(producto1);
        when(productoService.buscarPorCategoria(nombreCategoria)).thenReturn(productos);

        // Act & Assert
        mockMvc.perform(get("/data/productos/categoria/" + nombreCategoria))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("Laptop"));

        verify(productoService).buscarPorCategoria(nombreCategoria);
    }

    @Test
    void cuandoObtenerProductosConStockBajo_entoncesRetornaInventarioConStockBajo() throws Exception {
        // Arrange
        Inventario inventario1 = new Inventario();
        inventario1.setId(1L);
        inventario1.setCantidad(5);
        inventario1.setStockMinimo(10);

        List<Inventario> inventarios = Arrays.asList(inventario1);
        when(inventarioService.obtenerProductosConStockBajo()).thenReturn(inventarios);

        // Act & Assert
        mockMvc.perform(get("/data/inventario/stock-bajo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cantidad").value(5))
                .andExpect(jsonPath("$[0].stockMinimo").value(10));

        verify(inventarioService).obtenerProductosConStockBajo();
    }

    @Test
    void cuandoObtenerTodoElInventario_entoncesRetornaTodosLosInventarios() throws Exception {
        // Arrange
        Inventario inventario1 = new Inventario();
        inventario1.setId(1L);
        inventario1.setCantidad(20);
        inventario1.setStockMinimo(10);

        Inventario inventario2 = new Inventario();
        inventario2.setId(2L);
        inventario2.setCantidad(15);
        inventario2.setStockMinimo(5);

        List<Inventario> inventarios = Arrays.asList(inventario1, inventario2);
        when(inventarioService.obtenerTodos()).thenReturn(inventarios);

        // Act & Assert
        mockMvc.perform(get("/data/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cantidad").value(20))
                .andExpect(jsonPath("$[1].cantidad").value(15));

        verify(inventarioService).obtenerTodos();
    }


    @Test
    void cuandoActualizarProductoInexistente_entoncesRetorna404() throws Exception {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Producto Inexistente");
        producto.setDescripcion("No existe");
        producto.setPrecio(BigDecimal.valueOf(100.00));

        when(productoService.actualizar(eq(999L), any(Producto.class)))
                .thenThrow(new ProductoNoEncontradoException("Producto no encontrado con ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/data/productos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isNotFound());

        verify(productoService).actualizar(eq(999L), any(Producto.class));
    }

    @Test
    void cuandoEliminarProductoInexistente_entoncesRetorna404() throws Exception {
        // Arrange
        doThrow(new ProductoNoEncontradoException("Producto no encontrado con ID: 999"))
                .when(productoService).eliminar(999L);

        // Act & Assert
        mockMvc.perform(delete("/data/productos/999"))
                .andExpect(status().isNotFound());

        verify(productoService).eliminar(999L);
    }
}
