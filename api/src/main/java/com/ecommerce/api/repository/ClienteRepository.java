package com.ecommerce.api.repository;

import com.ecommerce.api.dto.ClienteDto;
import com.ecommerce.api.entity.Cliente;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  Optional<Cliente> findByDocumento(String documento);
  boolean existsByDocumento(String documento);
  void deleteByDocumento(String documento);
  
  @Transactional
  @Modifying
  @Query("UPDATE Cliente c SET " +
      "c.nombre = :#{#dto.nombre}, " +
      "c.direccion = :#{#dto.direccion}, " +
      "c.telefono = :#{#dto.numeroTelefono} " +
      "WHERE c.documento = :#{#dto.numeroDocumento}")
  int actualizarCliente(@Param("dto") ClienteDto clienteDto);
}
