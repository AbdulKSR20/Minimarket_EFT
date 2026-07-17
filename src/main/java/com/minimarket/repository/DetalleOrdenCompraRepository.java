package com.minimarket.repository;

import com.minimarket.entity.DetalleOrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenCompraRepository extends JpaRepository<DetalleOrdenCompra, Long> {
}
