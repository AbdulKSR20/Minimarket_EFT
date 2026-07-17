package com.minimarket.controller;

import jakarta.validation.Valid;

import com.minimarket.entity.Carrito;
import com.minimarket.exception.ErrorResponse;
import com.minimarket.dto.carrito.CarritoResponseDto;
import com.minimarket.mapper.CarritoMapper;
import com.minimarket.service.CarritoService;

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
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "API de gestion de carritos, incluye CRUD de carritos.")
@SecurityRequirement(name = "bearerAuth")
public class CarritoController {

        @Autowired
        private CarritoService carritoService;

        @Autowired
        private CarritoMapper carritoMapper;

        @GetMapping
        @Operation(summary = "Listar carritos", description = "Retorna una lista de todos los carritos")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Se encontraron carritos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoResponseDto[].class))),
                        @ApiResponse(responseCode = "404", description = "No se encontraron carritos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'CLIENTE')")
        public ResponseEntity<CollectionModel<EntityModel<CarritoResponseDto>>> listarCarrito() {
                List<EntityModel<CarritoResponseDto>> carritos = carritoService.findAll().stream()
                                .map(carrito -> EntityModel.of(carritoMapper.toResponse(carrito),
                                                linkTo(methodOn(CarritoController.class)
                                                                .obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                                                linkTo(methodOn(CarritoController.class)
                                                                .actualizarCarrito(carrito.getId(), carrito))
                                                                .withRel("update"),
                                                linkTo(methodOn(CarritoController.class)
                                                                .eliminarProductoDelCarrito(carrito.getId()))
                                                                .withRel("delete")))
                                .toList();
                return ResponseEntity.ok(CollectionModel.of(carritos));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener carrito por id", description = "Retorna un carrito por id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Se encontro el carrito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoResponseDto.class)), links = {
                                        @Link(name = "self", description = "Enlace al recurso del carrito obtenido", operationId = "obtenerCarritoPorId"),
                                        @Link(name = "allCarritos", description = "Enlace a la lista de todos los carritos", operationId = "listarCarrito"),
                                        @Link(name = "update", description = "Enlace para actualizar el carrito", operationId = "actualizarCarrito")
                        }),
                        @ApiResponse(responseCode = "404", description = "No se encontro el carrito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'CLIENTE')")
        public ResponseEntity<EntityModel<CarritoResponseDto>> obtenerCarritoPorId(
                        @Parameter(description = "Id del carrito", required = true) @PathVariable Long id) {
                Carrito carrito = carritoService.findById(id);
                if (carrito != null) {
                        EntityModel<CarritoResponseDto> entityModel = EntityModel.of(carritoMapper.toResponse(carrito),
                                        linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId()))
                                                        .withSelfRel(),
                                        linkTo(methodOn(CarritoController.class).listarCarrito())
                                                        .withRel("allCarritos"),
                                        linkTo(methodOn(CarritoController.class).actualizarCarrito(carrito.getId(),
                                                        carrito))
                                                        .withRel("update"),
                                        linkTo(methodOn(CarritoController.class)
                                                        .eliminarProductoDelCarrito(carrito.getId()))
                                                        .withRel("delete"));
                        return ResponseEntity.ok(entityModel);
                }
                return ResponseEntity.notFound().build();
        }

        @PostMapping
        @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Producto agregado al carrito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        public ResponseEntity<EntityModel<CarritoResponseDto>> agregarProductoAlCarrito(
                        @Parameter(description = "Introducir objeto de tipo Carrito a guardar", required = true) @Valid @RequestBody Carrito carrito) {
                Carrito savedCarrito = carritoService.save(carrito);
                EntityModel<CarritoResponseDto> entityModel = EntityModel.of(carritoMapper.toResponse(savedCarrito),
                                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(savedCarrito.getId()))
                                                .withSelfRel(),
                                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("allCarritos"),
                                linkTo(methodOn(CarritoController.class).actualizarCarrito(savedCarrito.getId(),
                                                savedCarrito))
                                                .withRel("update"),
                                linkTo(methodOn(CarritoController.class)
                                                .eliminarProductoDelCarrito(savedCarrito.getId()))
                                                .withRel("delete"));
                return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar carrito", description = "Actualiza un carrito")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carrito actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "No se encontro el carrito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        public ResponseEntity<EntityModel<CarritoResponseDto>> actualizarCarrito(
                        @Parameter(description = "Introducir ID del carrito que se desea actualizar", required = true) @PathVariable Long id,
                        @Parameter(description = "Introducir objeto de tipo Carrito que va a reemplazar al actual", required = true) @Valid @RequestBody Carrito carrito) {
                Carrito existente = carritoService.findById(id);
                if (existente != null) {
                        carrito.setId(id);
                        Carrito updatedCarrito = carritoService.save(carrito);
                        EntityModel<CarritoResponseDto> entityModel = EntityModel.of(
                                        carritoMapper.toResponse(updatedCarrito),
                                        linkTo(methodOn(CarritoController.class)
                                                        .obtenerCarritoPorId(updatedCarrito.getId())).withSelfRel(),
                                        linkTo(methodOn(CarritoController.class).listarCarrito())
                                                        .withRel("allCarritos"),
                                        linkTo(methodOn(CarritoController.class)
                                                        .actualizarCarrito(updatedCarrito.getId(), updatedCarrito))
                                                        .withRel("update"),
                                        linkTo(methodOn(CarritoController.class)
                                                        .eliminarProductoDelCarrito(updatedCarrito.getId()))
                                                        .withRel("delete"));
                        return ResponseEntity.ok(entityModel);
                }
                return ResponseEntity.notFound().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar carrito", description = "Elimina un carrito")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carrito eliminado", content = @Content(mediaType = "application/json", schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "404", description = "No se encontro el carrito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        public ResponseEntity<EntityModel<Map<String, String>>> eliminarProductoDelCarrito(
                        @Parameter(description = "Introducir ID del carrito que se desea eliminar", required = true) @PathVariable Long id) {
                Carrito carrito = carritoService.findById(id);
                if (carrito != null) {
                        carritoService.deleteById(id);
                        EntityModel<Map<String, String>> entityModel = EntityModel.of(
                                        Map.of("message", "Carrito eliminado correctamente"),
                                        linkTo(methodOn(CarritoController.class).listarCarrito())
                                                        .withRel("allCarritos"),
                                        linkTo(methodOn(CarritoController.class)
                                                        .agregarProductoAlCarrito(new Carrito()))
                                                        .withRel("addCarrito"));
                        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
                }
                return ResponseEntity.notFound().build();
        }
}