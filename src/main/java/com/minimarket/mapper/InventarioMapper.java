package com.minimarket.mapper;

import com.minimarket.dto.inventario.InventarioResponseDto;
import com.minimarket.entity.Inventario;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {

    public InventarioResponseDto toResponse(Inventario inventario) {
        if (inventario == null) {
            return null;
        }

        InventarioResponseDto response = new InventarioResponseDto();
        response.setCantidad(inventario.getCantidad());
        response.setTipoMovimiento(inventario.getTipoMovimiento());
        response.setFechaMovimiento(inventario.getFechaMovimiento());
        if (inventario.getProducto() != null) {
            response.setProductoId(inventario.getProducto().getId());
        }
        return response;
    }
}
