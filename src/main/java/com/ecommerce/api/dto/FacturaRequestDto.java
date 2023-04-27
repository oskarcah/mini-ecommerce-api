package com.ecommerce.api.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FacturaRequestDto {
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  LocalDate fecha;
  String documentoCliente;
  List<RenglonFacturaRequestDto> detalleProductos;
}
