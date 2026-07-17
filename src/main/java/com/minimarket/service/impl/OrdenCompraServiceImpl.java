package com.minimarket.service.impl;

import com.minimarket.entity.DetalleOrdenCompra;
import com.minimarket.entity.OrdenCompra;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Proveedor;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.OrdenCompraRepository;
import com.minimarket.repository.ProveedorRepository;
import com.minimarket.service.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    public OrdenCompra generarOrdenAutomatica(Producto producto, Integer cantidadRequerida) {
        // Lógica simplificada: toma el primer proveedor disponible
        Proveedor proveedor = proveedorRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No hay proveedores registrados para generar la orden."));

        OrdenCompra orden = new OrdenCompra();
        orden.setProveedor(proveedor);
        orden.setFechaEmision(new Date());
        orden.setEstado("PENDIENTE");

        DetalleOrdenCompra detalle = new DetalleOrdenCompra();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidadRequerida);
        detalle.setPrecioUnitario(producto.getPrecio()); // O el precio de costo si existiera
        detalle.setOrdenCompra(orden);

        orden.setDetalles(Collections.singletonList(detalle));

        return ordenCompraRepository.save(orden);
    }

    @Override
    public List<OrdenCompra> findAll() {
        return ordenCompraRepository.findAll();
    }

    @Override
    public OrdenCompra findById(Long id) {
        return ordenCompraRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Orden de Compra no encontrada con id: " + id));
    }

    @Override
    public OrdenCompra save(OrdenCompra ordenCompra) {
        return ordenCompraRepository.save(ordenCompra);
    }
}
