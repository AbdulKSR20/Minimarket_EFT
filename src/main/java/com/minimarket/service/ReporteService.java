package com.minimarket.service;

import java.util.Map;

public interface ReporteService {
    // Retorna un mapa con el ID del producto y la cantidad total vendida
    Map<Long, Long> obtenerRotacionDeProductos();
}
