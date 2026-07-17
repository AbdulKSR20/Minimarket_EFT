package com.minimarket.mapper;

import com.minimarket.dto.detalleventa.DetalleVentaResponseDto;
import com.minimarket.dto.venta.VentaResponseDto;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Venta;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VentaMapper {

    public VentaResponseDto toResponse(Venta venta) {
        if (venta == null)
            return null;

        VentaResponseDto dto = new VentaResponseDto();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTipoEntrega(venta.getTipoEntrega());
        dto.setEstadoPedido(venta.getEstadoPedido());
        dto.setDireccionDespacho(venta.getDireccionDespacho());

        if (venta.getUsuario() != null) {
            dto.setUsuarioUsername(venta.getUsuario().getUsername());
        }

        if (venta.getSucursal() != null) {
            dto.setSucursalNombre(venta.getSucursal().getNombre());
        }

        List<DetalleVentaResponseDto> detallesDto = new ArrayList<>();
        double total = 0.0;

        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                DetalleVentaResponseDto detDto = new DetalleVentaResponseDto();
                detDto.setId(detalle.getId());
                detDto.setCantidad(detalle.getCantidad());
                detDto.setPrecio(detalle.getPrecio());
                if (detalle.getProducto() != null) {
                    detDto.setProductoNombre(detalle.getProducto().getNombre());
                }
                detallesDto.add(detDto);
                total += (detalle.getCantidad() * detalle.getPrecio());
            }
        }

        dto.setDetalles(detallesDto);
        dto.setTotalVenta(total);

        return dto;
    }
}
