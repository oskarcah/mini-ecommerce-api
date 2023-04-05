package com.ecommerce.api.repository;

import com.ecommerce.api.entity.EstatusFactura;
import com.ecommerce.api.entity.Factura;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
  List<Factura> findByClienteDocumento(Long documento);
  
  @Transactional
  @Modifying
  @Query("UPDATE Factura f SET f.estatus=:estatusFactura WHERE f.id=:numeroFactura")
  int updateEstatusByNumero(Long numeroFactura, EstatusFactura estatusFactura);
  
  @Query("SELECT (count(f.id) > 0) FROM Factura f  WHERE f.cliente.documento=:numeroDocumento")
  boolean clienteTieneFacturas(String numeroDocumento);
  
  @Query("SELECT (count(rf.id) > 0) FROM RenglonFactura rf WHERE rf.producto.sku=:sku")
  boolean productoTieneFacturas(String sku);
}


