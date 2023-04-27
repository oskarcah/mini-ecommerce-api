package com.ecommerce.api.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad tabla renglones_facturas
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name="renglones_facturas")
public class RenglonFactura {
  @Id
  @Column(name="id_factura")
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  // relación producto
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "id_producto_renglon_factura", nullable = false)
  private Producto producto;
  @Column(name="precio_unitario_renglon_factura", nullable = false)
  Double precioUnitario;
  @Column(name="cantidad_renglon_factura", nullable = false)
  int cantidad;
}
