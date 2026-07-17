package com.minimarket.controller;

import jakarta.validation.Valid;

import com.minimarket.entity.Producto;
import com.minimarket.exception.ErrorResponse;
import com.minimarket.dto.producto.ProductoResponseDto;
import com.minimarket.mapper.ProductoMapper;
import com.minimarket.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.links.Link;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "API de gestion de productos, incluye CRUD de productos.")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

        @Autowired
        private ProductoService productoService;

        @Autowired
        private ProductoMapper productoMapper;

        @GetMapping
        @Operation(summary = "Listar todos los productos", description = "Retorna una lista de todos los productos")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Se encontraron productos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDto[].class))),
                        @ApiResponse(responseCode = "404", description = "No se encontraron productos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<CollectionModel<EntityModel<ProductoResponseDto>>> listarProductos() {
                List<EntityModel<ProductoResponseDto>> productos = productoService.findAll().stream()
                                .map(producto -> EntityModel.of(productoMapper.toResponse(producto),
                                                linkTo(methodOn(ProductoController.class)
                                                                .obtenerProductoPorId(producto.getId())).withSelfRel(),
                                                linkTo(methodOn(ProductoController.class)
                                                                .actualizarProducto(producto.getId(), producto))
                                                                .withRel("update"),
                                                linkTo(methodOn(ProductoController.class)
                                                                .eliminarProducto(producto.getId()))
                                                                .withRel("delete")))
                                .toList();
                return ResponseEntity.ok(CollectionModel.of(productos));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener producto por ID", description = "Retorna un producto por su ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Se encontro el producto", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDto.class)), links = {
                                        @Link(name = "self", description = "Enlace al recurso del producto obtenido", operationId = "obtenerProductoPorId"),
                                        @Link(name = "allProductos", description = "Enlace a la lista de todos los productos", operationId = "listarProductos"),
                                        @Link(name = "update", description = "Enlace para actualizar el producto", operationId = "actualizarProducto")
                        }),
                        @ApiResponse(responseCode = "404", description = "No se encontro el producto", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<EntityModel<ProductoResponseDto>> obtenerProductoPorId(
                        @Parameter(description = "Introducir ID del producto", required = true) @PathVariable Long id) {
                Producto producto = productoService.findById(id);
                EntityModel<ProductoResponseDto> entityModel = EntityModel.of(productoMapper.toResponse(producto),
                                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId()))
                                                .withSelfRel(),
                                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("allProductos"),
                                linkTo(methodOn(ProductoController.class).actualizarProducto(producto.getId(),
                                                producto))
                                                .withRel("update"),
                                linkTo(methodOn(ProductoController.class).eliminarProducto(producto.getId()))
                                                .withRel("delete"));

                return ResponseEntity.ok(entityModel);
        }

        @PostMapping
        @Operation(summary = "Guardar producto", description = "Recibe un objeto de tipo Producto y lo guarda en la base de datos.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Producto guardado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
        public ResponseEntity<EntityModel<ProductoResponseDto>> guardarProducto(
                        @Parameter(description = "Introducir objeto de tipo Producto a guardar", required = true) @Valid @RequestBody Producto producto) {
                Producto savedProducto = productoService.save(producto);
                EntityModel<ProductoResponseDto> entityModel = EntityModel.of(productoMapper.toResponse(savedProducto),
                                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(savedProducto.getId()))
                                                .withSelfRel(),
                                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("allProductos"),
                                linkTo(methodOn(ProductoController.class).actualizarProducto(savedProducto.getId(),
                                                savedProducto))
                                                .withRel("update"),
                                linkTo(methodOn(ProductoController.class).eliminarProducto(savedProducto.getId()))
                                                .withRel("delete"));
                return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar producto", description = "Recibe un objeto de tipo Producto y lo actualiza en la base de datos.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Producto actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
        public ResponseEntity<EntityModel<ProductoResponseDto>> actualizarProducto(
                        @Parameter(description = "Introducir ID del producto que se desea actualizar", required = true) @PathVariable Long id,
                        @Parameter(description = "Introducir objeto de tipo Producto que va a reemplazar al actual", required = true) @Valid @RequestBody Producto producto) {
                Producto productoExistente = productoService.findById(id);
                if (productoExistente != null) {
                        producto.setId(id);
                        Producto updatedProducto = productoService.save(producto);
                        EntityModel<ProductoResponseDto> entityModel = EntityModel.of(
                                        productoMapper.toResponse(updatedProducto),
                                        linkTo(methodOn(ProductoController.class)
                                                        .obtenerProductoPorId(updatedProducto.getId()))
                                                        .withSelfRel(),
                                        linkTo(methodOn(ProductoController.class).listarProductos())
                                                        .withRel("allProductos"),
                                        linkTo(methodOn(ProductoController.class).actualizarProducto(
                                                        updatedProducto.getId(),
                                                        updatedProducto)).withRel("update"),
                                        linkTo(methodOn(ProductoController.class)
                                                        .eliminarProducto(updatedProducto.getId()))
                                                        .withRel("delete"));
                        return ResponseEntity.ok(entityModel);
                }
                return ResponseEntity.notFound().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Producto eliminado", content = @Content(mediaType = "application/json", schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "404", description = "No se encontro el producto", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
        public ResponseEntity<EntityModel<Map<String, String>>> eliminarProducto(
                        @Parameter(description = "Introducir ID del producto que se desea eliminar", required = true) @PathVariable Long id) {
                Producto producto = productoService.findById(id);
                if (producto != null) {
                        productoService.deleteById(id);
                        EntityModel<Map<String, String>> entityModel = EntityModel.of(
                                        Map.of("message", "Producto eliminado correctamente"),
                                        linkTo(methodOn(ProductoController.class).listarProductos())
                                                        .withRel("allProductos"),
                                        linkTo(methodOn(ProductoController.class).guardarProducto(new Producto()))
                                                        .withRel("addProducto"));
                        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
                } else {
                        return ResponseEntity.notFound().build();
                }
        }
}