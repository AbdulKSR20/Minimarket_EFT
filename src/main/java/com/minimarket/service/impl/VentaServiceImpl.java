package com.minimarket.service.impl;

import com.minimarket.entity.*;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.InventarioService;
import com.minimarket.service.PromocionService;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private InventarioService inventarioService;

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));
    }

    @Override
    public Venta save(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public List<Venta> findByUsuarioId(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public Venta procesarCheckout(Long usuarioId, Long sucursalId, String tipoEntrega, String direccionDespacho) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + sucursalId));

        List<Carrito> itemsCarrito = carritoRepository.findByUsuarioId(usuarioId);
        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setSucursal(sucursal);
        venta.setFecha(new Date());
        venta.setTipoEntrega(tipoEntrega);
        venta.setEstadoPedido("PENDIENTE");
        if ("DESPACHO_DOMICILIO".equalsIgnoreCase(tipoEntrega)) {
            venta.setDireccionDespacho(direccionDespacho);
        }

        List<DetalleVenta> detalles = new ArrayList<>();

        for (Carrito item : itemsCarrito) {
            Producto producto = item.getProducto();
            Integer cantidad = item.getCantidad();

            // 1. Descontar stock (esto lanzará excepción si no hay stock)
            inventarioService.registrarMovimiento(producto.getId(), sucursal.getId(), cantidad, "SALIDA");

            // 2. Calcular precio con promoción
            Double precioFinal = promocionService.calcularPrecioFinal(producto);

            // 3. Crear detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecio(precioFinal);
            
            detalles.add(detalle);
        }

        venta.setDetalles(detalles);
        Venta ventaGuardada = ventaRepository.save(venta);

        // 4. Vaciar carrito
        for (Carrito item : itemsCarrito) {
            carritoRepository.deleteById(item.getId());
        }

        return ventaGuardada;
    }
}
