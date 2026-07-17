package com.minimarket.controller;

import jakarta.validation.Valid;

import com.minimarket.entity.Inventario;
import com.minimarket.exception.ErrorResponse;
import com.minimarket.dto.inventario.InventarioResponseDto;
import com.minimarket.mapper.InventarioMapper;
import com.minimarket.service.InventarioService;

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
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "API de gestion de inventario, incluye CRUD de movimientos de inventario.")
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {

        @Autowired
        private InventarioService inventarioService;

        @Autowired
        private InventarioMapper inventarioMapper;

        @GetMapping
        @Operation(summary = "Listar movimientos de inventario", description = "Retorna una lista de todos los movimientos de inventario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Se encontraron movimientos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioResponseDto[].class))),
                        @ApiResponse(responseCode = "404", description = "No se encontraron movimientos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
        public ResponseEntity<CollectionModel<EntityModel<InventarioResponseDto>>> listarMovimientosDeInventario() {
                List<EntityModel<InventarioResponseDto>> inventarios = inventarioService.findAll().stream()
                                .map(inventario -> EntityModel.of(inventarioMapper.toResponse(inventario),
                                                linkTo(methodOn(InventarioController.class)
                                                                .obtenerMovimientoPorId(inventario.getId()))
                                                                .withSelfRel(),
                                                linkTo(methodOn(InventarioController.class).actualizarMovimiento(
                                                                inventario.getId(),
                                                                inventario))
                                                                .withRel("update"),
                                                linkTo(methodOn(InventarioController.class)
                                                                .eliminarMovimiento(inventario.getId()))
                                                                .withRel("delete")))
                                .toList();
                return ResponseEntity.ok(CollectionModel.of(inventarios));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener movimiento de inventario por id", description = "Retorna un movimiento de inventario por id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Se encontro el movimiento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioResponseDto.class)), links = {
                                        @Link(name = "self", description = "Enlace al recurso del movimiento obtenido", operationId = "obtenerMovimientoPorId"),
                                        @Link(name = "allMovimientos", description = "Enlace a la lista de todos los movimientos", operationId = "listarMovimientosDeInventario"),
                                        @Link(name = "update", description = "Enlace para actualizar el movimiento", operationId = "actualizarMovimiento")
                        }),
                        @ApiResponse(responseCode = "404", description = "No se encontro el movimiento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
        public ResponseEntity<EntityModel<InventarioResponseDto>> obtenerMovimientoPorId(
                        @Parameter(description = "Id del movimiento de inventario", required = true) @PathVariable Long id) {
                Inventario inventario = inventarioService.findById(id);
                if (inventario != null) {
                        EntityModel<InventarioResponseDto> entityModel = EntityModel.of(
                                        inventarioMapper.toResponse(inventario),
                                        linkTo(methodOn(InventarioController.class)
                                                        .obtenerMovimientoPorId(inventario.getId()))
                                                        .withSelfRel(),
                                        linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario())
                                                        .withRel("allMovimientos"),
                                        linkTo(methodOn(InventarioController.class)
                                                        .actualizarMovimiento(inventario.getId(), inventario))
                                                        .withRel("update"),
                                        linkTo(methodOn(InventarioController.class)
                                                        .eliminarMovimiento(inventario.getId()))
                                                        .withRel("delete"));
                        return ResponseEntity.ok(entityModel);
                }
                return ResponseEntity.notFound().build();
        }

        @PostMapping
        @Operation(summary = "Registrar movimiento de inventario", description = "Registra un nuevo movimiento de inventario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Movimiento registrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL', 'REPONEDOR')")
        public ResponseEntity<EntityModel<InventarioResponseDto>> registrarMovimiento(
                        @Parameter(description = "Introducir objeto de tipo Inventario a guardar", required = true) @Valid @RequestBody Inventario inventario) {
                Inventario savedInventario = inventarioService.save(inventario);
                EntityModel<InventarioResponseDto> entityModel = EntityModel.of(
                                inventarioMapper.toResponse(savedInventario),
                                linkTo(methodOn(InventarioController.class)
                                                .obtenerMovimientoPorId(savedInventario.getId()))
                                                .withSelfRel(),
                                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario())
                                                .withRel("allMovimientos"),
                                linkTo(methodOn(InventarioController.class).actualizarMovimiento(
                                                savedInventario.getId(),
                                                savedInventario)).withRel("update"),
                                linkTo(methodOn(InventarioController.class).eliminarMovimiento(savedInventario.getId()))
                                                .withRel("delete"));
                return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar movimiento de inventario", description = "Actualiza un movimiento de inventario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Movimiento actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "No se encontro el movimiento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
        public ResponseEntity<EntityModel<InventarioResponseDto>> actualizarMovimiento(
                        @Parameter(description = "Introducir ID del movimiento que se desea actualizar", required = true) @PathVariable Long id,
                        @Parameter(description = "Introducir objeto de tipo Inventario que va a reemplazar al actual", required = true) @Valid @RequestBody Inventario inventario) {
                Inventario existente = inventarioService.findById(id);
                if (existente != null) {
                        inventario.setId(id);
                        Inventario updatedInventario = inventarioService.save(inventario);
                        EntityModel<InventarioResponseDto> entityModel = EntityModel.of(
                                        inventarioMapper.toResponse(updatedInventario),
                                        linkTo(methodOn(InventarioController.class)
                                                        .obtenerMovimientoPorId(updatedInventario.getId()))
                                                        .withSelfRel(),
                                        linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario())
                                                        .withRel("allMovimientos"),
                                        linkTo(methodOn(InventarioController.class).actualizarMovimiento(
                                                        updatedInventario.getId(),
                                                        updatedInventario)).withRel("update"),
                                        linkTo(methodOn(InventarioController.class)
                                                        .eliminarMovimiento(updatedInventario.getId()))
                                                        .withRel("delete"));
                        return ResponseEntity.ok(entityModel);
                }
                return ResponseEntity.notFound().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar movimiento de inventario", description = "Elimina un movimiento de inventario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Movimiento eliminado", content = @Content(mediaType = "application/json", schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "404", description = "No se encontro el movimiento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<EntityModel<Map<String, String>>> eliminarMovimiento(
                        @Parameter(description = "Introducir ID del movimiento que se desea eliminar", required = true) @PathVariable Long id) {
                Inventario inventario = inventarioService.findById(id);
                if (inventario != null) {
                        inventarioService.deleteById(id);
                        EntityModel<Map<String, String>> entityModel = EntityModel.of(
                                        Map.of("message", "Movimiento de inventario eliminado correctamente"),
                                        linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario())
                                                        .withRel("allMovimientos"),
                                        linkTo(methodOn(InventarioController.class)
                                                        .registrarMovimiento(new Inventario()))
                                                        .withRel("addMovimiento"));
                        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
                }
                return ResponseEntity.notFound().build();
        }
}