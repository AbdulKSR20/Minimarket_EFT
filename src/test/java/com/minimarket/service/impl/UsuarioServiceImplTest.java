package com.minimarket.service.impl;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
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
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("juan");
    }

    @Test
    void findAll_ShouldReturnList() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));
        List<Usuario> result = usuarioService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findAll_Empty_ShouldReturnEmptyList_EdgeCase() {
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());
        List<Usuario> result = usuarioService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnOptionalUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Optional<Usuario> result = usuarioService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_NotFound_ShouldReturnEmptyOptional_EdgeCase() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Usuario> result = usuarioService.findById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_ShouldReturnOptionalUsuario() {
        when(usuarioRepository.findByUsername("juan")).thenReturn(Optional.of(usuario));
        Optional<Usuario> result = usuarioService.findByUsername("juan");
        assertTrue(result.isPresent());
        assertEquals("juan", result.get().getUsername());
    }

    @Test
    void findByUsername_NotFound_ShouldReturnEmptyOptional_EdgeCase() {
        when(usuarioRepository.findByUsername("no_existe")).thenReturn(Optional.empty());
        Optional<Usuario> result = usuarioService.findByUsername("no_existe");
        assertFalse(result.isPresent());
    }

    @Test
    void save_ShouldReturnSavedUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        Usuario result = usuarioService.save(new Usuario());
        assertNotNull(result);
        assertEquals("juan", result.getUsername());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(usuarioRepository).deleteById(1L);
        usuarioService.deleteById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
