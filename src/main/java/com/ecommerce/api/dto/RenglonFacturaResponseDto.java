package com.ecommerce.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RenglonFacturaResponseDto {
  ProductoDto producto;
  int cantidad;
  double precioUnitario;
  double precio;
}
