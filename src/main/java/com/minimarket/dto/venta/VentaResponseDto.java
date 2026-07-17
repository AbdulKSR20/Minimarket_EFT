package com.minimarket.dto.venta;

import java.util.Date;
import java.util.List;

import com.minimarket.dto.detalleventa.DetalleVentaResponseDto;

public class VentaResponseDto {
    private Long id;
    private String sucursalNombre;
    private String usuarioUsername;
    private Date fecha;
    private String tipoEntrega;
    private String estadoPedido;
    private String direccionDespacho;
    private List<DetalleVentaResponseDto> detalles;
    private Double totalVenta;

    public VentaResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSucursalNombre() {
        return sucursalNombre;
    }

    public void setSucursalNombre(String sucursalNombre) {
        this.sucursalNombre = sucursalNombre;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public String getDireccionDespacho() {
        return direccionDespacho;
    }

    public void setDireccionDespacho(String direccionDespacho) {
        this.direccionDespacho = direccionDespacho;
    }

    public List<DetalleVentaResponseDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaResponseDto> detalles) {
        this.detalles = detalles;
    }

    public Double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }
}
