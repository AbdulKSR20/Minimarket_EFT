package com.minimarket.service.impl;

import com.minimarket.entity.*;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.InventarioService;
import com.minimarket.service.PromocionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;
    
    @Mock
    private CarritoRepository carritoRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private SucursalRepository sucursalRepository;
    
    @Mock
    private PromocionService promocionService;
    
    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Usuario usuario;
    private Sucursal sucursal;
    private Producto producto;
    private Carrito carrito;
    private Venta venta;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        sucursal = new Sucursal();
        sucursal.setId(1L);

        producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(100.0);

        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(2);

        venta = new Venta();
        venta.setId(1L);
    }

    @Test
    void procesarCheckout_ShouldReturnVenta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(carrito));
        
        when(inventarioService.registrarMovimiento(eq(1L), eq(1L), eq(2), eq("SALIDA"))).thenReturn(null);
        when(promocionService.calcularPrecioFinal(producto)).thenReturn(90.0);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArguments()[0]);

        Venta result = ventaService.procesarCheckout(1L, 1L, "DESPACHO_DOMICILIO", "Calle Falsa 123");

        assertNotNull(result);
        assertEquals("PENDIENTE", result.getEstadoPedido());
        assertEquals("DESPACHO_DOMICILIO", result.getTipoEntrega());
        assertEquals("Calle Falsa 123", result.getDireccionDespacho());
        assertEquals(1, result.getDetalles().size());
        assertEquals(90.0, result.getDetalles().get(0).getPrecio());
        
        verify(carritoRepository, times(1)).deleteById(1L);
    }

    @Test
    void procesarCheckout_CarritoVacio_ShouldThrowException_EdgeCase() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> ventaService.procesarCheckout(1L, 1L, "RETIRO", null));
    }

    @Test
    void findAll_ShouldReturnList() {
        when(ventaRepository.findAll()).thenReturn(Arrays.asList(venta));
        List<Venta> result = ventaService.findAll();
        assertFalse(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnVenta() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        Venta result = ventaService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void findById_NotFound_ShouldThrowException_EdgeCase() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ventaService.findById(99L));
    }

    @Test
    void findByUsuarioId_ShouldReturnList() {
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(venta));
        List<Venta> result = ventaService.findByUsuarioId(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void save_ShouldReturnSavedVenta() {
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        Venta result = ventaService.save(new Venta());
        assertNotNull(result);
    }
}
