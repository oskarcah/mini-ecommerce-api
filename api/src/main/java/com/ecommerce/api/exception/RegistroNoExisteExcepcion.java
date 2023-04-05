package com.ecommerce.api.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(NOT_FOUND)
public class RegistroNoExisteExcepcion extends RuntimeException {
  public RegistroNoExisteExcepcion(String mensaje, Exception cause) {
    super(mensaje, cause);
  }
}
