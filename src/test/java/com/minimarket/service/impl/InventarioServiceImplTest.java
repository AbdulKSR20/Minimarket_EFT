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
import com.minimarket.service.OrdenCompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private StockSucursalRepository stockSucursalRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private OrdenCompraService ordenCompraService;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto producto;
    private Sucursal sucursal;
    private StockSucursal stock;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);

        sucursal = new Sucursal();
        sucursal.setId(1L);

        stock = new StockSucursal();
        stock.setId(1L);
        stock.setProducto(producto);
        stock.setSucursal(sucursal);
        stock.setCantidad(20);
        stock.setStockMinimo(10);

        inventario = new Inventario();
        inventario.setId(1L);
    }

    @Test
    void findAll_ShouldReturnList() {
        when(inventarioRepository.findAll()).thenReturn(Arrays.asList(inventario));
        List<Inventario> result = inventarioService.findAll();
        assertFalse(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnInventario() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        Inventario result = inventarioService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void findById_NotFound_ShouldThrowException_EdgeCase() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> inventarioService.findById(99L));
    }

    @Test
    void save_ShouldReturnSavedInventario() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        Inventario result = inventarioService.save(new Inventario());
        assertNotNull(result);
    }

    @Test
    void findByProductoId_ShouldReturnList() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Arrays.asList(inventario));
        List<Inventario> result = inventarioService.findByProductoId(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void registrarMovimiento_Entrada_ShouldIncreaseStock() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(stockSucursalRepository.findByProductoIdAndSucursalId(1L, 1L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        inventarioService.registrarMovimiento(1L, 1L, 5, "ENTRADA");

        assertEquals(25, stock.getCantidad());
        verify(stockSucursalRepository, times(1)).save(stock);
        verify(ordenCompraService, never()).generarOrdenAutomatica(any(), any());
    }

    @Test
    void registrarMovimiento_Salida_ShouldDecreaseStock() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(stockSucursalRepository.findByProductoIdAndSucursalId(1L, 1L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        inventarioService.registrarMovimiento(1L, 1L, 5, "SALIDA");

        assertEquals(15, stock.getCantidad());
        verify(stockSucursalRepository, times(1)).save(stock);
    }

    @Test
    void registrarMovimiento_Salida_InsufficientStock_ShouldThrowException_EdgeCase() {
        stock.setCantidad(2);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(stockSucursalRepository.findByProductoIdAndSucursalId(1L, 1L)).thenReturn(Optional.of(stock));

        assertThrows(RuntimeException.class, () -> inventarioService.registrarMovimiento(1L, 1L, 5, "SALIDA"));
    }

    @Test
    void registrarMovimiento_Salida_ShouldTriggerOrdenCompra_EdgeCase() {
        stock.setCantidad(15);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(stockSucursalRepository.findByProductoIdAndSucursalId(1L, 1L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        inventarioService.registrarMovimiento(1L, 1L, 5, "SALIDA"); // 15 - 5 = 10 (<= stockMinimo 10)

        assertEquals(10, stock.getCantidad());
        verify(ordenCompraService, times(1)).generarOrdenAutomatica(producto, 20);
    }

    @Test
    void registrarMovimiento_TipoInvalido_ShouldThrowException_EdgeCase() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(stockSucursalRepository.findByProductoIdAndSucursalId(1L, 1L)).thenReturn(Optional.of(stock));

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.registrarMovimiento(1L, 1L, 5, "INVALIDO"));
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(inventarioRepository).deleteById(1L);
        inventarioService.deleteById(1L);
        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void registrarMovimiento_Salida_ErrorGenerandoOrdenCompra_EdgeCase() {
        stock.setCantidad(15);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(stockSucursalRepository.findByProductoIdAndSucursalId(1L, 1L)).thenReturn(Optional.of(stock));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // Simulamos un error al generar la orden
        doThrow(new RuntimeException("Error en BD")).when(ordenCompraService).generarOrdenAutomatica(any(), any());

        // La excepción debe ser atrapada e ignorada en la ejecución del movimiento
        Inventario result = inventarioService.registrarMovimiento(1L, 1L, 5, "SALIDA");

        assertNotNull(result);
        assertEquals(10, stock.getCantidad());
        verify(ordenCompraService, times(1)).generarOrdenAutomatica(producto, 20);
    }
}
