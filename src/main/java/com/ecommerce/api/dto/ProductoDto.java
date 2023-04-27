package com.ecommerce.api.dto;


import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductoDto {
  private String codigo;
  private String descripcion;
  private Double precio;
  private Boolean disponible;
}
