package com.minimarket.repository;

import com.minimarket.entity.StockSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StockSucursalRepository extends JpaRepository<StockSucursal, Long> {
    Optional<StockSucursal> findByProductoIdAndSucursalId(Long productoId, Long sucursalId);
}
