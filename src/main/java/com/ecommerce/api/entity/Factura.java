package com.ecommerce.api.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static org.hibernate.annotations.CascadeType.PERSIST;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;


/**
 * Entidad tabla facturas
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "facturas")
public class Factura {
  @Id
  @Column(name = "id_factura")
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  @Column(name = "fecha_factura", nullable = false)
  private LocalDate fecha;
  @Column(name = "estatus_factura", nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private EstatusFactura estatus;
  // relacion con renglon factura
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_factura_renglon_factura")
  @Cascade(PERSIST)
  List<RenglonFactura> renglones = new ArrayList<>();
  // relacion con cliente
  @ManyToOne(fetch = FetchType.LAZY)
  @Cascade(SAVE_UPDATE)
  Cliente cliente;
  
  // cálculo del total de la factura
  @Transient
  public double getTotal() {
    return renglones == null
           ? 0.0D
           : renglones
               .stream()
               .map(r -> r.getPrecioUnitario() * r.getCantidad())
               .reduce(0.0D, Double::sum);
  }
}
