package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.repository.DetalleVentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void obtenerRotacionDeProductos_ShouldReturnMap() {
        Producto p1 = new Producto();
        p1.setId(1L);
        Producto p2 = new Producto();
        p2.setId(2L);

        DetalleVenta dv1 = new DetalleVenta();
        dv1.setProducto(p1);
        dv1.setCantidad(5);
        DetalleVenta dv2 = new DetalleVenta();
        dv2.setProducto(p1);
        dv2.setCantidad(3);
        DetalleVenta dv3 = new DetalleVenta();
        dv3.setProducto(p2);
        dv3.setCantidad(10);

        when(detalleVentaRepository.findAll()).thenReturn(Arrays.asList(dv1, dv2, dv3));

        Map<Long, Long> result = reporteService.obtenerRotacionDeProductos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(8L, result.get(1L));
        assertEquals(10L, result.get(2L));
    }

    @Test
    void obtenerRotacionDeProductos_EmptyList_ShouldReturnEmptyMap_EdgeCase() {
        when(detalleVentaRepository.findAll()).thenReturn(Collections.emptyList());

        Map<Long, Long> result = reporteService.obtenerRotacionDeProductos();

        assertTrue(result.isEmpty());
    }
}
