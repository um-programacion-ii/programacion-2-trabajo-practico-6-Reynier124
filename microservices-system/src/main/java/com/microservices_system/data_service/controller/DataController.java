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

/**
 * Controlador REST para la capa de datos del sistema de microservicios.
 * Expone endpoints CRUD básicos para el acceso directo a entidades de datos.
 * Proporciona operaciones fundamentales sin lógica de negocio adicional.
 *
 * @author Reynier124
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/data")
@Validated
public class DataController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final InventarioService inventarioService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param productoService Servicio de datos para productos
     * @param categoriaService Servicio de datos para categorías
     * @param inventarioService Servicio de datos para inventario
     */
    public DataController(ProductoService productoService,
                          CategoriaService categoriaService,
                          InventarioService inventarioService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.inventarioService = inventarioService;
    }

    /**
     * Obtiene todos los productos almacenados en la base de datos.
     * Operación de lectura directa sin filtros ni lógica de negocio.
     *
     * @return Lista de entidades Producto
     *
     * @apiNote
     * - URL: GET /data/productos
     * - Respuesta: 200 OK con lista completa de productos
     * - Content-Type: application/json
     * - Nota: Retorna entidades completas, no DTOs
     *
     * @example
     * GET /data/productos
     * Response: [
     *   {
     *     "id": 1,
     *     "nombre": "Laptop HP",
     *     "descripcion": "Laptop gaming 16GB RAM",
     *     "precio": 1299.99,
     *     "categoria": {...},
     *     "fechaCreacion": "2025-01-15T10:30:00"
     *   }
     * ]
     */
    @GetMapping("/productos")
    public List<Producto> obtenerTodosLosProductos() {
        return productoService.obtenerTodos();
    }

    /**
     * Busca y retorna un producto por su identificador único.
     *
     * @param id Identificador único del producto
     * @return Entidad Producto con todos sus datos
     *
     * @apiNote
     * - URL: GET /data/productos/{id}
     * - Path Parameter: id (Long) - ID del producto
     * - Respuesta exitosa: 200 OK
     * - Respuesta error: 404 NOT FOUND
     *
     * @example
     * GET /data/productos/1
     * Response: {
     *   "id": 1,
     *   "nombre": "Laptop HP",
     *   "descripcion": "Laptop gaming 16GB RAM",
     *   "precio": 1299.99,
     *   "categoria": {...}
     * }
     */
    @GetMapping("/productos/{id}")
    public Producto obtenerProductoPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    /**
     * Crea un nuevo producto en la base de datos.
     * Operación de inserción directa con validaciones básicas.
     *
     * @param producto Entidad Producto a persistir
     * @return Producto guardado con ID generado y datos actualizados
     *
     * @apiNote
     * - URL: POST /data/productos
     * - Body: Producto (JSON) - Entidad completa
     * - Respuesta exitosa: 201 CREATED
     * - Respuesta error: 400 BAD REQUEST
     * - Nota: ID debe ser null o se ignorará
     *
     * @example
     * POST /data/productos
     * Body: {
     *   "nombre": "Teclado Mecánico",
     *   "descripcion": "Teclado RGB switch azul",
     *   "precio": 89.99,
     *   "categoria": { "id": 1 }
     * }
     */
    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    /**
     * Actualiza un producto existente en la base de datos.
     * Reemplaza completamente los datos del producto identificado por el ID.
     *
     * @param id Identificador del producto a actualizar
     * @param producto Nuevos datos del producto
     * @return Producto actualizado con los nuevos datos
     *
     * @apiNote
     * - URL: PUT /data/productos/{id}
     * - Path Parameter: id (Long) - ID del producto
     * - Body: Producto (JSON) - Datos actualizados
     * - Respuesta exitosa: 200 OK
     * - Respuesta error: 404 NOT FOUND
     * - Nota: Actualización completa, no parcial
     *
     * @example
     * PUT /data/productos/1
     * Body: {
     *   "nombre": "Laptop HP Actualizada",
     *   "descripcion": "Nueva descripción",
     *   "precio": 1399.99,
     *   "categoria": { "id": 2 }
     * }
     */
    @PutMapping("/productos/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return productoService.actualizar(id, producto);
    }

    /**
     * Elimina un producto de la base de datos de forma permanente.
     *
     * @param id Identificador del producto a eliminar
     *
     * @apiNote
     * - URL: DELETE /data/productos/{id}
     * - Path Parameter: id (Long) - ID del producto
     * - Respuesta exitosa: 204 NO CONTENT (sin body)
     * - Respuesta error: 404 NOT FOUND
     * - Advertencia: Operación irreversible
     *
     * @example
     * DELETE /data/productos/1
     * Response: 204 No Content (sin body)
     */
    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    /**
     * Busca productos que pertenecen a una categoría específica.
     * Filtrado directo por nombre de categoría.
     *
     * @param nombre Nombre exacto de la categoría
     * @return Lista de productos de la categoría especificada
     *
     * @apiNote
     * - URL: GET /data/productos/categoria/{nombre}
     * - Path Parameter: nombre (String) - Nombre de categoría
     * - Respuesta: 200 OK con lista de productos
     * - Content-Type: application/json
     * - Búsqueda: Case-sensitive, coincidencia exacta
     *
     * @example
     * GET /data/productos/categoria/Electronics
     * Response: [
     *   {
     *     "id": 1,
     *     "nombre": "Laptop HP",
     *     "categoria": { "nombre": "Electronics" }
     *   }
     * ]
     */
    @GetMapping("/productos/categoria/{nombre}")
    public List<Producto> obtenerProductosPorCategoria(@PathVariable String nombre) {
        return productoService.buscarPorCategoria(nombre);
    }

    /**
     * Obtiene el inventario de productos con stock bajo según criterios del servicio.
     * Consulta directa a la capa de datos sin procesamiento adicional.
     *
     * @return Lista de entidades Inventario con stock insuficiente
     *
     * @apiNote
     * - URL: GET /data/inventario/stock-bajo
     * - Respuesta: 200 OK con lista de inventarios
     * - Content-Type: application/json
     * - Criterio: Definido en capa de servicio de datos
     *
     * @example
     * GET /data/inventario/stock-bajo
     * Response: [
     *   {
     *     "id": 1,
     *     "producto": { "nombre": "Mouse" },
     *     "cantidad": 2,
     *     "stockMinimo": 10,
     *     "ubicacion": "A1-B2"
     *   }
     * ]
     */
    @GetMapping("/inventario/stock-bajo")
    public List<Inventario> obtenerProductosConStockBajo() {
        return inventarioService.obtenerProductosConStockBajo();
    }

    /**
     * Obtiene el registro completo del inventario almacenado.
     * Lista todos los registros de inventario sin filtros.
     *
     * @return Lista completa de entidades Inventario
     *
     * @apiNote
     * - URL: GET /data/inventario
     * - Respuesta: 200 OK con inventario completo
     * - Content-Type: application/json
     * - Advertencia: Puede retornar gran cantidad de datos
     *
     * @example
     * GET /data/inventario
     * Response: [
     *   {
     *     "id": 1,
     *     "producto": { "nombre": "Laptop" },
     *     "cantidad": 50,
     *     "stockMinimo": 10,
     *     "ubicacion": "A1-C3",
     *     "fechaActualizacion": "2025-01-15T14:22:00"
     *   }
     * ]
     */
    @GetMapping("/inventario")
    public List<Inventario> obtenerTodoElInventario() {
        return inventarioService.obtenerTodos();
    }
}