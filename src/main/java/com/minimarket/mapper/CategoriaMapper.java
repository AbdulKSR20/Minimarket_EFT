package com.minimarket.mapper;

import com.minimarket.dto.categoria.CategoriaResponseDto;
import com.minimarket.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    public CategoriaResponseDto toResponse(Categoria categoria) {
        if (categoria == null)
            return null;
        CategoriaResponseDto dto = new CategoriaResponseDto();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        return dto;
    }
}
