package com.ecommerce.api.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class FalloGeneralExcepcion extends RuntimeException {
  
  public FalloGeneralExcepcion(String mensaje, Exception cause) {
    super(mensaje, cause);
  }
}
