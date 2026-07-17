package com.minimarket.dto.carrito;

public class CarritoResponseDto {
    private Long id;
    private Long usuarioId;
    private Long productoId;
    private Integer cantidad;

    public CarritoResponseDto() {}

    public CarritoResponseDto(Long id, Long usuarioId, Long productoId, Integer cantidad) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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
}
