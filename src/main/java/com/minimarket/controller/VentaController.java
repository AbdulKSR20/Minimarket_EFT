package com.minimarket.controller;

import com.minimarket.dto.venta.VentaResponseDto;
import com.minimarket.entity.Venta;
import com.minimarket.mapper.VentaMapper;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.links.Link;

import com.minimarket.exception.ErrorResponse;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "API de gestion de ventas, incluye creacion y lectura de ventas.")
@SecurityRequirement(name = "bearerAuth")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaMapper ventaMapper;

    @GetMapping
    @Operation(summary = "Listar ventas", description = "Retorna una lista de todas las ventas registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontraron ventas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VentaResponseDto[].class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL', 'CAJERO')")
    public ResponseEntity<CollectionModel<EntityModel<VentaResponseDto>>> listarVentas() {
        List<EntityModel<VentaResponseDto>> ventas = ventaService.findAll().stream()
                .map(venta -> EntityModel.of(ventaMapper.toResponse(venta),
                        linkTo(methodOn(VentaController.class).obtenerVentaPorId(venta.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(ventas,
                linkTo(methodOn(VentaController.class).listarVentas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por id", description = "Retorna una venta específica por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontro la venta", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VentaResponseDto.class)), links = {
                    @Link(name = "self", description = "Enlace a la venta actual", operationId = "obtenerVentaPorId"),
                    @Link(name = "allVentas", description = "Enlace a la lista de ventas", operationId = "listarVentas")
            }),
            @ApiResponse(responseCode = "404", description = "No se encontro la venta", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL', 'CAJERO', 'CLIENTE')")
    public ResponseEntity<EntityModel<VentaResponseDto>> obtenerVentaPorId(
            @Parameter(description = "Id de la venta", required = true) @PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        if (venta != null) {
            EntityModel<VentaResponseDto> entityModel = EntityModel.of(ventaMapper.toResponse(venta),
                    linkTo(methodOn(VentaController.class).obtenerVentaPorId(id)).withSelfRel(),
                    linkTo(methodOn(VentaController.class).listarVentas()).withRel("allVentas"));
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/checkout")
    @Operation(summary = "Procesar checkout de carrito", description = "Crea una venta a partir del carrito de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VentaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación o stock insuficiente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'CLIENTE')")
    public ResponseEntity<EntityModel<VentaResponseDto>> procesarCheckout(
            @Parameter(description = "ID del usuario", required = true) @RequestParam Long usuarioId,
            @Parameter(description = "ID de la sucursal", required = true) @RequestParam Long sucursalId,
            @Parameter(description = "Tipo de entrega (Ej: DESPACHO, RETIRO)", required = true) @RequestParam String tipoEntrega,
            @Parameter(description = "Dirección de despacho (opcional)") @RequestParam(required = false) String direccionDespacho) {
        
        Venta venta = ventaService.procesarCheckout(usuarioId, sucursalId, tipoEntrega, direccionDespacho);
        EntityModel<VentaResponseDto> entityModel = EntityModel.of(ventaMapper.toResponse(venta),
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(venta.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas()).withRel("allVentas"));
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }
}
