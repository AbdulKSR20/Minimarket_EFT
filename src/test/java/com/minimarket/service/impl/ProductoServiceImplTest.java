package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.ProductoRepository;
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
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
    }

    @Test
    void findAll_ShouldReturnList() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto));
        List<Producto> result = productoService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findAll_EmptyList_ShouldReturnEmptyList_EdgeCase() {
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());
        List<Producto> result = productoService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        Producto result = productoService.findById(1L);
        assertNotNull(result);
        assertEquals("Leche", result.getNombre());
    }

    @Test
    void findById_NotFound_ShouldThrowException_EdgeCase() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productoService.findById(99L));
    }

    @Test
    void save_ShouldReturnSavedProducto() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        Producto result = productoService.save(new Producto());
        assertNotNull(result);
        assertEquals("Leche", result.getNombre());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(productoRepository).deleteById(1L);
        productoService.deleteById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByCategoriaId_ShouldReturnList() {
        when(productoRepository.findByCategoriaId(2L)).thenReturn(Arrays.asList(producto));
        List<Producto> result = productoService.findByCategoriaId(2L);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByCategoriaId_NotFound_ShouldReturnEmptyList_EdgeCase() {
        when(productoRepository.findByCategoriaId(99L)).thenReturn(Collections.emptyList());
        List<Producto> result = productoService.findByCategoriaId(99L);
        assertTrue(result.isEmpty());
    }
}
