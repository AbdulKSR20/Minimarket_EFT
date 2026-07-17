package com.minimarket.service.impl;

import com.minimarket.entity.Carrito;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.CarritoRepository;
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
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setCantidad(5);
    }

    @Test
    void findAll_ShouldReturnList() {
        when(carritoRepository.findAll()).thenReturn(Arrays.asList(carrito));
        List<Carrito> result = carritoService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findAll_EmptyList_ShouldReturnEmptyList_EdgeCase() {
        when(carritoRepository.findAll()).thenReturn(Collections.emptyList());
        List<Carrito> result = carritoService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnCarrito() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        Carrito result = carritoService.findById(1L);
        assertNotNull(result);
        assertEquals(5, result.getCantidad());
    }

    @Test
    void findById_NotFound_ShouldThrowException_EdgeCase() {
        when(carritoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> carritoService.findById(99L));
    }

    @Test
    void save_ShouldReturnSavedCarrito() {
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);
        Carrito result = carritoService.save(new Carrito());
        assertNotNull(result);
        assertEquals(5, result.getCantidad());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(carritoRepository).deleteById(1L);
        carritoService.deleteById(1L);
        verify(carritoRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByUsuarioId_ShouldReturnList() {
        when(carritoRepository.findByUsuarioId(2L)).thenReturn(Arrays.asList(carrito));
        List<Carrito> result = carritoService.findByUsuarioId(2L);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByUsuarioId_NotFound_ShouldReturnEmptyList_EdgeCase() {
        when(carritoRepository.findByUsuarioId(99L)).thenReturn(Collections.emptyList());
        List<Carrito> result = carritoService.findByUsuarioId(99L);
        assertTrue(result.isEmpty());
    }
}
