package com.minimarket.mapper;

import com.minimarket.dto.detalleventa.DetalleVentaResponseDto;
import com.minimarket.entity.DetalleVenta;
import org.springframework.stereotype.Component;

@Component
public class DetalleVentaMapper {

    public DetalleVentaResponseDto toResponse(DetalleVenta detalle) {
        if (detalle == null) return null;
        DetalleVentaResponseDto dto = new DetalleVentaResponseDto();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecio(detalle.getPrecio());
        if (detalle.getProducto() != null) {
            dto.setProductoNombre(detalle.getProducto().getNombre());
        }
        return dto;
    }
}
