package com.minimarket.service;

import com.minimarket.entity.OrdenCompra;
import com.minimarket.entity.Producto;
import java.util.List;

public interface OrdenCompraService {
    OrdenCompra generarOrdenAutomatica(Producto producto, Integer cantidadRequerida);
    List<OrdenCompra> findAll();
    OrdenCompra findById(Long id);
    OrdenCompra save(OrdenCompra ordenCompra);
}
