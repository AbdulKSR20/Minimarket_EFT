package com.minimarket.mapper;

import com.minimarket.dto.producto.ProductoResponseDto;
import com.minimarket.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoResponseDto toResponse(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoResponseDto response = new ProductoResponseDto();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setPrecio(producto.getPrecio());
        return response;
    }
}
