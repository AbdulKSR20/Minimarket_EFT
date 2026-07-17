package com.minimarket.service.impl;

import com.minimarket.entity.Rol;
import com.minimarket.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceImplTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ROLE_ADMIN");
    }

    @Test
    void findByNombre_ShouldReturnRol() {
        when(rolRepository.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(rol));
        Optional<Rol> result = rolService.findByNombre("ROLE_ADMIN");
        assertTrue(result.isPresent());
        assertEquals("ROLE_ADMIN", result.get().getNombre());
    }

    @Test
    void findByNombre_NotFound_ShouldReturnEmpty_EdgeCase() {
        when(rolRepository.findByNombre("ROLE_NO_EXISTE")).thenReturn(Optional.empty());
        Optional<Rol> result = rolService.findByNombre("ROLE_NO_EXISTE");
        assertFalse(result.isPresent());
    }
}
