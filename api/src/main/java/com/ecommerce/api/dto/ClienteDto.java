package com.ecommerce.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ClienteDto {
  private String numeroDocumento;
  private String nombre;
  private String direccion;
  private String numeroTelefono;
}
