package com.minimarket.mapper;

import com.minimarket.dto.usuario.UsuarioResponseDto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Rol;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public UsuarioResponseDto toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        UsuarioResponseDto response = new UsuarioResponseDto();
        response.setId(usuario.getId());
        response.setUsername(usuario.getUsername());
        if (usuario.getRoles() != null) {
            response.setRoles(usuario.getRoles().stream()
                    .map(Rol::getNombre)
                    .collect(Collectors.toSet()));
        }
        return response;
    }
}
