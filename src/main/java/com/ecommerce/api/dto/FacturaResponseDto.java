package com.ecommerce.api.dto;

import com.ecommerce.api.entity.EstatusFactura;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FacturaResponseDto {
  Long numero;
  String fecha;
  EstatusFactura estatus;
  ClienteDto cliente;
  List<RenglonFacturaResponseDto> detalle;
  Double total;
}
