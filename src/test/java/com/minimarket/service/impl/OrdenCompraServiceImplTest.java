package com.minimarket.service.impl;

import com.minimarket.entity.OrdenCompra;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Proveedor;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.OrdenCompraRepository;
import com.minimarket.repository.ProveedorRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenCompraServiceImplTest {

    @Mock
    private OrdenCompraRepository ordenCompraRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private OrdenCompraServiceImpl ordenCompraService;

    private OrdenCompra ordenCompra;
    private Proveedor proveedor;
    private Producto producto;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor 1");

        producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(10.0);

        ordenCompra = new OrdenCompra();
        ordenCompra.setId(1L);
        ordenCompra.setEstado("PENDIENTE");
    }

    @Test
    void generarOrdenAutomatica_ShouldReturnOrden() {
        when(proveedorRepository.findAll()).thenReturn(Arrays.asList(proveedor));
        when(ordenCompraRepository.save(any(OrdenCompra.class))).thenAnswer(i -> i.getArguments()[0]);

        OrdenCompra result = ordenCompraService.generarOrdenAutomatica(producto, 50);

        assertNotNull(result);
        assertEquals("PENDIENTE", result.getEstado());
        assertEquals(proveedor, result.getProveedor());
        assertFalse(result.getDetalles().isEmpty());
        assertEquals(50, result.getDetalles().get(0).getCantidad());
    }

    @Test
    void generarOrdenAutomatica_SinProveedores_ShouldThrowException_EdgeCase() {
        when(proveedorRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> ordenCompraService.generarOrdenAutomatica(producto, 50));
    }

    @Test
    void findAll_ShouldReturnList() {
        when(ordenCompraRepository.findAll()).thenReturn(Arrays.asList(ordenCompra));
        List<OrdenCompra> result = ordenCompraService.findAll();
        assertFalse(result.isEmpty());
    }

    @Test
    void findAll_EmptyList_ShouldReturnEmptyList_EdgeCase() {
        when(ordenCompraRepository.findAll()).thenReturn(Collections.emptyList());
        List<OrdenCompra> result = ordenCompraService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnOrden() {
        when(ordenCompraRepository.findById(1L)).thenReturn(Optional.of(ordenCompra));
        OrdenCompra result = ordenCompraService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_NotFound_ShouldThrowException_EdgeCase() {
        when(ordenCompraRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ordenCompraService.findById(99L));
    }

    @Test
    void save_ShouldReturnSavedOrden() {
        when(ordenCompraRepository.save(any(OrdenCompra.class))).thenReturn(ordenCompra);
        OrdenCompra result = ordenCompraService.save(new OrdenCompra());
        assertNotNull(result);
    }
}
