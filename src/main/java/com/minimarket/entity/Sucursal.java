package com.minimarket.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    private String telefono;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<StockSucursal> inventarios;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<StockSucursal> getInventarios() {
        return inventarios;
    }

    public void setInventarios(List<StockSucursal> inventarios) {
        this.inventarios = inventarios;
    }
}
