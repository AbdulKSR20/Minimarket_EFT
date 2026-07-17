package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.entity.Promocion;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.PromocionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromocionServiceImplTest {

    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private PromocionServiceImpl promocionService;

    private Promocion promocion;
    private Producto producto;

    @BeforeEach
    void setUp() {
        promocion = new Promocion();
        promocion.setId(1L);
        promocion.setPorcentajeDescuento(20.0);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        promocion.setFechaInicio(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, 5);
        promocion.setFechaFin(cal.getTime());

        producto = new Producto();
        producto.setPrecio(100.0);
        producto.setPromocion(promocion);
    }

    @Test
    void findAll_ShouldReturnList() {
        when(promocionRepository.findAll()).thenReturn(Arrays.asList(promocion));
        List<Promocion> result = promocionService.findAll();
        assertFalse(result.isEmpty());
    }

    @Test
    void findAll_EmptyList_ShouldReturnEmptyList_EdgeCase() {
        when(promocionRepository.findAll()).thenReturn(Collections.emptyList());
        List<Promocion> result = promocionService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnPromocion() {
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));
        Promocion result = promocionService.findById(1L);
        assertNotNull(result);
        assertEquals(20.0, result.getPorcentajeDescuento());
    }

    @Test
    void findById_NotFound_ShouldThrowException_EdgeCase() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> promocionService.findById(99L));
    }

    @Test
    void save_ShouldReturnSavedPromocion() {
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocion);
        Promocion result = promocionService.save(new Promocion());
        assertNotNull(result);
        assertEquals(20.0, result.getPorcentajeDescuento());
    }

    @Test
    void calcularPrecioFinal_ConPromocionVigente_ShouldReturnDescuento() {
        Double precioFinal = promocionService.calcularPrecioFinal(producto);
        assertEquals(80.0, precioFinal);
    }

    @Test
    void calcularPrecioFinal_SinPromocion_ShouldReturnPrecioOriginal_EdgeCase() {
        producto.setPromocion(null);
        Double precioFinal = promocionService.calcularPrecioFinal(producto);
        assertEquals(100.0, precioFinal);
    }

    @Test
    void calcularPrecioFinal_PromocionCaducada_ShouldReturnPrecioOriginal_EdgeCase() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -10);
        promocion.setFechaInicio(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 5); // Caducó hace 5 días
        promocion.setFechaFin(cal.getTime());

        Double precioFinal = promocionService.calcularPrecioFinal(producto);
        assertEquals(100.0, precioFinal);
    }
}
