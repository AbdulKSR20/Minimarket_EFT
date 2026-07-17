package com.minimarket.service.impl;

import com.minimarket.entity.Categoria;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.CategoriaRepository;
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
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Lacteos");
    }

    @Test
    void findAll_ShouldReturnList() {
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria));
        List<Categoria> result = categoriaService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findAll_EmptyList_ShouldReturnEmptyList_EdgeCase() {
        when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());
        List<Categoria> result = categoriaService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        Categoria result = categoriaService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_NotFound_ShouldThrowException_EdgeCase() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.findById(99L));
    }

    @Test
    void save_ShouldReturnSavedCategoria() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        Categoria result = categoriaService.save(new Categoria());
        assertNotNull(result);
        assertEquals("Lacteos", result.getNombre());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(categoriaRepository).deleteById(1L);
        categoriaService.deleteById(1L);
        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}
