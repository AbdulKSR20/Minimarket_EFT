package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.DetalleVentaRepository;
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
class DetalleVentaServiceImplTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private DetalleVentaServiceImpl detalleVentaService;

    private DetalleVenta detalleVenta;

    @BeforeEach
    void setUp() {
        detalleVenta = new DetalleVenta();
        detalleVenta.setId(1L);
        detalleVenta.setCantidad(2);
    }

    @Test
    void findAll_ShouldReturnList() {
        when(detalleVentaRepository.findAll()).thenReturn(Arrays.asList(detalleVenta));
        List<DetalleVenta> result = detalleVentaService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findAll_EmptyList_ShouldReturnEmptyList_EdgeCase() {
        when(detalleVentaRepository.findAll()).thenReturn(Collections.emptyList());
        List<DetalleVenta> result = detalleVentaService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnDetalleVenta() {
        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(detalleVenta));
        DetalleVenta result = detalleVentaService.findById(1L);
        assertNotNull(result);
        assertEquals(2, result.getCantidad());
    }

    @Test
    void findById_NotFound_ShouldThrowException_EdgeCase() {
        when(detalleVentaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> detalleVentaService.findById(99L));
    }

    @Test
    void save_ShouldReturnSavedDetalleVenta() {
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);
        DetalleVenta result = detalleVentaService.save(new DetalleVenta());
        assertNotNull(result);
        assertEquals(2, result.getCantidad());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(detalleVentaRepository).deleteById(1L);
        detalleVentaService.deleteById(1L);
        verify(detalleVentaRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByVentaId_ShouldReturnList() {
        when(detalleVentaRepository.findByVentaId(2L)).thenReturn(Arrays.asList(detalleVenta));
        List<DetalleVenta> result = detalleVentaService.findByVentaId(2L);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByVentaId_NotFound_ShouldReturnEmptyList_EdgeCase() {
        when(detalleVentaRepository.findByVentaId(99L)).thenReturn(Collections.emptyList());
        List<DetalleVenta> result = detalleVentaService.findByVentaId(99L);
        assertTrue(result.isEmpty());
    }
}
