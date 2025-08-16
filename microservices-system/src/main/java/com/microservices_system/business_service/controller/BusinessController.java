package com.microservices_system.business_service.controller;

import com.microservices_system.business_service.dto.InventarioDTO;
import com.microservices_system.business_service.dto.ProductoDTO;
import com.microservices_system.business_service.dto.ProductoRequest;
import com.microservices_system.business_service.service.CategoriaBusinessService;
import com.microservices_system.business_service.service.InventarioBusinessService;
import com.microservices_system.business_service.service.ProductoBusinessService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class BusinessController {

    private final ProductoBusinessService productoBusinessService;
    private final CategoriaBusinessService categoriaBusinessService;
    private final InventarioBusinessService inventarioBusinessService;

    public BusinessController(ProductoBusinessService productoBusinessService,
                              CategoriaBusinessService categoriaBusinessService,
                              InventarioBusinessService inventarioBusinessService) {
        this.productoBusinessService = productoBusinessService;
        this.categoriaBusinessService = categoriaBusinessService;
        this.inventarioBusinessService = inventarioBusinessService;
    }

    @GetMapping("/productos")
    public List<ProductoDTO> obtenerTodosLosProductos() {
        return productoBusinessService.obtenerTodosLosProductos();
    }

    @GetMapping("/productos/{id}")
    public ProductoDTO obtenerProductoPorId(@PathVariable Long id) {
        return productoBusinessService.obtenerProductoPorId(id);
    }

    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO crearProducto(@RequestBody ProductoRequest request) {
        return productoBusinessService.crearProducto(request);
    }

    @GetMapping("/productos/categoria/{nombre}")
    public List<ProductoDTO> obtenerProductosPorCategoria(@PathVariable String nombre) {
        return productoBusinessService.obtenerProductosPorCategoria(nombre);
    }

    @GetMapping("/reportes/stock-bajo")
    public List<InventarioDTO> obtenerProductosConStockBajo() {
        return inventarioBusinessService.obtenerProductosConStockBajo();
    }

    @GetMapping("/reportes/valor-inventario")
    public BigDecimal obtenerValorTotalInventario() {
        return inventarioBusinessService.calcularValorTotalInventario();
    }
}
