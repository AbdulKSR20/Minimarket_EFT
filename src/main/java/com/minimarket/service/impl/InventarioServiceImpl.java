package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.StockSucursal;
import com.minimarket.entity.Sucursal;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.StockSucursalRepository;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.service.InventarioService;
import com.minimarket.service.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;
    
    @Autowired
    private StockSucursalRepository stockSucursalRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private SucursalRepository sucursalRepository;
    
    @Autowired
    private OrdenCompraService ordenCompraService;

    @Override
    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Inventario findById(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con el id: " + id));
    }

    @Override
    public Inventario save(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    @Override
    public void deleteById(Long id) {
        inventarioRepository.deleteById(id);
    }

    @Override
    public List<Inventario> findByProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    @Override
    public Inventario registrarMovimiento(Long productoId, Long sucursalId, Integer cantidad, String tipoMovimiento) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + productoId));
                
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con el id: " + sucursalId));

        StockSucursal stock = stockSucursalRepository.findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseGet(() -> {
                    StockSucursal nuevoStock = new StockSucursal();
                    nuevoStock.setProducto(producto);
                    nuevoStock.setSucursal(sucursal);
                    nuevoStock.setCantidad(0);
                    nuevoStock.setStockMinimo(10); // Valor por defecto
                    return nuevoStock;
                });

        if ("ENTRADA".equalsIgnoreCase(tipoMovimiento)) {
            stock.setCantidad(stock.getCantidad() + cantidad);
        } else if ("SALIDA".equalsIgnoreCase(tipoMovimiento)) {
            if (stock.getCantidad() < cantidad) {
                throw new RuntimeException("Stock insuficiente en la sucursal");
            }
            stock.setCantidad(stock.getCantidad() - cantidad);
        } else {
            throw new IllegalArgumentException("Tipo de movimiento no válido. Use ENTRADA o SALIDA.");
        }

        stockSucursalRepository.save(stock);

        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setSucursal(sucursal);
        movimiento.setCantidad(cantidad);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setFechaMovimiento(new Date());
        
        Inventario guardado = inventarioRepository.save(movimiento);

        // Disparar orden de compra si el stock cae por debajo del mínimo
        if (stock.getCantidad() <= stock.getStockMinimo()) {
            Integer cantidadSugerida = stock.getStockMinimo() * 2; // Ejemplo de regla de negocio
            try {
                ordenCompraService.generarOrdenAutomatica(producto, cantidadSugerida);
            } catch (Exception e) {
                // Loggear el error de generación de orden, pero no fallar el movimiento
                System.err.println("Error generando orden de compra automática: " + e.getMessage());
            }
        }

        return guardado;
    }
}
