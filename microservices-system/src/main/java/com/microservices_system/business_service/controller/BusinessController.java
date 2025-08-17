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

/**
 * Controlador REST para la capa de negocio del sistema de microservicios.
 * Expone endpoints relacionados con la lógica de negocio de productos, categorías e inventario.
 *
 * @author Reynier124
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api")
@Validated
public class BusinessController {

    private final ProductoBusinessService productoBusinessService;
    private final CategoriaBusinessService categoriaBusinessService;
    private final InventarioBusinessService inventarioBusinessService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param productoBusinessService Servicio de lógica de negocio para productos
     * @param categoriaBusinessService Servicio de lógica de negocio para categorías
     * @param inventarioBusinessService Servicio de lógica de negocio para inventario
     */
    public BusinessController(ProductoBusinessService productoBusinessService,
                              CategoriaBusinessService categoriaBusinessService,
                              InventarioBusinessService inventarioBusinessService) {
        this.productoBusinessService = productoBusinessService;
        this.categoriaBusinessService = categoriaBusinessService;
        this.inventarioBusinessService = inventarioBusinessService;
    }

    /**
     * Obtiene la lista completa de productos disponibles en el sistema.
     *
     * @return Lista de ProductoDTO con todos los productos registrados
     *
     * @apiNote
     * - URL: GET /api/productos
     * - Respuesta: 200 OK con lista de productos (puede estar vacía)
     * - Content-Type: application/json
     *
     * @example
     * GET /api/productos
     * Response: [
     *   {
     *     "id": 1,
     *     "nombre": "Laptop HP",
     *     "descripcion": "Laptop gaming 16GB RAM",
     *     "precio": 1299.99
     *   }
     * ]
     */
    @GetMapping("/productos")
    public List<ProductoDTO> obtenerTodosLosProductos() {
        return productoBusinessService.obtenerTodosLosProductos();
    }

    /**
     * Obtiene un producto específico por su ID.
     *
     * @param id Identificador único del producto
     * @return ProductoDTO con los datos del producto solicitado
     *
     * @apiNote
     * - URL: GET /api/productos/{id}
     * - Path Parameter: id (Long) - ID del producto
     * - Respuesta exitosa: 200 OK
     * - Respuesta error: 404 NOT FOUND si el producto no existe
     *
     * @example
     * GET /api/productos/1
     * Response: {
     *   "id": 1,
     *   "nombre": "Laptop HP",
     *   "descripcion": "Laptop gaming 16GB RAM",
     *   "precio": 1299.99
     * }
     */
    @GetMapping("/productos/{id}")
    public ProductoDTO obtenerProductoPorId(@PathVariable Long id) {
        return productoBusinessService.obtenerProductoPorId(id);
    }

    /**
     * Crea un nuevo producto en el sistema.
     *
     * @param request Datos del producto a crear
     * @return ProductoDTO con los datos del producto creado (incluye ID generado)
     *
     * @apiNote
     * - URL: POST /api/productos
     * - Body: ProductoRequest (JSON)
     * - Respuesta exitosa: 201 CREATED
     * - Content-Type: application/json
     *
     * @example
     * POST /api/productos
     * Body: {
     *   "nombre": "Mouse Logitech",
     *   "descripcion": "Mouse inalámbrico ergonómico",
     *   "precio": 45.99
     * }
     * Response: {
     *   "id": 2,
     *   "nombre": "Mouse Logitech",
     *   "descripcion": "Mouse inalámbrico ergonómico",
     *   "precio": 45.99
     * }
     */
    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO crearProducto(@RequestBody ProductoRequest request) {
        return productoBusinessService.crearProducto(request);
    }

    /**
     * Obtiene todos los productos que pertenecen a una categoría específica.
     *
     * @param nombre Nombre de la categoría
     * @return Lista de ProductoDTO de la categoría especificada
     *
     * @apiNote
     * - URL: GET /api/productos/categoria/{nombre}
     * - Path Parameter: nombre (String) - Nombre de la categoría
     * - Respuesta: 200 OK con lista de productos (puede estar vacía)
     * - Content-Type: application/json
     * - Nota: Búsqueda case-sensitive
     *
     * @example
     * GET /api/productos/categoria/Electronics
     * Response: [
     *   {
     *     "id": 1,
     *     "nombre": "Laptop HP",
     *     "descripcion": "Laptop gaming",
     *     "precio": 1299.99
     *   }
     * ]
     */
    @GetMapping("/productos/categoria/{nombre}")
    public List<ProductoDTO> obtenerProductosPorCategoria(@PathVariable String nombre) {
        return productoBusinessService.obtenerProductosPorCategoria(nombre);
    }

    /**
     * Genera un reporte de productos con stock bajo según criterios de negocio.
     * Útil para alertas de reposición y gestión de inventario.
     *
     * @return Lista de InventarioDTO con productos que requieren reposición
     *
     * @apiNote
     * - URL: GET /api/reportes/stock-bajo
     * - Respuesta: 200 OK con lista de inventarios con stock bajo
     * - Content-Type: application/json
     * - Criterio: Definido por reglas de negocio (ej: stock menor a 10)
     *
     * @example
     * GET /api/reportes/stock-bajo
     * Response: [
     *   {
     *     id: 1,
     *     producto: {
     *       id: 1,
     *       nombre: "Laptop HP",
     *       descripcion: "Laptop gaming",
     *       precio: 1299.99
     *     },
     *     cantidad: 3,
     *     stockMinimo: 10
     *   }
     * ]
     */
    @GetMapping("/reportes/stock-bajo")
    public List<InventarioDTO> obtenerProductosConStockBajo() {
        return inventarioBusinessService.obtenerProductosConStockBajo();
    }

    /**
     * Calcula y retorna el valor monetario total del inventario.
     * Suma todos los productos multiplicando cantidad por precio unitario.
     *
     * @return BigDecimal con el valor total del inventario
     *
     * @apiNote
     * - URL: GET /api/reportes/valor-inventario
     * - Respuesta: 200 OK con valor total como número
     * - Content-Type: application/json
     * - Formato: Número decimal con precisión monetaria
     *
     * @example
     * GET /api/reportes/valor-inventario
     * Response: 25750.50
     */
    @GetMapping("/reportes/valor-inventario")
    public BigDecimal obtenerValorTotalInventario() {
        return inventarioBusinessService.calcularValorTotalInventario();
    }
}
