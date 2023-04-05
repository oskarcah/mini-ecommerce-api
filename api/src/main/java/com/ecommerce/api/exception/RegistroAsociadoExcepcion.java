package com.ecommerce.api.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(CONFLICT)
public class RegistroAsociadoExcepcion extends RuntimeException {
  public RegistroAsociadoExcepcion(String mensaje, Exception cause) {
    super(mensaje, cause);
  }
}
