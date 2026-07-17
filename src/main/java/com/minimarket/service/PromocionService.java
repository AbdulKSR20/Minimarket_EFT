package com.minimarket.service;

import com.minimarket.entity.Producto;
import com.minimarket.entity.Promocion;

import java.util.List;

public interface PromocionService {
    List<Promocion> findAll();
    Promocion findById(Long id);
    Promocion save(Promocion promocion);
    
    // Calcula el precio final de un producto considerando las promociones vigentes
    Double calcularPrecioFinal(Producto producto);
}
