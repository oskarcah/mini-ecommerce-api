package com.ecommerce.api.repository;

import com.ecommerce.api.dto.ProductoDto;
import com.ecommerce.api.entity.Producto;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
  Optional<Producto> findBySku(String sku);
  boolean existsBySku(String sku);
  void deleteBySku(String sku);
  
  @Transactional
  @Modifying
  @Query("UPDATE Producto p SET " +
      "p.descripcion = :#{#dto.descripcion}, " +
      "p.precioUnitario = :#{#dto.precio}, " +
      "p.disponible = :#{#dto.disponible} " +
      "WHERE p.sku = :#{#dto.codigo}")
  int actualizarProducto(@Param("dto") ProductoDto productoDto);
  
  @Transactional
  @Modifying
  @Query("UPDATE Producto p SET p.disponible=true WHERE p.sku = :sku")
  int activarProducto(String sku);
  
  @Transactional
  @Modifying
  @Query("UPDATE Producto p SET p.disponible=false WHERE p.sku = :sku")
  int desactivarProducto(String sku);
  
  @Query("SELECT p.disponible FROM Producto p WHERE p.sku = :sku")
  boolean estaDisponiblePorSku(String sku);
}

