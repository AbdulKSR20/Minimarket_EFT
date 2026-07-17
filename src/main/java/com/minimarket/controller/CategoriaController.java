package com.minimarket.controller;

import jakarta.validation.Valid;

import com.minimarket.dto.categoria.CategoriaResponseDto;
import com.minimarket.entity.Categoria;
import com.minimarket.mapper.CategoriaMapper;
import com.minimarket.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "API de gestion de categorias, incluye CRUD de categorias.")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaMapper categoriaMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todas las categorias", description = "Retorna una lista de todas las categorias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontraron categorias", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponseDto[].class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<CollectionModel<EntityModel<CategoriaResponseDto>>> listarCategorias() {
        List<EntityModel<CategoriaResponseDto>> list = categoriaService.findAll().stream()
                .map(categoria -> EntityModel.of(categoriaMapper.toResponse(categoria),
                        linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(categoria.getId())).withSelfRel(),
                        linkTo(methodOn(CategoriaController.class).actualizarCategoria(categoria.getId(), categoria)).withRel("update"),
                        linkTo(methodOn(CategoriaController.class).eliminarCategoria(categoria.getId())).withRel("delete")))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(list, linkTo(methodOn(CategoriaController.class).listarCategorias()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener categoria por ID", description = "Retorna una categoria por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontro la categoria", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponseDto.class)), links = {
                    @Link(name = "self", description = "Enlace a la categoria", operationId = "obtenerCategoriaPorId"),
                    @Link(name = "allCategorias", description = "Enlace a la lista de categorias", operationId = "listarCategorias"),
                    @Link(name = "update", description = "Enlace para actualizar la categoria", operationId = "actualizarCategoria")
            }),
            @ApiResponse(responseCode = "404", description = "No se encontro la categoria", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<EntityModel<CategoriaResponseDto>> obtenerCategoriaPorId(
            @Parameter(description = "Id de la categoria", required = true) @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            EntityModel<CategoriaResponseDto> entityModel = EntityModel.of(categoriaMapper.toResponse(categoria),
                    linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(id)).withSelfRel(),
                    linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("allCategorias"),
                    linkTo(methodOn(CategoriaController.class).actualizarCategoria(id, categoria)).withRel("update"),
                    linkTo(methodOn(CategoriaController.class).eliminarCategoria(id)).withRel("delete"));
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
    @Operation(summary = "Guardar categoria", description = "Guarda una nueva categoria en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria creada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<EntityModel<CategoriaResponseDto>> guardarCategoria(
            @Parameter(description = "Categoria a guardar", required = true) @Valid @RequestBody Categoria categoria) {
        Categoria saved = categoriaService.save(categoria);
        EntityModel<CategoriaResponseDto> entityModel = EntityModel.of(categoriaMapper.toResponse(saved),
                linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(saved.getId())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("allCategorias"),
                linkTo(methodOn(CategoriaController.class).actualizarCategoria(saved.getId(), saved)).withRel("update"),
                linkTo(methodOn(CategoriaController.class).eliminarCategoria(saved.getId())).withRel("delete"));
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(entityModel);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
    @Operation(summary = "Actualizar categoria", description = "Actualiza una categoria existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria actualizada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontro la categoria", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    public ResponseEntity<EntityModel<CategoriaResponseDto>> actualizarCategoria(
            @Parameter(description = "Id de la categoria", required = true) @PathVariable Long id, 
            @Parameter(description = "Nuevos datos de la categoria", required = true) @Valid @RequestBody Categoria categoria) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            Categoria updated = categoriaService.save(categoria);
            EntityModel<CategoriaResponseDto> entityModel = EntityModel.of(categoriaMapper.toResponse(updated),
                    linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(updated.getId())).withSelfRel(),
                    linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("allCategorias"),
                    linkTo(methodOn(CategoriaController.class).actualizarCategoria(updated.getId(), updated)).withRel("update"),
                    linkTo(methodOn(CategoriaController.class).eliminarCategoria(updated.getId())).withRel("delete"));
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
    @Operation(summary = "Eliminar categoria", description = "Elimina una categoria por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria eliminada", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No se encontro la categoria", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json")) })
    public ResponseEntity<EntityModel<Map<String, String>>> eliminarCategoria(
            @Parameter(description = "Id de la categoria", required = true) @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            EntityModel<Map<String, String>> entityModel = EntityModel.of(
                    Map.of("message", "Categoria eliminada exitosamente"),
                    linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("allCategorias"));
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }
}
