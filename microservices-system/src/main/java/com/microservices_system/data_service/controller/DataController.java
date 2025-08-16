package com.microservices_system.data_service.controller;

import com.microservices_system.data_service.entity.Inventario;
import com.microservices_system.data_service.entity.Producto;
import com.microservices_system.data_service.services.CategoriaService;
import com.microservices_system.data_service.services.InventarioService;
import com.microservices_system.data_service.services.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data")
@Validated
public class DataController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final InventarioService inventarioService;

    public DataController(ProductoService productoService,
                          CategoriaService categoriaService,
                          InventarioService inventarioService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/productos")
    public List<Producto> obtenerTodosLosProductos() {
        return productoService.obtenerTodos();
    }

    @GetMapping("/productos/{id}")
    public Producto obtenerProductoPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @PutMapping("/productos/{id}")
    public Producto actualizarProducto(@PathVariable Long id,@RequestBody Producto producto) {
        return productoService.actualizar(id, producto);
    }

    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    @GetMapping("/productos/categoria/{nombre}")
    public List<Producto> obtenerProductosPorCategoria(@PathVariable String nombre) {
        return productoService.buscarPorCategoria(nombre);
    }

    @GetMapping("/inventario/stock-bajo")
    public List<Inventario> obtenerProductosConStockBajo() {
        return inventarioService.obtenerProductosConStockBajo();
    }

    @GetMapping("/inventario")
    public List<Inventario> obtenerTodoElInventario() {
        return inventarioService.obtenerTodos();
    }

}
