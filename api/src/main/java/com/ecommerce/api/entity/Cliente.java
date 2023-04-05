package com.ecommerce.api.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad tabla clientes
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name="clientes")
public class Cliente {
  @Id
  @Column(name="id_cliente")
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  @Column(name="documento_cliente", unique = true, nullable = false, length = 20)
  private String documento;
  @Column(name="nombre_cliente", nullable = false, length = 150)
  private String nombre;
  @Column(name="direccion_cliente", nullable = false, length = 255)
  private String direccion;
  @Column(name="telefono_cliente", nullable = false, length = 20)
  private String telefono;
  @OneToMany(fetch = LAZY)
  @JoinColumn(name = "id_cliente_factura")
  List<Factura> facturas;
}
