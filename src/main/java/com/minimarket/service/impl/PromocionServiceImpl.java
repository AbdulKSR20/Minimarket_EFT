package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.entity.Promocion;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.repository.PromocionRepository;
import com.minimarket.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PromocionServiceImpl implements PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Override
    public List<Promocion> findAll() {
        return promocionRepository.findAll();
    }

    @Override
    public Promocion findById(Long id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con id: " + id));
    }

    @Override
    public Promocion save(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    @Override
    public Double calcularPrecioFinal(Producto producto) {
        Promocion promocion = producto.getPromocion();
        if (promocion == null) {
            return producto.getPrecio();
        }

        Date hoy = new Date();
        // Verificar si la promoción está vigente
        if (hoy.after(promocion.getFechaInicio()) && hoy.before(promocion.getFechaFin())) {
            Double descuento = producto.getPrecio() * (promocion.getPorcentajeDescuento() / 100.0);
            return producto.getPrecio() - descuento;
        }

        return producto.getPrecio();
    }
}
