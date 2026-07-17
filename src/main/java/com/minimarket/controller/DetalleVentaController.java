package com.minimarket.controller;

import jakarta.validation.Valid;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.minimarket.dto.detalleventa.DetalleVentaResponseDto;
import com.minimarket.mapper.DetalleVentaMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.links.Link;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.minimarket.exception.ErrorResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detalle-ventas")
@Tag(name = "Detalle de Ventas", description = "API de gestion de detalles de venta.")
@SecurityRequirement(name = "bearerAuth")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private DetalleVentaMapper detalleVentaMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL', 'CAJERO')")
    @Operation(summary = "Listar detalles de ventas", description = "Retorna una lista de todos los detalles de ventas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontraron detalles", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<CollectionModel<EntityModel<DetalleVentaResponseDto>>> listarDetalleVentas() {
        List<EntityModel<DetalleVentaResponseDto>> detalles = detalleVentaService.findAll().stream()
                .map(detalle -> EntityModel.of(detalleVentaMapper.toResponse(detalle),
                        linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(detalle.getId())).withSelfRel(),
                        linkTo(methodOn(DetalleVentaController.class).actualizarDetalleVenta(detalle.getId(), detalle)).withRel("update"),
                        linkTo(methodOn(DetalleVentaController.class).eliminarDetalleVenta(detalle.getId())).withRel("delete")))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(detalles, linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL', 'CAJERO', 'CLIENTE')")
    @Operation(summary = "Obtener detalle por ID", description = "Retorna un detalle de venta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontro el detalle", content = @Content(mediaType = "application/json"), links = {
                    @Link(name = "self", description = "Enlace al detalle", operationId = "obtenerDetalleVentaPorId"),
                    @Link(name = "allDetalles", description = "Enlace a la lista de detalles", operationId = "listarDetalleVentas"),
                    @Link(name = "update", description = "Enlace para actualizar el detalle", operationId = "actualizarDetalleVenta")
            }),
            @ApiResponse(responseCode = "404", description = "No se encontro el detalle", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<EntityModel<DetalleVentaResponseDto>> obtenerDetalleVentaPorId(
            @Parameter(description = "Id del detalle", required = true) @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            EntityModel<DetalleVentaResponseDto> entityModel = EntityModel.of(detalleVentaMapper.toResponse(detalleVenta),
                    linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(id)).withSelfRel(),
                    linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withRel("allDetalles"),
                    linkTo(methodOn(DetalleVentaController.class).actualizarDetalleVenta(id, detalleVenta)).withRel("update"),
                    linkTo(methodOn(DetalleVentaController.class).eliminarDetalleVenta(id)).withRel("delete"));
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Guardar detalle de venta", description = "Crea un nuevo detalle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle creado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<EntityModel<DetalleVentaResponseDto>> guardarDetalleVenta(
            @Parameter(description = "Detalle a guardar", required = true) @Valid @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta saved = detalleVentaService.save(detalleVenta);
        EntityModel<DetalleVentaResponseDto> entityModel = EntityModel.of(detalleVentaMapper.toResponse(saved),
                linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(saved.getId())).withSelfRel(),
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withRel("allDetalles"),
                linkTo(methodOn(DetalleVentaController.class).actualizarDetalleVenta(saved.getId(), saved)).withRel("update"),
                linkTo(methodOn(DetalleVentaController.class).eliminarDetalleVenta(saved.getId())).withRel("delete"));
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(entityModel);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar detalle", description = "Actualiza un detalle de venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No se encontro el detalle", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<EntityModel<DetalleVentaResponseDto>> actualizarDetalleVenta(
            @Parameter(description = "Id", required = true) @PathVariable Long id, 
            @Parameter(description = "Datos actualizados", required = true) @Valid @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            detalleVenta.setId(id);
            DetalleVenta updated = detalleVentaService.save(detalleVenta);
            EntityModel<DetalleVentaResponseDto> entityModel = EntityModel.of(detalleVentaMapper.toResponse(updated),
                    linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(updated.getId())).withSelfRel(),
                    linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withRel("allDetalles"),
                    linkTo(methodOn(DetalleVentaController.class).actualizarDetalleVenta(updated.getId(), updated)).withRel("update"),
                    linkTo(methodOn(DetalleVentaController.class).eliminarDetalleVenta(updated.getId())).withRel("delete"));
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar detalle", description = "Elimina un detalle de venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle eliminado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No se encontro el detalle", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<EntityModel<Map<String, String>>> eliminarDetalleVenta(
            @Parameter(description = "Id", required = true) @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            detalleVentaService.deleteById(id);
            EntityModel<Map<String, String>> entityModel = EntityModel.of(
                    Map.of("message", "Detalle de venta eliminado exitosamente"),
                    linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withRel("allDetalles"));
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }
}
