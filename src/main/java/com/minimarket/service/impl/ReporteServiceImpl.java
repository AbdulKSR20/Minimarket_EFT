package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Override
    public Map<Long, Long> obtenerRotacionDeProductos() {
        List<DetalleVenta> detalles = detalleVentaRepository.findAll();
        
        // Agrupar por producto y sumar las cantidades vendidas
        return detalles.stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProducto().getId(),
                        Collectors.summingLong(DetalleVenta::getCantidad)
                ));
    }
}
