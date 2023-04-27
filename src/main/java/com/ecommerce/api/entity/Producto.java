package com.ecommerce.api.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Entidad tabla productos
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name="productos")
public class Producto {
  @Id
  @Column(name="id_producto")
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  @Column(name="sku_producto", unique = true, nullable = false, length = 20)
  private String sku;
  @Column(name="descripcion_producto", nullable = false, length = 150)
  private String descripcion;
  @Column(name="precio_unitario_producto", nullable = false)
  private Double precioUnitario;
  @Column(name="disponible_producto", nullable = false)
  private Boolean disponible;
}
