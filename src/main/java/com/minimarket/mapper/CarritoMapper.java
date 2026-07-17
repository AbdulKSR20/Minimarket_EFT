package com.minimarket.mapper;

import com.minimarket.dto.carrito.CarritoResponseDto;
import com.minimarket.entity.Carrito;
import org.springframework.stereotype.Component;

@Component
public class CarritoMapper {

    public CarritoResponseDto toResponse(Carrito carrito) {
        if (carrito == null) {
            return null;
        }

        CarritoResponseDto response = new CarritoResponseDto();
        response.setId(carrito.getId());
        response.setCantidad(carrito.getCantidad());
        if (carrito.getUsuario() != null) {
            response.setUsuarioId(carrito.getUsuario().getId());
        }
        if (carrito.getProducto() != null) {
            response.setProductoId(carrito.getProducto().getId());
        }
        return response;
    }
}
