package com.minimarket.dto.inventario;

import java.util.Date;

public class InventarioResponseDto {
    private Long productoId;
    private Integer cantidad;
    private String tipoMovimiento;
    private Date fechaMovimiento;

    public InventarioResponseDto() {}

    public InventarioResponseDto(Long productoId, Integer cantidad, String tipoMovimiento, Date fechaMovimiento) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.tipoMovimiento = tipoMovimiento;
        this.fechaMovimiento = fechaMovimiento;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
}
