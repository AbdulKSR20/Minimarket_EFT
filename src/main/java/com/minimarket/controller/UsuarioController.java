package com.minimarket.controller;

import jakarta.validation.Valid;

import com.minimarket.entity.Usuario;
import com.minimarket.exception.ErrorResponse;
import com.minimarket.dto.usuario.UsuarioResponseDto;
import com.minimarket.mapper.UsuarioMapper;
import com.minimarket.service.UsuarioService;

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
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API de gestion de usuarios, incluye CRUD de usuarios.")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

        @Autowired
        private UsuarioService usuarioService;

        @Autowired
        private UsuarioMapper usuarioMapper;

        @GetMapping
        @Operation(summary = "Listar usuarios", description = "Retorna una lista de todos los usuarios")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Se encontraron usuarios", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto[].class))),
                        @ApiResponse(responseCode = "404", description = "No se encontraron usuarios", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<CollectionModel<EntityModel<UsuarioResponseDto>>> listarUsuarios() {
                List<EntityModel<UsuarioResponseDto>> usuarios = usuarioService.findAll().stream()
                                .map(usuario -> EntityModel.of(usuarioMapper.toResponse(usuario),
                                                linkTo(methodOn(UsuarioController.class)
                                                                .obtenerUsuarioPorId(usuario.getId())).withSelfRel(),
                                                linkTo(methodOn(UsuarioController.class)
                                                                .actualizarUsuario(usuario.getId(), usuario))
                                                                .withRel("update"),
                                                linkTo(methodOn(UsuarioController.class)
                                                                .eliminarUsuario(usuario.getId()))
                                                                .withRel("delete")))
                                .toList();
                return ResponseEntity.ok(CollectionModel.of(usuarios));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener usuario por id", description = "Retorna un usuario por id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Se encontro el usuario", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class)), links = {
                                        @Link(name = "self", description = "Enlace al recurso del usuario obtenido", operationId = "obtenerUsuarioPorId"),
                                        @Link(name = "allUsuarios", description = "Enlace a la lista de todos los usuarios", operationId = "listarUsuarios"),
                                        @Link(name = "update", description = "Enlace para actualizar el usuario", operationId = "actualizarUsuario")
                        }),
                        @ApiResponse(responseCode = "404", description = "No se encontro el usuario", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE_SUCURSAL')")
        public ResponseEntity<EntityModel<UsuarioResponseDto>> obtenerUsuarioPorId(
                        @Parameter(description = "Id del usuario", required = true) @PathVariable Long id) {
                Optional<Usuario> optionalUsuario = usuarioService.findById(id);
                if (optionalUsuario.isPresent()) {
                        Usuario usuario = optionalUsuario.get();
                        EntityModel<UsuarioResponseDto> entityModel = EntityModel.of(usuarioMapper.toResponse(usuario),
                                        linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId()))
                                                        .withSelfRel(),
                                        linkTo(methodOn(UsuarioController.class).listarUsuarios())
                                                        .withRel("allUsuarios"),
                                        linkTo(methodOn(UsuarioController.class).actualizarUsuario(usuario.getId(),
                                                        usuario))
                                                        .withRel("update"),
                                        linkTo(methodOn(UsuarioController.class).eliminarUsuario(usuario.getId()))
                                                        .withRel("delete"));
                        return ResponseEntity.ok(entityModel);
                }
                return ResponseEntity.notFound().build();
        }

        @PostMapping
        @Operation(summary = "Guardar usuario", description = "Registra un nuevo usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuario registrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<EntityModel<UsuarioResponseDto>> guardarUsuario(
                        @Parameter(description = "Introducir objeto de tipo Usuario a guardar", required = true) @Valid @RequestBody Usuario usuario) {
                Usuario savedUsuario = usuarioService.save(usuario);
                EntityModel<UsuarioResponseDto> entityModel = EntityModel.of(usuarioMapper.toResponse(savedUsuario),
                                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(savedUsuario.getId()))
                                                .withSelfRel(),
                                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("allUsuarios"),
                                linkTo(methodOn(UsuarioController.class).actualizarUsuario(savedUsuario.getId(),
                                                savedUsuario))
                                                .withRel("update"),
                                linkTo(methodOn(UsuarioController.class).eliminarUsuario(savedUsuario.getId()))
                                                .withRel("delete"));
                return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "No se encontro el usuario", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<EntityModel<UsuarioResponseDto>> actualizarUsuario(
                        @Parameter(description = "Introducir ID del usuario que se desea actualizar", required = true) @PathVariable Long id,
                        @Parameter(description = "Introducir objeto de tipo Usuario que va a reemplazar al actual", required = true) @Valid @RequestBody Usuario usuario) {
                Optional<Usuario> usuarioExistente = usuarioService.findById(id);
                if (usuarioExistente.isPresent()) {
                        usuario.setId(id);
                        Usuario updatedUsuario = usuarioService.save(usuario);
                        EntityModel<UsuarioResponseDto> entityModel = EntityModel.of(
                                        usuarioMapper.toResponse(updatedUsuario),
                                        linkTo(methodOn(UsuarioController.class)
                                                        .obtenerUsuarioPorId(updatedUsuario.getId())).withSelfRel(),
                                        linkTo(methodOn(UsuarioController.class).listarUsuarios())
                                                        .withRel("allUsuarios"),
                                        linkTo(methodOn(UsuarioController.class)
                                                        .actualizarUsuario(updatedUsuario.getId(), updatedUsuario))
                                                        .withRel("update"),
                                        linkTo(methodOn(UsuarioController.class)
                                                        .eliminarUsuario(updatedUsuario.getId()))
                                                        .withRel("delete"));
                        return ResponseEntity.ok(entityModel);
                }
                return ResponseEntity.notFound().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar usuario", description = "Elimina un usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario eliminado", content = @Content(mediaType = "application/json", schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "404", description = "No se encontro el usuario", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<EntityModel<Map<String, String>>> eliminarUsuario(
                        @Parameter(description = "Introducir ID del usuario que se desea eliminar", required = true) @PathVariable Long id) {
                Optional<Usuario> usuario = usuarioService.findById(id);
                if (usuario.isPresent()) {
                        usuarioService.deleteById(id);
                        EntityModel<Map<String, String>> entityModel = EntityModel.of(
                                        Map.of("message", "Usuario eliminado correctamente"),
                                        linkTo(methodOn(UsuarioController.class).listarUsuarios())
                                                        .withRel("allUsuarios"),
                                        linkTo(methodOn(UsuarioController.class).guardarUsuario(new Usuario()))
                                                        .withRel("addUsuario"));
                        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
                }
                return ResponseEntity.notFound().build();
        }
}